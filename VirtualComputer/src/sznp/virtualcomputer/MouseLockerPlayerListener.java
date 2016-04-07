package sznp.virtualcomputer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MouseLockerPlayerListener implements Listener
{
	public static List<Player> LockedPlayers = new List<Player>();

	@EventHandler
	public void onPlayerMoveMouse(PlayerMoveEvent e)
	{
		if (!LockedPlayers.contains(e.getPlayer())
			return;
		float yaw1 = e.getFrom().getYaw();
		float pitch1 = e.getFrom().getPitch();
		float yaw2 = e.getTo().getYaw();
		float pitch2 = e.getTo().getPitch();

		PluginMain.Instance.UpdateMouse(null, (int) (yaw2 - yaw1),
				(int) (pitch2 - pitch1), 0, 0, "");

		e.setTo(e.getFrom());
	}
}
