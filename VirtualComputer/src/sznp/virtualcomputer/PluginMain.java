package sznp.virtualcomputer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.HashMap;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.mozilla.interfaces.IFramebufferOverlay;
import org.mozilla.interfaces.nsISupports;
import org.virtualbox_5_1.IFramebuffer;
import org.virtualbox_5_1.ISession;
import org.virtualbox_5_1.IVirtualBox;
import org.virtualbox_5_1.VirtualBoxManager;

import com.mcplugindev.slipswhitley.sketchmap.map.RelativeLocation;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;

public class PluginMain extends JavaPlugin {
	private IVirtualBox vbox;
	private ISession session;
	private SketchMap smap;

	public static PluginMain Instance;

	// Fired when plugin is first enabled
	@Override
	public void onEnable() {
		Instance = this;
		try {
			ConsoleCommandSender ccs = getServer().getConsoleSender();
			this.getCommand("computer").setExecutor(new Commands());
			ccs.sendMessage("§bInitializing VirtualBox...");
			// Connect con = new Connect("vbox:///");
			final VirtualBoxManager manager = VirtualBoxManager.createInstance(getDataFolder().getAbsolutePath());
			vbox = manager.getVBox();
			session = manager.getSessionObject();
			vbox.getMachines().get(0).launchVMProcess(session, "headless", "");
			session.getConsole().getDisplay().attachFramebuffer(0L, new MCFrameBuffer());
			
			ccs.sendMessage("§bLoading SketchMap...");
			img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
			HashMap<Short, RelativeLocation> map = new HashMap<>();
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 4; j++)
					map.put((short) (i * 4 + j), new RelativeLocation(i, j));
			smap = new SketchMap(img, "Screen", 5, 4, false, map);
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

	private volatile BufferedImage img;
	private volatile BukkitTask task = null;

	public void Start(CommandSender sender) {
		sender.sendMessage("§eStarting computer...");
		// computer.Start();
		sender.sendMessage("§eComputer started.");
		DoStart();
	}

	public static int MouseSpeed = 1;

	private void DoStart() {
		if (task == null)
			task = this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
				public void run() {
					final int[] a = ((DataBufferInt) smap.image.getRaster().getDataBuffer()).getData();
					// final int[] data = computer.GetScreenPixelColors();
					// System.arraycopy(data, 0, a, 0, data.length);
				}
			}, 1, 10);
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
		// computer.PowerOff();
		sender.sendMessage("§eComputer stopped.");
	}

	public void PowerButton(CommandSender sender) {
		sender.sendMessage("§eStarting/stoppping computer...");
		final CommandSender s = sender;
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				/*
				 * if (computer.PowerButton()) { DoStart(); s.sendMessage("§eComputer started."); } else s.sendMessage("§ePowerbutton pressed.");
				 */
			}
		});
	}

	public void Reset(CommandSender sender) {
		sender.sendMessage("§eResetting computer...");
		// computer.Reset();
		sender.sendMessage("§eComputer reset.");
	}

	public void FixScreen(CommandSender sender) {
		sender.sendMessage("§eFixing screen...");
		// computer.FixScreen();
		sender.sendMessage("§eScreen fixed.");
	}

	public void PressKey(CommandSender sender, String key, String stateorduration) {
		/*
		 * if (stateorduration.length() == 0) computer.PressKey(key, (short) 0); else if (stateorduration.equalsIgnoreCase("down")) computer.PressKey(key, (short) -1); else if
		 * (stateorduration.equalsIgnoreCase("up")) computer.PressKey(key, (short) -2); else computer.PressKey(key, Short.parseShort(stateorduration));
		 */
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs, boolean down) {
		/*
		 * if (down) computer.UpdateMouse(x, y, z, w, mbs); else computer.UpdateMouse(x, y, z, w, "");
		 */
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs) {
		UpdateMouse(sender, x, y, z, w, mbs, true);
		UpdateMouse(sender, x, y, z, w, mbs, false);
	}
}
