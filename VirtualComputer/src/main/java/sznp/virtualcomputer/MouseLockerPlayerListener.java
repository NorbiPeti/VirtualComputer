package sznp.virtualcomputer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MouseLockerPlayerListener implements Runnable, Listener {
	private static final Map<Player, Location> LockedPlayers = new HashMap<>();
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

			try {
				Computer.getInstance().UpdateMouse(null, (int) ((yaw2 - yaw1) * LockedSpeed),
						(int) ((pitch2 - pitch1) * LockedSpeed), 0, 0, "");
			} catch (Exception e) { //Should not happen
				e.printStackTrace();
			}

			entry.getKey().teleport(entry.getValue(), TeleportCause.PLUGIN);
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		LockedPlayers.remove(event.getPlayer());
	}

	public static void toggleLock(Player player) {
		if (!MouseLockerPlayerListener.LockedPlayers.containsKey(player)) {
			MouseLockerPlayerListener.LockedPlayers.put(player, player.getLocation());
			player.sendMessage("§aMouse locked.");
		} else {
			MouseLockerPlayerListener.LockedPlayers.remove(player);
			player.sendMessage("§bMouse unlocked.");
		}
	}

	public static void computerStop() {
		LockedPlayers.clear();
	}
}
