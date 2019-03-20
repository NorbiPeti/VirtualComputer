package sznp.virtualcomputer;

import jnr.ffi.LibraryLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.virtualbox_6_0.*;
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
	/*
	 * Only used if {@link #direct} is true.
	 */
	//public static PXCLib pxc;
	public static boolean direct;

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
			Utils.addLibraryPath(vbpath);
			final VirtualBoxManager manager = VirtualBoxManager.createInstance(getDataFolder().getAbsolutePath());
			VBoxLib vbl = LibraryLoader.create(VBoxLib.class).load("vboxjxpcom");
			vbl.RTR3InitExe(0, "", 0);
			vbox = manager.getVBox();
			new VBoxEventHandler().registerTo(vbox.getEventSource());
			session = manager.getSessionObject();
			new Computer(this, session, vbox); //Saves itself
			ccs.sendMessage("§bLoading Screen...");
			try {
				//throw new NoClassDefFoundError("Test error pls ignore");
				for (short i = 0; i < MCX; i++)
					for (short j = 0; j < MCY; j++)
						renderers.add(new GPURenderer((short) (j * 5 + i), Bukkit.getWorlds().get(0), i, j));
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

}
