package sznp.virtualcomputer;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.virtualbox_6_1.*;
import sznp.virtualcomputer.events.MachineEventHandler;
import sznp.virtualcomputer.events.VBoxEventHandler;
import sznp.virtualcomputer.renderer.GPURenderer;
import sznp.virtualcomputer.renderer.MCFrameBuffer;
import sznp.virtualcomputer.util.COMUtils;
import sznp.virtualcomputer.util.Scancode;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class Computer {
	@Getter
	private static Computer instance;

	private final PluginMain plugin;
	private ISession session;
	private IVirtualBox vbox;
	private IMachine machine;
	private MachineEventHandler handler;
	private IEventListener listener;
	private VirtualBoxManager manager;
	private MCFrameBuffer framebuffer;

	@java.beans.ConstructorProperties({"plugin"})
	public Computer(PluginMain plugin, VirtualBoxManager manager, IVirtualBox vbox) {
		this.plugin = plugin;
		this.manager = manager;
		session = manager.getSessionObject();
		this.vbox = vbox;
		if (instance != null) throw new IllegalStateException("A computer already exists!");
		instance = this;
	}

	public void Start(CommandSender sender, int index) {// TODO: Add touchscreen support (#2)
		if (session.getState() == SessionState.Locked) {
			sender.sendMessage("§cThe machine is already running!");
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			if (index < 0 || vbox.getMachines().size() <= index) {
				sendMessage(sender, "§cMachine not found!");
				return;
			}
			try {
				sendMessage(sender, "§eStarting computer...");
				machine = vbox.getMachines().get(index);
				session.setName("minecraft");
				// machine.launchVMProcess(session, "headless", "").waitForCompletion(10000); - This creates a *process*, we don't want that anymore
				synchronized (session) {
					machine.lockMachine(session, LockType.VM); // We want the machine inside *our* process <-- Need the VM type to have console access
				}
				VBoxEventHandler.getInstance().setup(machine.getId(), sender); //TODO: Sometimes null
			} catch (VBoxException e) {
				if (e.getResultCode() == 0x80070005) { //lockMachine: "The object functionality is limited"
					sendMessage(sender, "§6Cannot start computer, the machine may be inaccessible");
					sendMessage(sender, "§6Make sure that the server is running as root (sudo)");
					//TODO: If we have VirtualBox open, it won't close the server's port
					//TODO: "The object in question already exists." on second start
					//machine.launchVMProcess(session, "headless", "").waitForCompletion(10000); //No privileges, start the 'old' way
					//session.getConsole().getDisplay().attachFramebuffer(0L, new IFramebuffer(new COMFrameBuffer(session.getConsole().getDisplay(), false)));
					//sendMessage(sender, "§6Computer started with slower screen. Run as root to use a faster method.");
				} else {
					sendMessage(sender, "§cFailed to start computer: " + e.getMessage());
				}
			}
		});
	}

	public void List(CommandSender sender) {
		val machines = vbox.getMachines();
		for (int i = 0; i < machines.size(); i++) {
			val m = machines.get(i);
			sender.sendMessage("[" + i + "] " + m.getName() + " - " + m.getState());
		}
	}

	/**
	 * Gets called when the machine is locked after {@link #Start(CommandSender, int)}
	 *
	 * @param sender The sender which started the machine
	 */
	public void onLock(CommandSender sender) {
		machine = session.getMachine(); // This is the Machine object we can work with
		final IConsole console = session.getConsole();
		if (handler != null)
			handler.disable();
		handler = new MachineEventHandler(Computer.this, sender);
		listener = handler.registerTo(console.getEventSource());
		IProgress progress = console.powerUp(); // https://marc.info/?l=vbox-dev&m=142780789819967&w=2
		handler.setProgress(progress);
		handler.registerTo(progress.getEventSource()); //TODO: Show progress bar some way?
		val fb = new MCFrameBuffer(console.getDisplay());
		fb.start();
		String fbid = console.getDisplay().attachFramebuffer(0L, COMUtils.gimmeAFramebuffer(fb));
		fb.setId(fbid);
		framebuffer = fb;
	}

	private void sendMessage(@Nullable CommandSender sender, String message) {
		if (sender != null)
			sender.sendMessage(message);
		plugin.getLogger().warning((sender == null ? "" : sender.getName() + ": ") + ChatColor.stripColor(message));
	}

	public void Stop(CommandSender sender) {
		if (checkMachineNotRunning(sender)) {
			if (session.getState().equals(SessionState.Locked)) {
				onMachineStop(sender); //Needed for session reset
				sendMessage(sender, "§eComputer was already off, released it.");
			}
			return;
		}
		sendMessage(sender, "§eStopping computer...");
		synchronized (session) {
			session.getConsole().powerDown();
		}
	}

	public void PowerButton(CommandSender sender, int index) {
		sendMessage(sender, "§ePressing powerbutton...");
		Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				if (session.getState() != SessionState.Locked || session.getMachine() == null) {
					Start(sender, index);
				} else {
					synchronized (session) {
						session.getConsole().powerButton();
					}
					sendMessage(sender, "§ePowerbutton pressed.");
				}
			}
		});
	}

	public void Reset(CommandSender sender) {
		if (checkMachineNotRunning(sender))
			return;
		sendMessage(sender, "§eResetting computer...");
		synchronized (session) {
			session.getConsole().reset();
		}
		sendMessage(sender, "§eComputer reset.");
	}

	public void SaveState(CommandSender sender) {
		if (checkMachineNotRunning(sender))
			return;
		sendMessage(sender, "§eSaving computer state...");
		synchronized (session) {
			session.getMachine().saveState();
		}
	}

	public void FixScreen(CommandSender sender, Boolean seamless) {
		if (checkMachineNotRunning(sender))
			return;
		if (framebuffer == null) {
			sender.sendMessage("§cFramebuffer is null...");
			return;
		}
		val lastUpdated = new Holder<Long>();
		sendMessage(sender, "§eFixing screen...");
		try {
			synchronized (session) {
				if (seamless == null)
					session.getConsole().getDisplay().setVideoModeHint(0L,
							true, false,
							0, 0, 640L, 480L,
							32L, false);
			} //Last param: notify - send PnP notification - stops updates but not changes for some reason
			Bukkit.getScheduler().runTaskLaterAsynchronously(PluginMain.Instance, () -> {
				synchronized (session) {
					sendMessage(sender, "Needs host cursor: " + session.getConsole().getMouse().getNeedsHostCursor());
					session.getConsole().getMouse().putMouseEventAbsolute(-1, -1, 0, 0, 0);
					session.getConsole().getMouse().putMouseEvent(0, 0, 0, 0, 0); //Switch to relative mode
					sendMessage(sender, "§eScreen fixed.");
				}
			}, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		int code = Scancode.getCode("sc_" + key.toLowerCase());
		if (code == -1) {
			sender.sendMessage("§cUnknown key: " + key);
			return;
		}
		// Release key scan code concept taken from VirtualBox source code (KeyboardImpl.cpp:putCAD())
		// +128
		synchronized (session) {
			if (durationorstate != -2)
				session.getConsole().getKeyboard().putScancode(code);
			Runnable sendrelease = () -> session.getConsole().getKeyboard().putScancodes(Lists.newArrayList(code + 128,
					Scancode.sc_controlLeft.Code + 128, Scancode.sc_shiftLeft.Code + 128, Scancode.sc_altLeft.Code + 128));
			if (durationorstate == 0 || durationorstate == -2)
				sendrelease.run();
			if (durationorstate > 0) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, sendrelease, durationorstate * 20);
			}
		}
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs, boolean down) throws Exception {
		if (checkMachineNotRunning(sender))
			return;
		int state = 0;
		if (mbs.length() > 0 && down)
			state = Arrays.stream(MouseButtonState.values()).filter(mousebs -> mousebs.name().equalsIgnoreCase(mbs))
					.findAny().orElseThrow(() -> new Exception("Unknown mouse button")).value();
		synchronized (session) {
			session.getConsole().getMouse().putMouseEvent(x, y, z, w, state);
		}
	}

	public void UpdateMouse(CommandSender sender, int x, int y, int z, int w, String mbs) throws Exception {
		if (checkMachineNotRunning(sender))
			return;
		UpdateMouse(sender, x, y, z, w, mbs, true);
		UpdateMouse(sender, x, y, z, w, mbs, false);
	}

	public void onMachineStart(CommandSender sender) {
		sendMessage(sender, "§eComputer started.");
	}

	public void onMachineStop(CommandSender sender) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			if (session.getState() == SessionState.Locked) {
				synchronized (session) {
					session.unlockMachine(); //Needs to be outside of the event handler
				}
				handler = null;
				machine = null;
				session = manager.getSessionObject();
				sendMessage(sender, "§eComputer powered off."); //This block runs later
			}
		});
		GPURenderer.update(new byte[1], 0, 0, 0, 0, 640, 480); //Black screen
		stopEvents();
		MouseLockerPlayerListener.computerStop();
		if (framebuffer != null)
			framebuffer.stop();
	}

	public void stopEvents() {
        /*if(session!=null && listener!=null)
            session.getConsole().getEventSource().unregisterListener(listener);*/
		if (listener != null)
			handler.disable();
		listener = null;
	}

	public void pluginDisable(CommandSender ccs) {
		stopEvents();
		if (framebuffer != null)
			framebuffer.stop();
		if (session.getState() == SessionState.Locked) {
			if (session.getMachine().getState().equals(MachineState.Running)) {
				ccs.sendMessage("§aSaving machine state...");
				session.getMachine().saveState().waitForCompletion(10000);
			}
			session.unlockMachine();
		}
	}
}
