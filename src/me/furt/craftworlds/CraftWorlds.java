package me.furt.craftworlds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.World.Environment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftWorlds extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {
		checkConfig();
		loadWorlds();
		getCommand("world").setExecutor(new WorldCommand(this));
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " v" + pdfFile.getVersion()
				+ " is enabled!");

	}

	private void loadWorlds() {
		// TODO load worlds from config
		this.getServer().createWorld("Utopia", Environment.NORMAL);
	}

	private void checkConfig() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();
		if (!new File(getDataFolder(), "config.yml").exists()) {
			try {
				new File(this.getDataFolder(), "config.yml").createNewFile();
				FileWriter fstream = new FileWriter(new File(getDataFolder(),
						"config.yml"));
				BufferedWriter out = new BufferedWriter(fstream);
				out.close();
				fstream.close();
			} catch (IOException ex) {
				setEnabled(false);
			}
			log.info("[CraftWorlds] config.yml not found, creating.");
		}

	}

}
