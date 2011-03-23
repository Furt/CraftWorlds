package me.furt.craftworlds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.furt.craftworlds.commands.WorldCommand;
import me.furt.craftworlds.listeners.CWPlayerListener;
import me.furt.craftworlds.sql.CWConnector;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class CraftWorlds extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");
	public static PermissionHandler Permissions;
	public CWPlayerListener PlayerListener = new CWPlayerListener(this);

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " Disabled");
	}

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.PlayerListener,
				Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.PlayerListener,
				Event.Priority.Monitor, this);
		setupPermissions();
		CWConfig.Load(getConfiguration());
		sqlConnection();
		loadWorlds();
		getCommand("world").setExecutor(new WorldCommand(this));
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " v" + pdfFile.getVersion()
				+ " is enabled!");

	}

	public void sqlConnection() {
		Connection conn = CWConnector.createConnection();

		if (conn == null) {
			log.log(Level.SEVERE,
					"[CraftWorlds] Could not establish SQL connection. Disabling CraftWorlds");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
	
	private void setupPermissions() {
		Plugin test = this.getServer().getPluginManager()
				.getPlugin("Permissions");

		if (CraftWorlds.Permissions == null) {
			if (test != null) {
				CraftWorlds.Permissions = ((Permissions) test).getHandler();
			} else {
				log.info("Permission system not detected, defaulting to OP");
			}
		}
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
				if (rs.getString("enabled").equalsIgnoreCase("true")) {
					if (rs.getString("environment").equalsIgnoreCase("normal")) {
						this.getServer().createWorld(rs.getString("name"),
								Environment.NORMAL);
					} else {
						this.getServer().createWorld(rs.getString("name"),
								Environment.NETHER);
					}
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

	public boolean isPlayer(CommandSender sender) {
		if (!(sender instanceof Player)) {
			return false;
		} else {
			return true;
		}
	}

}
