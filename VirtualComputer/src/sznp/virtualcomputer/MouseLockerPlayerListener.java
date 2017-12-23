package sznp.virtualcomputer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class MouseLockerPlayerListener implements Runnable {
	public static Map<Player, Location> LockedPlayers = new HashMap<>();
	public static float LockedSpeed = 5;

	@Override
	public void run() {
		for (Entry<Player, Location> entry : LockedPlayers.entrySet()) {
			float yaw1 = entry.getValue().getYaw();
			float pitch1 = entry.getValue().getPitch();
			float yaw2 = entry.getKey().getLocation().getYaw();
			float pitch2 = entry.getKey().getLocation().getPitch();
			if (yaw2 - yaw1 == 0 || pitch2 - pitch1 == 0)
				return;

			PluginMain.Instance.UpdateMouse(null, (int) ((yaw2 - yaw1) * LockedSpeed),
					(int) ((pitch2 - pitch1) * LockedSpeed), 0, 0, "");

			entry.getKey().teleport(entry.getValue(), TeleportCause.PLUGIN);
		}
	}
}
