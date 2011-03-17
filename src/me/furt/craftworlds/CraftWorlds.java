package me.furt.craftworlds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.furt.craftworlds.commands.WorldCommand;
import me.furt.craftworlds.sql.CWConnector;

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
		CWConfig.Load(getConfiguration());
		checkConfig();
		loadWorlds();
		getCommand("world").setExecutor(new WorldCommand(this));
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " v" + pdfFile.getVersion()
				+ " is enabled!");

	}

	private void loadWorlds() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CWConnector.getConnection();
			ps = conn.prepareStatement("Select * from `worlds`");
			rs = ps.executeQuery();
			conn.commit();
			while (rs.next()) {
				if (rs.getString("environment").equalsIgnoreCase("normal")) {
					this.getServer().createWorld(rs.getString("world"),
							Environment.NORMAL);
				} else {
					this.getServer().createWorld(rs.getString("world"),
							Environment.NETHER);
				}

			}
		} catch (SQLException ex) {
			CraftWorlds.log.log(Level.SEVERE,
					"[CraftWorlds]: Find SQL Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				CraftWorlds.log.log(Level.SEVERE,
						"[CraftWorlds]: Find SQL Exception (on close)");
			}
		}
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
		} else {
		}

	}

}
