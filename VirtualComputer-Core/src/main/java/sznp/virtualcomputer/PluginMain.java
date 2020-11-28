package sznp.virtualcomputer;

import buttondevteam.lib.architecture.ButtonPlugin;
import buttondevteam.lib.architecture.ConfigData;
import jnr.ffi.LibraryLoader;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.virtualbox_6_1.IVirtualBox;
import org.virtualbox_6_1.VirtualBoxManager;
import sznp.virtualcomputer.events.VBoxEventHandler;
import sznp.virtualcomputer.renderer.BukkitRenderer;
import sznp.virtualcomputer.renderer.GPURenderer;
import sznp.virtualcomputer.renderer.IRenderer;
import sznp.virtualcomputer.util.Utils;
import sznp.virtualcomputer.util.VBoxLib;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class PluginMain extends ButtonPlugin {
	public static final int MCX = 5;
	public static final int MCY = 4;
	private BukkitTask mousetask;
	private VBoxEventHandler listener;
	private VirtualBoxManager manager;

	public static PluginMain Instance;
	/**
	 * Only used if {@link #direct} is false.
	 */
	public static ByteBuffer allpixels; // It's set on each change
	private static final ArrayList<IRenderer> renderers = new ArrayList<>();
	public static boolean direct;
	public static boolean sendAll;
	@Getter
	private static boolean pluginEnabled; //The Bukkit plugin has to be enabled for the enable command to work

	/**
	 * The first map ID to use for the screen.
	 * The maps with IDs in the range startID -> startID+19 will be temporarily replaced with the screen.
	 */
	public final ConfigData<Short> startID = getIConfig().getData("startID", (short) 0);
	/**
	 * If true, uses the GPU to accelerate screen rendering. Requires root on Linux.
	 */
	private final ConfigData<Boolean> useGPU = getIConfig().getData("useGPU", true);
	/**
	 * Determines the keyboard layout to use for /c show keyboard. Layouts can be defined in VirtualComputer/layouts/.
	 */
	private final ConfigData<String> kbLayout = getIConfig().getData("kbLayout", "en");
	public File layoutFolder = new File(getDataFolder(), "layouts");
	/**
	 * When set to false, the plugin will not initialize on server startup and its only valid command will be /c plugin enable.
	 * This can be useful to save resources as the plugin keeps the VirtualBox interface running while enabled.
	 */
	private final ConfigData<Boolean> autoEnable = getIConfig().getData("autoEnable", true);

	@Override
	public void pluginEnable() {
		registerCommand(new ComputerCommand());
		Instance = this;
		if (autoEnable.get())
			pluginEnableInternal();
		else
			getLogger().info("Auto-enable is disabled. Enable with /c plugin enable.");
	}

	void pluginEnableInternal() {
		if (pluginEnabled)
			return;
		pluginEnabled = true;
		try {
			ConsoleCommandSender ccs = getServer().getConsoleSender();
			sendAll = getConfig().getBoolean("sendAll", true);
			ccs.sendMessage("§bInitializing VirtualBox...");
			String osname = System.getProperty("os.name").toLowerCase();
			final boolean windows;
			String vbpath;
			{
				boolean win = false;
				vbpath = osname.contains("mac")
						? "/Applications/VirtualBox.app/Contents/MacOS"
						: (win = osname.contains("windows"))
						? "C:\\Program Files\\Oracle\\VirtualBox"
						: "/opt/virtualbox";
				windows = win;
			}
			//noinspection ConstantConditions
			Predicate<File> notGoodDir = ff -> !ff.isDirectory() || (!windows && Arrays.stream(ff.list()).noneMatch(s -> s.contains("xpcom")));
			if (notGoodDir.test(new File(vbpath)))
				vbpath = "/usr/lib/virtualbox";
			if (notGoodDir.test(new File(vbpath)))
				error("Could not find VirtualBox! Download from https://www.virtualbox.org/wiki/Downloads");
			if (System.getProperty("vbox.home") == null || System.getProperty("vbox.home").isEmpty())
				System.setProperty("vbox.home", vbpath);
			if (System.getProperty("sun.boot.library.path") == null
					|| System.getProperty("sun.boot.library.path").isEmpty())
				System.setProperty("sun.boot.library.path", vbpath);
			if (System.getProperty("java.library.path") == null || System.getProperty("java.library.path").isEmpty())
				System.setProperty("java.library.path", vbpath);
			Utils.addLibraryPath(vbpath); //TODO: Jacob DLL must be in the server folder
			final VirtualBoxManager manager = VirtualBoxManager.createInstance(getDataFolder().getAbsolutePath());
			if (!windows) {
				VBoxLib vbl = LibraryLoader.create(VBoxLib.class).load("vboxjxpcom");
				vbl.RTR3InitExe(0, "", 0);
			}
			IVirtualBox vbox = manager.getVBox();
			(listener = new VBoxEventHandler()).registerTo(vbox.getEventSource());
			new Computer(this, manager, vbox); //Saves itself
			this.manager = manager;
			ccs.sendMessage("§bLoading Screen...");
			try {
				if (useGPU.get())
					setupDirectRendering(ccs);
				else
					setupBukkitRendering(ccs);
			} catch (NoClassDefFoundError | Exception e) {
				e.printStackTrace();
				setupBukkitRendering(ccs);
			}
			ccs.sendMessage("§bLoaded!");
			val mlpl = new MouseLockerPlayerListener();
			mousetask = getServer().getScheduler().runTaskTimer(this, mlpl, 0, 0);
			getServer().getPluginManager().registerEvents(mlpl, this);

		} catch (final Exception e) {
			getLogger().severe("A fatal error occured, disabling plugin!");
			Bukkit.getPluginManager().disablePlugin(this);
			throw new RuntimeException(e);
		}
	}

	private void setupDirectRendering(CommandSender ccs) throws Exception {
		for (short i = 0; i < MCX; i++)
			for (short j = 0; j < MCY; j++)
				renderers.add(new GPURenderer((short) (startID.get() + j * MCX + i), Bukkit.getWorlds().get(0), i, j));
		direct = true;
		ccs.sendMessage("§bUsing Direct Renderer, all good");
	}

	private void setupBukkitRendering(CommandSender ccs) {
		for (short i = 0; i < MCX * MCY; i++)
			renderers.add(new BukkitRenderer((short) (startID.get() + i), Bukkit.getWorlds().get(0), i * 128 * 128 * 4));
		direct = false;
		ccs.sendMessage("§6Using Bukkit renderer");
	}

	private void error(String message) {
		getLogger().severe("A fatal error occured, disabling plugin!");
		Bukkit.getPluginManager().disablePlugin(this);
		throw new RuntimeException(message);
	}

	@Override
	public void pluginDisable() {
		pluginDisableInternal();
	}

	void pluginDisableInternal() {
		if (!pluginEnabled)
			return;
		pluginEnabled = false;
		ConsoleCommandSender ccs = getServer().getConsoleSender();
		if (mousetask != null)
			mousetask.cancel();
		/*try {
			source.unregisterListener(listener);
		} catch (VBoxException e) { //"Listener was never registered"
			e.printStackTrace(); - VBox claims the listener was never registered (can double register as well)
		}*/
		if (listener != null)
			listener.disable(); //The save progress wait locks with the event
		if (Computer.getInstance() != null)
			Computer.getInstance().pluginDisable(ccs);
		ccs.sendMessage("§aHuh.");
		saveConfig();
		renderers.clear();
		manager.cleanup();
	}

}
