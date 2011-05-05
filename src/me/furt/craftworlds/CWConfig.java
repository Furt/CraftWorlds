package me.furt.craftworlds;

import java.util.List;

import org.bukkit.util.config.Configuration;

public class CWConfig {
	public static String test;

	static boolean Load(Configuration config) {
		config.load();
		List<String> keys = config.getKeys(null);
		if (!keys.contains("test"))
			config.setProperty("test", "123");
		if (!config.save()) {
			CraftWorlds.log
					.severe("[CraftWorlds] Error while writing to config.yml");
			return false;
		}

		test = config.getString("test");
		return true;
	}

}
