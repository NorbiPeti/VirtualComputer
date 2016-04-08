package sznp.virtualcomputer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MouseLockerPlayerListener implements Listener
{
	public static List<Player> LockedPlayers = new ArrayList<Player>();
	public static float LockedSpeed = 1;

	@EventHandler
	public void onPlayerMoveMouse(PlayerMoveEvent e)
	{
		if (!LockedPlayers.contains(e.getPlayer()))
			return;
		float yaw1 = e.getFrom().getYaw();
		float pitch1 = e.getFrom().getPitch();
		float yaw2 = e.getTo().getYaw();
		float pitch2 = e.getTo().getPitch();
		if (yaw2 - yaw1 == 0 || pitch2 - pitch1 == 0)
			return;

		PluginMain.Instance.UpdateMouse(null,
				(int) ((yaw2 - yaw1) * LockedSpeed),
				(int) ((pitch2 - pitch1) * LockedSpeed), 0, 0, "");

		e.setTo(e.getFrom());
	}
}
