package sznp.virtualcomputer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.virtualbox_5_1.*;

import com.google.common.collect.Lists;

public class PluginMain extends JavaPlugin {
	private IVirtualBox vbox;
	private ISession session;
	private IMachine machine;
	private BukkitTask screenupdatetask;
	private BukkitTask mousetask;

	public static PluginMain Instance;
	public static byte[] allpixels = null; // It's set on each change
	public static ArrayList<IRenderer> renderers = new ArrayList<>();

	// Fired when plugin is first enabled
	@Override
	public void onEnable() {
		Instance = this;
		try {
			ConsoleCommandSender ccs = getServer().getConsoleSender();
			this.getCommand("computer").setExecutor(new Commands());
			ccs.sendMessage("§bInitializing VirtualBox...");
			final String vbpath = System.getProperty("os.name").toLowerCase().contains("mac")
					? "/Applications/VirtualBox.app/Contents/MacOS" : "/opt/virtualbox";
			if (System.getProperty("vbox.home") == null || System.getProperty("vbox.home").isEmpty())
				System.setProperty("vbox.home", vbpath);
			if (System.getProperty("sun.boot.library.path") == null
					|| System.getProperty("sun.boot.library.path").isEmpty())
				System.setProperty("sun.boot.library.path", vbpath);
			addLibraryPath(vbpath);
			final VirtualBoxManager manager = VirtualBoxManager.createInstance(getDataFolder().getAbsolutePath());
			vbox = manager.getVBox();
			session = manager.getSessionObject(); // TODO: Events
			ccs.sendMessage("§bLoading Screen...");
			try {
				for (short i = 0; i < 20; i++)
					renderers.add(new DirectRenderer(i, Bukkit.getWorlds().get(0), i * 128 * 128 * 4)); // TODO: The pixels are selected in a horribly wrong way probably
				ccs.sendMessage("§bUsing Direct Renderer, all good");
			} catch (NoClassDefFoundError e) {
				for (short i = 0; i < 20; i++)
					renderers.add(new BukkitRenderer(i, Bukkit.getWorlds().get(0), i * 128 * 128 * 4));
				ccs.sendMessage("§6Compability error, using slower renderer");
			}
			ccs.sendMessage("§bLoaded!");
			mousetask = getServer().getScheduler().runTaskTimer(this, new MouseLockerPlayerListener(), 0, 0);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {
		ConsoleCommandSender ccs = getServer().getConsoleSender();
		mousetask.cancel();
		if (session.getState() == SessionState.Locked) {
			if (session.getMachine().getState().equals(MachineState.Running)) {
				ccs.sendMessage("§aSaving machine state...");
				session.getMachine().saveState().waitForCompletion(10000);
			}
			session.unlockMachine();
		}
		ccs.sendMessage("§aHuh.");
		saveConfig();
	}

	public void Start(CommandSender sender) {// TODO: Add touchscreen support (#2)
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			sender.sendMessage("§eStarting computer...");
			if (machine == null)
				machine = vbox.getMachines().get(0);
			session.setName("minecraft");
			// machine.launchVMProcess(session, "headless", "").waitForCompletion(10000); - This creates a *process*, we don't want that anymore
			machine.lockMachine(session, LockType.VM); // We want the machine inside *our* process <-- Need the VM type to have console access
			sender.sendMessage("A: " + machine.getState().toString());
			screenupdatetask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
				if (session.getState() != SessionState.Locked) // https://www.virtualbox.org/sdkref/_virtual_box_8idl.html#ac82c179a797c0d7c249d1b98a8e3aa8f
					return; // "This state also occurs as a short transient state during an IMachine::lockMachine call."
				else {
					screenupdatetask.cancel();
					screenupdatetask = null;
				}
				machine = session.getMachine(); // This is the Machine object we can work with
				sender.sendMessage("B: " + machine.getState().toString());
				final IConsole console = session.getConsole();
				sender.sendMessage("1: " + console.getState().toString());
				console.powerUp().waitForCompletion(10000);
				sender.sendMessage("2: " + console.getState().toString());
				console.getDisplay().attachFramebuffer(0L, new IFramebuffer(new MCFrameBuffer(console.getDisplay())));
				sender.sendMessage("3: " + console.getState().toString());
				if (screenupdatetask == null)
					screenupdatetask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
						sender.sendMessage("4: " + console.getState().toString());
						if (session.getState().equals(SessionState.Locked) // Don't run until the machine is running
								&& console.getState().equals(MachineState.Running))
							console.getDisplay().invalidateAndUpdateScreen(0L);
						if (session.getState().equals(SessionState.Unlocked) // Stop if the machine stopped fully
								|| console.getState().equals(MachineState.PoweredOff)) {
							sender.sendMessage("5: " + console.getState().toString());
							if (session.getState().equals(SessionState.Locked)) {
								session.unlockMachine();
								sender.sendMessage("Computer powered off, released it.");
							}
							screenupdatetask.cancel();
							screenupdatetask = null;
						}
					}, 100, 100); // Do a full update every 5 seconds
				sender.sendMessage("§eComputer started.");
			}, 5, 5);
		});
	}

	public static int MouseSpeed = 1;

	public void Stop(CommandSender sender) {
		if (!checkMachineRunning(sender))
			return;
		sender.sendMessage("§eStopping computer...");
		session.getConsole().powerDown().waitForCompletion(2000);
		session.unlockMachine();
		sender.sendMessage("§eComputer stopped.");
	}

	public void PowerButton(CommandSender sender) {
		sender.sendMessage("§ePressing powerbutton...");
		final CommandSender s = sender;
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				if (session.getState() != SessionState.Locked || session.getMachine() == null) {
					Start(sender);
				} else {
					session.getConsole().powerButton();
					s.sendMessage("§ePowerbutton pressed.");
				}
			}
		});
	}

	public void Reset(CommandSender sender) {
		if (!checkMachineRunning(sender))
			return;
		sender.sendMessage("§eResetting computer...");
		session.getConsole().reset();
		sender.sendMessage("§eComputer reset.");
	}

	public void FixScreen(CommandSender sender) {
		if (!checkMachineRunning(sender))
			return;
		sender.sendMessage("§eFixing screen...");
		session.getConsole().getDisplay().setSeamlessMode(false);
		session.getConsole().getDisplay().setVideoModeHint(0L, true, false, 0, 0, 640L, 480L, 32L);
		sender.sendMessage("§eScreen fixed.");
	}

	private boolean checkMachineRunning(CommandSender sender) {
		if (session.getState() != SessionState.Locked || machine.getState() != MachineState.Running) {
			sender.sendMessage("§cMachine isn't running.");
			return false;
		}
		return true;
	}

	public void PressKey(CommandSender sender, String key, String stateorduration) {
		if (!checkMachineRunning(sender))
			return;
		int durationorstate;
		if (stateorduration.length() == 0)
			durationorstate = 0;
		else if (stateorduration.equalsIgnoreCase("down"))
			durationorstate = -1;
		else if (stateorduration.equalsIgnoreCase("up"))
			durationorstate = -2;
		else
			durationorstate = Short.parseShort(stateorduration);
		int code = 0;
		// Release key scan code concept taken from VirtualBox source code (KeyboardImpl.cpp:putCAD())
		// +128
		if (durationorstate != 2)
			session.getConsole().getKeyboard().putScancode(code);
		Runnable sendrelease = () -> session.getConsole().getKeyboard().putScancodes(Lists.newArrayList(code + 128,
				Scancode.sc_controlLeft.Code + 128, Scancode.sc_shiftLeft.Code + 128, Scancode.sc_altLeft.Code + 128));
		if (durationorstate == 0 || durationorstate == -2)
			sendrelease.run();
		if (durationorstate > 0) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(this, sendrelease, durationorstate);
		}
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs, boolean down) {
		if (!checkMachineRunning(sender))
			return;
		int state = 0;
		if (mbs.length() > 0 && down)
			state = Arrays.stream(MouseButtonState.values()).filter(mousebs -> mousebs.name().equalsIgnoreCase(mbs))
					.findAny().orElseThrow(() -> new RuntimeException("Unknown mouse button")).value();
		session.getConsole().getMouse().putMouseEvent(x, y, z, w, state);
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs) {
		if (!checkMachineRunning(sender))
			return;
		UpdateMouse(sender, x, y, z, w, mbs, true);
		UpdateMouse(sender, x, y, z, w, mbs, false);
	}

	/**
	 * Adds the specified path to the java library path
	 *
	 * @param pathToAdd
	 *            the path to add
	 * @throws Exception
	 */
	public static void addLibraryPath(String pathToAdd) throws Exception {
		final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		// get array of paths
		final String[] paths = (String[]) usrPathsField.get(null);

		// check if the path to add is already present
		for (String path : paths) {
			if (path.equals(pathToAdd)) {
				return;
			}
		}

		// add the new path
		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = pathToAdd;
		usrPathsField.set(null, newPaths);
	}
}
