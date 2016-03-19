package sznp.virtualcomputer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.sf.jni4net.Bridge;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import virtualcomputersender.Computer;

import com.mcplugindev.slipswhitley.sketchmap.map.RelativeLocation;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;

public class PluginMain extends JavaPlugin
{
	private Computer computer;
	private SketchMap smap;

	public static PluginMain Instance;

	// Fired when plugin is first enabled
	@Override
	public void onEnable()
	{
		Instance = this;
		try
		{
			ConsoleCommandSender ccs = getServer().getConsoleSender();
			this.getCommand("computer").setExecutor(new Commands());
			ccs.sendMessage("§bExtracting necessary libraries...");
			final File[] libs = new File[] { // added to class path
			new File(getDataFolder(), "jni4net.j-0.8.8.0.jar"),
					new File(getDataFolder(), "VirtualComputerSender.j4n.jar") };
			final File[] libs2 = new File[] {
					new File(getDataFolder(), "jni4net.n-0.8.8.0.dll"),
					new File(getDataFolder(), "jni4net.n.w64.v40-0.8.8.0.dll") };
			for (final File lib : libs)
			{
				if (!lib.exists())
				{
					JarUtils.extractFromJar(lib.getName(),
							lib.getAbsolutePath());
				}
			}
			for (final File lib : libs2)
			{
				if (!lib.exists())
				{
					JarUtils.extractFromJar(lib.getName(),
							lib.getAbsolutePath());
				}
			}
			for (final File lib : libs)
			{
				if (!lib.exists())
				{
					getLogger().warning(
							"Failed to load plugin! Could not find lib: "
									+ lib.getName());
					Bukkit.getServer().getPluginManager().disablePlugin(this);
					return;
				}
				addClassPath(JarUtils.getJarUrl(lib));
			}
			ccs.sendMessage("§bInitializing bridge...");
			// Bridge.setVerbose(true);
			// Bridge.setDebug(true);
			Bridge.init(new File(getDataFolder(),
					"jni4net.n.w64.v40-0.8.8.0.dll").getAbsoluteFile());
			Bridge.LoadAndRegisterAssemblyFrom(new File(getDataFolder(),
					"VirtualComputerSender.j4n.dll"));
			ccs.sendMessage("§bInititalizing VirtualBox interface...");
			computer = new Computer();
			//ccs.sendMessage("§bLoading ArmorStands...");
			ccs.sendMessage("§bLoading SketchMap...");
			/*
			 * for (ArmorStand as : Bukkit.getWorlds().get(0)
			 * .getEntitiesByClass(ArmorStand.class))
			 * as.remove();
			 */
			/*
			 * World world = Bukkit.getWorlds().get(0);
			 * //armorstands = new ArmorStand[640][];
			 * iframes = new ItemFrame[640][];
			 * for (int i = 0; i < 640; i++)
			 * {
			 * //armorstands[i] = new ArmorStand[480];
			 * iframes[i] = new ItemFrame[480];
			 * for (int j = 0; j < 480; j++)
			 * {
			 * String id = getConfig().getString(i + "." + j);
			 * if (id == null)
			 * {
			 * //armorstands[i][j] = null;
			 * iframes[i][j] = null;
			 * break;
			 * }
			 * UUID uuid = UUID.fromString(id);
			 * for (Entity entity : world.getEntities())
			 * {
			 * if (entity.getUniqueId().equals(uuid))
			 * {
			 * //armorstands[i][j] = (ArmorStand) entity;
			 * iframes[i][j] = (ItemFrame) entity;
			 * break;
			 * }
			 * }
			 * }
			 * }
			 */
			img = new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
			HashMap<Short, RelativeLocation> map = new HashMap<>();
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 4; j++)
					map.put((short) (i * 4 + j), new RelativeLocation(i, j));
			smap = new SketchMap(img, "Screen", 5, 4, false, map);
			ccs.sendMessage("§bLoaded!");
			DoStart();
		} catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	// Fired when plugin is disabled
	@Override
	public void onDisable()
	{
		ConsoleCommandSender ccs = getServer().getConsoleSender();
		//ccs.sendMessage("§aSaving ArmorStands...");
		//ccs.sendMessage("§aSaving Maps...");
		/*
		 * for (int i = 0; i < iframes.length; i++)
		 * {
		 * for (int j = 0; j < iframes[i].length; j++)
		 * {
		 * if (iframes[i][j] == null)
		 * break;
		 * //getConfig().set(i + "." + j, armorstands[i][j].getUniqueId());
		 * getConfig().set(i + "." + j, iframes[i][j].getUniqueId());
		 * }
		 * }
		 */
		ccs.sendMessage("§aHuh.");
		saveConfig();
	}

	//private volatile ArmorStand[][] armorstands;
	//private volatile int ProgressX = 0;
	//private volatile int ProgressY = 0;
	//private volatile ItemFrame[][] iframes;
	private volatile BufferedImage img;
	private volatile int taskid = -1;

	//public volatile byte[][][] Screen;

	public void Start(CommandSender sender)
	{
		sender.sendMessage("§eStarting computer...");
		computer.Start();
		sender.sendMessage("§eComputer started.");
		DoStart();
		/*
		 * this.getServer().getScheduler()
		 * .scheduleSyncRepeatingTask(this, new Runnable()
		 * {
		 * public void run()
		 * {
		 * long worktime = TimeUnit.NANOSECONDS.toMillis(System
		 * .nanoTime());
		 * 
		 * if (ProgressX == 0 && ProgressY == 0)
		 * Screen = computer.GetScreen();
		 * if (Screen == null)
		 * return;
		 * 
		 * for (int i = ProgressX; i < 640; i++)
		 * {
		 * for (int j = ProgressY; j < 480; j++)
		 * {
		 * if (TimeUnit.NANOSECONDS.toMillis(System
		 * .nanoTime()) - worktime > 40)
		 * {
		 * ProgressX = i;
		 * ProgressY = j;
		 * return;
		 * }
		 * 
		 * if (armorstands[i][j] == null)
		 * armorstands[i][j] = (ArmorStand) Bukkit
		 * .getWorlds()
		 * .get(0)
		 * .spawnEntity(
		 * new Location(
		 * Bukkit.getWorlds()
		 * .get(0),
		 * i * 0.1,
		 * 80f - j * 0.1, 0f),
		 * EntityType.ARMOR_STAND);
		 * 
		 * 
		 * World world = Bukkit.getWorlds().get(0);
		 * Location loc = new
		 * Location(Bukkit.getWorlds()
		 * .get(0), i * 0.1, 80f - j * 0.1, 0f);
		 * if (iframes[i][j] == null)
		 * iframes[i][j] = (ItemFrame) world
		 * .spawnEntity(loc,
		 * EntityType.ITEM_FRAME);
		 * 
		 * 
		 * ItemStack stack = new ItemStack(
		 * Material.LEATHER_CHESTPLATE, 1);
		 * ((LeatherArmorMeta) stack.getItemMeta())
		 * .setColor(Color.fromRGB(Byte
		 * .toUnsignedInt(computer
		 * .GetScreenPixelColor(i,
		 * j, 0)), Byte
		 * .toUnsignedInt(computer
		 * .GetScreenPixelColor(i,
		 * j, 1)), Byte
		 * .toUnsignedInt(computer
		 * .GetScreenPixelColor(i,
		 * j, 2))));
		 * armorstands[i][j].setChestplate(stack);
		 * armorstands[i][j].setVisible(false);
		 * 
		 * //iframes[i][j].setItem(); //TO!DO: Copy int array to BufferedImage
		 * in
		 * background thread while rendering
		 * }
		 * }
		 * ProgressX = 0;
		 * ProgressY = 0;
		 * }
		 * }, 1, 1);
		 */
	}

	public static int MouseSpeed = 1;

	@SuppressWarnings("deprecation")
	private void DoStart()
	{
		if (taskid == -1)
			taskid = this.getServer().getScheduler()
					.scheduleAsyncRepeatingTask(this, new Runnable()
					{
						public void run()
						{
							//long time = System.nanoTime();

							final int[] a = ((DataBufferInt) smap.image
									.getRaster().getDataBuffer()).getData();
							final int[] data = computer.GetScreenPixelColors();
							/*
							 * if (data.length > 600)
							 * System.out.println("Updating screen...");
							 */
							System.arraycopy(data, 0, a, 0, data.length);
							/*
							 * if (data.length > 600)
							 * System.out.println("Updated screen.");
							 */

							/*
							 * long diff = System.nanoTime() - time;
							 * if (TimeUnit.NANOSECONDS.toMillis(diff) > 50)
							 * System.out.println("Data copy took "
							 * + TimeUnit.NANOSECONDS.toMillis(diff) + " ms");
							 */
						}
					}, 1, 10);
		this.getServer().getScheduler()
				.scheduleSyncRepeatingTask(this, new Runnable()
				{
					public void run()
					{
						Craft[] crafts = CraftManager.getInstance()
								.getCraftsInWorld(Bukkit.getWorlds().get(0));
						if (crafts == null)
							return;
						for (Craft c : crafts)
						{
							if (c.getType().getCraftName()
									.equalsIgnoreCase("mouse"))
							{
								int dx = c.getLastDX();
								/*
								 * if (dx != 0)
								 * System.out.println(dx);
								 */
								//int dy = c.getLastDY();
								int dz = c.getLastDZ();
								if (Bukkit
										.getWorlds()
										.get(0)
										.getBlockAt(c.getMinX(),
												c.getMinY() - 1, c.getMinZ())
										.getType() != Material.AIR
										&& (dx != 0 || dz != 0))
									UpdateMouse(null, dx * MouseSpeed, dz
											* MouseSpeed, 0, 0, "");
								c.setLastDX(0);
								/*
								 * if (dz != 0)
								 * System.out.println(dz);
								 */
								c.setLastDZ(0);
							}
						}
					}
				}, 1, 1);

		getServer().getPluginManager().registerEvents(
				new MouseLockerPlayerListener(), this);
	}

	public void Stop(CommandSender sender)
	{
		sender.sendMessage("§eStopping computer...");
		computer.PowerOff();
		/*
		 * if (taskid != -1)
		 * {
		 * this.getServer().getScheduler().cancelTask(taskid); run task
		 * constantly
		 * taskid = -1;
		 * }
		 */
		sender.sendMessage("§eComputer stopped.");
	}

	@SuppressWarnings("deprecation")
	public void PowerButton(CommandSender sender)
	{
		sender.sendMessage("§eStarting/stoppping computer...");
		final CommandSender s = sender;
		getServer().getScheduler().scheduleAsyncDelayedTask(this,
				new Runnable()
				{
					@Override
					public void run()
					{
						if (computer.PowerButton())
						{
							DoStart();
							s.sendMessage("§eComputer started.");
						} else
							s.sendMessage("§ePowerbutton pressed.");
					}
				});
	}

	public void Reset(CommandSender sender)
	{
		sender.sendMessage("§eResetting computer...");
		computer.Reset();
		sender.sendMessage("§eComputer reset.");
	}

	public void FixScreen(CommandSender sender)
	{
		sender.sendMessage("§eFixing screen...");
		computer.FixScreen();
		sender.sendMessage("§eScreen fixed.");
	}

	public void PressKey(CommandSender sender, String key,
			String stateorduration)
	{
		//sender.sendMessage("Pressing key...");
		if (stateorduration.length() == 0)
			computer.PressKey(key, (short) 0);
		else if (stateorduration.equalsIgnoreCase("down"))
			computer.PressKey(key, (short) -1);
		else if (stateorduration.equalsIgnoreCase("up"))
			computer.PressKey(key, (short) -2);
		else
			computer.PressKey(key, Short.parseShort(stateorduration));
		//sender.sendMessage("Key pressed.");
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w,
			String mbs, boolean down)
	{
		/*
		 * if (sender != null)
		 * sender.sendMessage("Updating mouse...");
		 */
		if (down)
			computer.UpdateMouse(x, y, z, w, mbs);
		else
			computer.UpdateMouse(x, y, z, w, "");
		/*
		 * if (sender != null)
		 * sender.sendMessage("Updated mouse.");
		 */
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w,
			String mbs)
	{
		UpdateMouse(sender, x, y, z, w, mbs, true);
		UpdateMouse(sender, x, y, z, w, mbs, false);
	}

	/*
	 * public void run() { int aX = wrapped.getInt(); int aY = wrapped.getInt();
	 * int aWidth = wrapped.getInt(); int aHeight = wrapped.getInt(); int asi =
	 * 0;
	 * 
	 * // ByteBuffer dbuf = ByteBuffer.allocate(2); //
	 * dbuf.putShort(num); // byte[] bytes = dbuf.array(); // { 0, 1 } for (int
	 * j = (int) aY; j < aHeight && j < 480; j++) { for (int i = (int) aX; i <
	 * aWidth && i < 640; i++) { int x = wrapped.getInt(); if
	 * (wrapped.remaining() < 4) { runningtask = false; return; } //
	 * FromArgb(255, x2, x1,x) ArmorStand as; if (armorstands[asi] == null) as =
	 * (ArmorStand) Bukkit .getWorlds() .get(0) .spawnEntity( new
	 * Location(Bukkit.getWorlds().get(0), j * 0.1, 80f - i * 0.1, 0f),
	 * EntityType.ARMOR_STAND); else as = armorstands[asi]; ItemStack stack =
	 * new ItemStack(Material.LEATHER_CHESTPLATE, 1); ((LeatherArmorMeta)
	 * stack.getItemMeta()).setColor(Color .fromBGR(x));
	 * as.setChestplate(stack); armorstands[asi++] = as; // x += 4;
	 * wrapped.get(); wrapped.get(); wrapped.get(); wrapped.get(); } for (int k
	 * = 0; k < aX * 4; k++) wrapped.get(); int add = aX + aWidth - 640; if (add
	 * > 0) for (int k = 0; k < add * 4; k++) wrapped.get(); } runningtask =
	 * false; }
	 */

	private void addClassPath(final URL url) throws IOException
	{
		final URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		final Class<URLClassLoader> sysclass = URLClassLoader.class;
		try
		{
			final Method method = sysclass.getDeclaredMethod("addURL",
					new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { url });
		} catch (final Throwable t)
		{
			t.printStackTrace();
			throw new IOException("Error adding " + url
					+ " to system classloader");
		}
	}
}
