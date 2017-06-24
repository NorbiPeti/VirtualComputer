package sznp.virtualcomputer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.virtualbox_5_1.*;

import com.google.common.collect.Lists;

public class PluginMain extends JavaPlugin {
	private IVirtualBox vbox;
	private ISession session;
	private ArrayList<IRenderer> renderers = new ArrayList<>();
	private IMachine machine;

	public static PluginMain Instance;
	public static byte[] allpixels = new byte[640 * 480];

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
			session = manager.getSessionObject();
			ccs.sendMessage("§bLoading Screen...");
			try {
				for (short i = 0; i < 20; i++)
					renderers.add(new DirectRenderer(i, Bukkit.getWorlds().get(0), allpixels, i * 128 * 128 * 4)); // TODO: The pixels are selected in a horribly wrong way probably
				ccs.sendMessage("§bUsing Direct Renderer");
			} catch (NoClassDefFoundError e) {
				for (short i = 0; i < 20; i++)
					renderers.add(new BukkitRenderer(i, Bukkit.getWorlds().get(0), allpixels, i * 128 * 128 * 4));
				ccs.sendMessage("§6Compability error, using slower renderer");
			}
			ccs.sendMessage("§bLoaded!");
			getServer().getPluginManager().registerEvents(new MouseLockerPlayerListener(), this);
			DoStart();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {
		ConsoleCommandSender ccs = getServer().getConsoleSender();
		ccs.sendMessage("§aHuh.");
		saveConfig();
	}

	public void Start(CommandSender sender) {
		sender.sendMessage("§eStarting computer...");
		if (machine == null)
			machine = vbox.getMachines().get(0);
		machine.launchVMProcess(session, "headless", "").waitForCompletion(10000);
		session.getConsole().getDisplay().attachFramebuffer(0L, new IFramebuffer(new MCFrameBuffer()));
		sender.sendMessage("§eComputer started.");
		DoStart();
	}

	public static int MouseSpeed = 1;

	private void DoStart() {
		if (getServer().getPluginManager().isPluginEnabled("Movecraft")) {
			this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {
					Craft[] crafts = CraftManager.getInstance().getCraftsInWorld(Bukkit.getWorlds().get(0));
					if (crafts == null)
						return;
					for (Craft c : crafts) {
						if (c.getType().getCraftName().equalsIgnoreCase("mouse")) {
							int dx = c.getLastDX();
							// int dy = c.getLastDY();
							int dz = c.getLastDZ();
							if (Bukkit.getWorlds().get(0).getBlockAt(c.getMinX(), c.getMinY() - 1, c.getMinZ())
									.getType() != Material.AIR && (dx != 0 || dz != 0))
								UpdateMouse(null, dx * MouseSpeed, dz * MouseSpeed, 0, 0, "");
							c.setLastDX(0);
							c.setLastDZ(0);
						}
					}
				}
			}, 1, 1);
		}
	}

	public void Stop(CommandSender sender) {
		sender.sendMessage("§eStopping computer...");
		session.getConsole().powerDown().waitForCompletion(2000);
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
		sender.sendMessage("§eResetting computer...");
		if (session.getState() == SessionState.Locked)
			session.getConsole().powerDown().waitForCompletion(10000);
		sender.sendMessage("§eComputer reset.");
	}

	public void FixScreen(CommandSender sender) {
		sender.sendMessage("§eFixing screen...");
		session.getConsole().getDisplay().setSeamlessMode(false);
		session.getConsole().getDisplay().setVideoModeHint(0L, true, false, 0, 0, 640L, 480L, 32L);
		sender.sendMessage("§eScreen fixed.");
	}

	public void PressKey(CommandSender sender, String key, String stateorduration) {
		if (session.getState() == SessionState.Locked) {
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
			Runnable sendrelease = () -> session.getConsole().getKeyboard()
					.putScancodes(Lists.newArrayList(code + 128, Scancode.sc_controlLeft.Code + 128,
							Scancode.sc_shiftLeft.Code + 128, Scancode.sc_altLeft.Code + 128));
			if (durationorstate == 0 || durationorstate == -2)
				sendrelease.run();
			if (durationorstate > 0) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(this, sendrelease, durationorstate);
			}
		}
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs, boolean down) {
		if (session.getState() != SessionState.Locked)
			return;
		int state = 0;
		if (mbs.length() > 0 && down)
			state = Arrays.stream(MouseButtonState.values()).filter(mousebs -> mousebs.name().equalsIgnoreCase(mbs))
					.findAny().orElseThrow(() -> new RuntimeException("Unknown mouse button")).value();
		session.getConsole().getMouse().putMouseEvent(x, y, z, w, state);
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs) {
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
