package sznp.virtualcomputer.renderer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

public interface IRenderer {
	static MapView prepare(short id, World world) {
		if (world == null) return null; //Testing
		@SuppressWarnings("deprecation")
		MapView map = Bukkit.getMap(id);
		if (map == null)
			map = Bukkit.createMap(world);
		map.getRenderers().clear();
		return map;
	}
}
