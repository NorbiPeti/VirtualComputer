package sznp.virtualcomputer.events;

import com.google.common.collect.ImmutableMap;
import lombok.Setter;
import lombok.experimental.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.virtualbox_6_0.IProgress;
import org.virtualbox_6_0.IStateChangedEvent;
import org.virtualbox_6_0.VBoxEventType;
import sznp.virtualcomputer.Computer;
import sznp.virtualcomputer.PluginMain;

import java.util.logging.Logger;

public class MachineEventHandler extends EventHandlerBase {
	private final Computer computer;
	private final CommandSender sender;
	private boolean starting = false;
	@Setter
	private IProgress progress;

	public MachineEventHandler(Computer computer, CommandSender sender) {
		super(ImmutableMap.of(VBoxEventType.OnStateChanged, IStateChangedEvent.class));
		//VBoxEventType.OnProgressPercentageChanged, IProgressPercentageChangedEvent.class)); - Doesn't fire at all, the complete event only fires on success
		this.computer = computer;
		this.sender = sender;
	}

	@EventHandler
	public void handleStateChange(IStateChangedEvent event) { //https://www.virtualbox.org/sdkref/_virtual_box_8idl.html#a80b08f71210afe16038e904a656ed9eb
		switch (event.getState()) {
			case Stuck:
				computer.Stop(null);
				break;
			case PoweredOff:
			case Saved:
				if (starting) {
					sender.sendMessage("§cFailed to start computer! See the VM's log for more details.");
					sender.sendMessage("§cMake sure that 2D and 3D acceleration is disabled.");
					starting = false;
					Bukkit.getScheduler().runTaskAsynchronously(PluginMain.Instance, () -> {
						progress.waitForCompletion(-1);
						if (progress != null && progress.getCompleted() && progress.getResultCode() != 0) {
							Logger l = PluginMain.Instance.getLogger();
							l.warning("Result code: " + Integer.toHexString(progress.getResultCode()));
							for (var info = progress.getErrorInfo(); info != null; info = info.getNext()) {
								l.warning("----------------");
								l.warning("VBox: " + info.getText());
								l.warning("Component: " + info.getComponent());
							}
						}
					});
				}
				computer.onMachineStop(sender);
				break;
			case Starting:
				starting = true;
				break;
			case Running:
				starting = false;
				computer.onMachineStart(sender);
				break;
		}
	}
}
