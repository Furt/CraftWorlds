package me.furt.craftworlds;

import java.util.List;

import org.bukkit.util.config.Configuration;

public class CWConfig {
	public static String sqlUrl;
	public static String sqlUsername;
	public static String sqlPassword;

	static boolean Load(Configuration config) {
		config.load();
		List<String> keys = config.getKeys(null);
		if (!keys.contains("url"))
			config.setProperty("url", "jdbc:mysql://localhost:3306/CraftBukkit");
		if (!keys.contains("username"))
			config.setProperty("username", "user");
		if (!keys.contains("password"))
			config.setProperty("password", "pass");
		if (!config.save()) {
			CraftWorlds.log
					.severe("[CraftWorlds] Error while writing to config.yml");
			return false;
		}

		sqlUrl = config.getString("url");
		sqlUsername = config.getString("username");
		sqlPassword = config.getString("password");
		return true;
	}

}
