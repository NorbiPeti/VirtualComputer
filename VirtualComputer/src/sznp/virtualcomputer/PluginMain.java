package sznp.virtualcomputer;

import com.google.common.collect.Lists;
import jnr.ffi.LibraryLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.virtualbox_6_0.*;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class PluginMain extends JavaPlugin {
	public static final int MCX = 5;
	public static final int MCY = 4;
	private IVirtualBox vbox;
	private ISession session;
	private IMachine machine;
	private BukkitTask screenupdatetask;
	private BukkitTask mousetask;

	public static PluginMain Instance;
	//public static ByteBuffer allpixels = ByteBuffer.allocate(640 * 480 * 4); // It's set on each change
	/**
	 * Only used if {@link #direct} is false.
	 */
	public static ByteBuffer allpixels; // It's set on each change
	public static ArrayList<IRenderer> renderers = new ArrayList<>();
	/**
	 * Only used if {@link #direct} is true.
	 */
	//public static PXCLib pxc;
	public static boolean direct;
	/**
	 * Only used if {@link #direct} is true.
	 */
	public static byte[] pixels;

	// Fired when plugin is first enabled
	@Override
	public void onEnable() {
		Instance = this;
		try {
			ConsoleCommandSender ccs = getServer().getConsoleSender();
			this.getCommand("computer").setExecutor(new Commands());
			ccs.sendMessage("§bInitializing VirtualBox...");
			String vbpath = System.getProperty("os.name").toLowerCase().contains("mac")
					? "/Applications/VirtualBox.app/Contents/MacOS"
					: "/opt/virtualbox";
			//noinspection ConstantConditions
			Predicate<File> notGoodDir= ff->!ff.isDirectory() || Arrays.stream(ff.list()).noneMatch(s -> s.contains("xpcom"));
			if (notGoodDir.test(new File(vbpath)))
				vbpath = "/usr/lib/virtualbox";
			if(notGoodDir.test(new File(vbpath)))
				error("Could not find VirtualBox! Download from https://www.virtualbox.org/wiki/Downloads");
			if (System.getProperty("vbox.home") == null || System.getProperty("vbox.home").isEmpty())
				System.setProperty("vbox.home", vbpath);
			if (System.getProperty("sun.boot.library.path") == null
					|| System.getProperty("sun.boot.library.path").isEmpty())
				System.setProperty("sun.boot.library.path", vbpath);
			if (System.getProperty("java.library.path") == null || System.getProperty("java.library.path").isEmpty())
				System.setProperty("java.library.path", vbpath);
			addLibraryPath(vbpath);
			final VirtualBoxManager manager = VirtualBoxManager.createInstance(getDataFolder().getAbsolutePath());
			VBoxLib vbl = LibraryLoader.create(VBoxLib.class).load("vboxjxpcom");
			vbl.RTR3InitExe(0, "", 0);
			vbox = manager.getVBox();
			session = manager.getSessionObject(); // TODO: Events
			ccs.sendMessage("§bLoading Screen...");
			try {
				//throw new NoClassDefFoundError("Test error pls ignore");
				for (short i = 0; i < 5; i++)
					for (short j = 0; j < 4; j++)
						renderers.add(new GPURenderer(i, Bukkit.getWorlds().get(0), i, j));
				//pxc = LibraryLoader.create(PXCLib.class).search(getDataFolder().getAbsolutePath()).load("pxc");
				direct=true;
				ccs.sendMessage("§bUsing Direct Renderer, all good");
			} catch (NoClassDefFoundError | Exception e) {
				for (short i = 0; i < 20; i++)
					renderers.add(new BukkitRenderer(i, Bukkit.getWorlds().get(0), i * 128 * 128 * 4));
				direct=false;
				e.printStackTrace();
				ccs.sendMessage("§6Compatibility error, using slower renderer");
			}
			ccs.sendMessage("§bLoaded!");
			mousetask = getServer().getScheduler().runTaskTimer(this, new MouseLockerPlayerListener(), 0, 0);

		} catch (final Exception e) {
			e.printStackTrace();
			error(e.getMessage());
		}
	}

	private void error(String message) {
		getLogger().severe("A fatal error occured, disabling plugin!");
		Bukkit.getPluginManager().disablePlugin(this);
		throw new RuntimeException(message);
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
			try {
				sendMessage(sender, "§eStarting computer...");
				if (machine == null)
					machine = vbox.getMachines().get(0);
				session.setName("minecraft");
				// machine.launchVMProcess(session, "headless", "").waitForCompletion(10000); - This creates a *process*, we don't want that anymore
				machine.lockMachine(session, LockType.VM); // We want the machine inside *our* process <-- Need the VM type to have console access
				final Runnable tr = new Runnable() {
					public void run() {
						if (session.getState() != SessionState.Locked) { // https://www.virtualbox.org/sdkref/_virtual_box_8idl.html#ac82c179a797c0d7c249d1b98a8e3aa8f
							Bukkit.getScheduler().runTaskLaterAsynchronously(PluginMain.this, this, 5);
							return; // "This state also occurs as a short transient state during an IMachine::lockMachine call."
						}
						machine = session.getMachine(); // This is the Machine object we can work with
						final IConsole console = session.getConsole();
						console.powerUp(); // https://marc.info/?l=vbox-dev&m=142780789819967&w=2
						console.getDisplay().attachFramebuffer(0L,
								new IFramebuffer(new MCFrameBuffer(console.getDisplay(), true)));
						startScreenTask(console, sender);
					}
				};
				Bukkit.getScheduler().runTaskLaterAsynchronously(this, tr, 5);
			} catch (VBoxException e) {
				if (e.getResultCode() == 0x80070005) { //lockMachine: "The object functionality is limited"
					sendMessage(sender, "§6Cannot start computer, the machine may be inaccessible");
					return; //TODO: If we have VirtualBox open, it won't close the server's port
					//TODO: Can't detect if machine fails to start because of hardening issues
					//TODO: This error also occurs if the machine has failed to start at least once (always reassign the machine?)
					//machine.launchVMProcess(session, "headless", "").waitForCompletion(10000); //No privileges, start the 'old' way
					//session.getConsole().getDisplay().attachFramebuffer(0L, new IFramebuffer(new MCFrameBuffer(session.getConsole().getDisplay(), false)));
					//sendMessage(sender, "§6Computer started with slower screen. Run as root to use a faster method.");
				} else {
					sendMessage(sender, "§cFailed to start computer: " + e.getMessage());
					return;
				}
			}
			sendMessage(sender, "§eComputer started.");
		});
	}

	private void startScreenTask(IConsole console, CommandSender sender) {
		if (screenupdatetask == null)
			screenupdatetask = Bukkit.getScheduler().runTaskTimerAsynchronously(PluginMain.this, () -> {
				if (session.getState().equals(SessionState.Locked) // Don't run until the machine is running
						&& console.getState().equals(MachineState.Running))
					console.getDisplay().invalidateAndUpdateScreen(0L);
				if (session.getState().equals(SessionState.Unlocked) // Stop if the machine stopped fully
						|| console.getState().equals(MachineState.PoweredOff)
						|| console.getState().equals(MachineState.Saved)) {
					if (session.getState().equals(SessionState.Locked)) {
						session.unlockMachine();
						sendMessage(sender, "Computer powered off, released it.");
					}
					screenupdatetask.cancel();
					screenupdatetask = null;
				}
			}, 100, 100); // Do a full update every 5 seconds
	}

	private void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(message);
		getLogger().warning(sender.getName() + ": " + ChatColor.stripColor(message));
	}

	public void Stop(CommandSender sender) {
		if (checkMachineNotRunning(sender)) {
			if (session.getState().equals(SessionState.Locked)) {
				session.unlockMachine();
				sendMessage(sender, "§eComputer powered off, released it.");
			}
			return;
		}
		sendMessage(sender, "§eStopping computer...");
		session.getConsole().powerDown().waitForCompletion(2000);
		session.unlockMachine();
		sendMessage(sender, "§eComputer stopped.");
	}

	public void PowerButton(CommandSender sender) {
		sendMessage(sender, "§ePressing powerbutton...");
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				if (session.getState() != SessionState.Locked || session.getMachine() == null) {
					Start(sender);
				} else {
					session.getConsole().powerButton();
					sendMessage(sender, "§ePowerbutton pressed.");
				}
			}
		});
	}

	public void Reset(CommandSender sender) {
		if (checkMachineNotRunning(sender))
			return;
		sendMessage(sender, "§eResetting computer...");
		session.getConsole().reset();
		sendMessage(sender, "§eComputer reset.");
	}

	public void FixScreen(CommandSender sender) {
		if (checkMachineNotRunning(sender))
			return;
		sendMessage(sender, "§eFixing screen...");
		session.getConsole().getDisplay().setSeamlessMode(false);
		session.getConsole().getDisplay().setVideoModeHint(0L, true, false, 0, 0, 640L, 480L, 32L);
		sendMessage(sender, "§eScreen fixed.");
	}

	public boolean checkMachineNotRunning(@Nullable CommandSender sender) {
		if (session.getState() != SessionState.Locked || machine.getState() != MachineState.Running) {
			if (sender != null)
				sender.sendMessage("§cMachine isn't running.");
			return true;
		}
		return false;
	}

	public void PressKey(CommandSender sender, String key, String stateorduration) {
		if (checkMachineNotRunning(sender))
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
		int code;
		try {
			code = Scancode.valueOf("sc_" + key.toLowerCase()).Code;
		} catch (IllegalArgumentException e) {
			sender.sendMessage("§cUnknown key: " + key);
			return;
		}
		// Release key scan code concept taken from VirtualBox source code (KeyboardImpl.cpp:putCAD())
		// +128
		if (durationorstate != -2)
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
		if (checkMachineNotRunning(sender))
			return;
		int state = 0;
		if (mbs.length() > 0 && down)
			state = Arrays.stream(MouseButtonState.values()).filter(mousebs -> mousebs.name().equalsIgnoreCase(mbs))
					.findAny().orElseThrow(() -> new RuntimeException("Unknown mouse button")).value();
		session.getConsole().getMouse().putMouseEvent(x, y, z, w, state);
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs) {
		if (checkMachineNotRunning(sender))
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
