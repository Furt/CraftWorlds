package me.furt.craftworlds.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import me.furt.craftworlds.CraftWorlds;
import me.furt.craftworlds.sql.CWConnector;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommand implements CommandExecutor {
	CraftWorlds plugin;

	public WorldCommand(CraftWorlds instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length == 0)
			return false;

		if (args[0].equalsIgnoreCase("port")) {
			if (plugin.isPlayer(sender)) {
				if (!CraftWorlds.Permissions.has((Player) sender,
						"craftworlds.port")) {
					sender.sendMessage(ChatColor.YELLOW
							+ "You to dont have proper permissions for that command.");
					return true;
				}
			}
			if (!plugin.isPlayer(sender)) {
				CraftWorlds.log
						.info("[CraftWorlds] Cannot use /world port from console.");
				return true;
			}

			if (args.length == 1)
				return false;

			World world = plugin.getServer().getWorld(args[1]);
			if (world != null) {
				if (plugin.isPlayer(sender)) {
					if (!CraftWorlds.Permissions.has((Player) sender,
							"craftworlds." + world.getName().toLowerCase())) {
						sender.sendMessage(ChatColor.YELLOW
								+ "You to dont have permission to teleport to that world.");
						return true;
					}
				}
				Player player = (Player) sender;
				player.teleportTo(world.getSpawnLocation());
			} else {
				sender.sendMessage("World not found.");
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("list")) {
			if (plugin.isPlayer(sender)) {
				if (!CraftWorlds.Permissions.has((Player) sender,
						"craftworlds.list")) {
					sender.sendMessage(ChatColor.YELLOW
							+ "You to dont have proper permissions for that command.");
					return true;
				}
			}
			sender.sendMessage(ChatColor.YELLOW
					+ "Worlds running on this Server");
			for (int i = 0; i < plugin.getServer().getWorlds().size(); i++) {
				ChatColor color;
				if (((World) plugin.getServer().getWorlds().get(i))
						.getEnvironment() == World.Environment.NETHER)
					color = ChatColor.RED;
				else {
					color = ChatColor.GREEN;
				}
				sender.sendMessage(color
						+ ((World) plugin.getServer().getWorlds().get(i))
								.getName());
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("create")) {
			if (plugin.isPlayer(sender)) {
				if (!CraftWorlds.Permissions.has((Player) sender,
						"craftworlds.create")) {
					sender.sendMessage(ChatColor.YELLOW
							+ "You to dont have proper permissions for that command.");
					return true;
				}
			}
			World world = plugin.getServer().getWorld(args[1]);
			if (world != null) {
				sender.sendMessage(ChatColor.YELLOW + world.getName()
						+ " already exists...");
				return true;
			}
			Environment env;
			if (args.length == 2) {
				env = Environment.NORMAL;
			} else {
				if (args[2].equalsIgnoreCase("nether")) {
					env = Environment.NETHER;
				} else {
					env = Environment.NORMAL;
				}
			}
			plugin.getServer().broadcastMessage(
					ChatColor.YELLOW + "Attempting to create a new world...");
			plugin.getServer().createWorld(args[1], env);
			plugin.getServer().broadcastMessage(
					ChatColor.YELLOW + args[1] + " created!");

			this.addWorld(sender, args[1], env.toString());
			sender.sendMessage(ChatColor.YELLOW + "World is now saved.");
			return true;
		}

		if (args[0].equalsIgnoreCase("disable")) {
			if (plugin.isPlayer(sender)) {
				if (!CraftWorlds.Permissions.has((Player) sender,
						"craftworlds.disable")) {
					sender.sendMessage(ChatColor.YELLOW
							+ "You to dont have proper permissions for that command.");
					return true;
				}
			}
			Connection conn = null;
			Statement stmt = null;
			int count = 0;
			try {
				conn = CWConnector.getConnection();
				stmt = conn.createStatement();
				count += stmt
						.executeUpdate("UPDATE `worlds` SET `enabled` = 'false' WHERE `name` = '"
								+ args[1] + "'");
				sender.sendMessage(args[1]
						+ " is disabled, remember u must restart to finish the process.");
			} catch (SQLException ex) {
				sender.sendMessage("World - " + args[1] + " could not be found.");
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("enable")) {
			if (plugin.isPlayer(sender)) {
				if (!CraftWorlds.Permissions.has((Player) sender,
						"craftworlds.enable")) {
					sender.sendMessage(ChatColor.YELLOW
							+ "You to dont have proper permissions for that command.");
					return true;
				}
			}
			Connection conn = null;
			Statement stmt = null;
			int count = 0;
			try {
				conn = CWConnector.getConnection();
				stmt = conn.createStatement();
				count += stmt
						.executeUpdate("UPDATE `worlds` SET `enabled` = 'true' WHERE `name` = '"
								+ args[1] + "'");
				stmt.close();
				this.loadWorld(args[1]);
				sender.sendMessage(args[1] + " has been enabled.");
			} catch (SQLException ex) {
				sender.sendMessage("World - " + args[1] + " could not be found.");
			}
			return true;
		}
		return false;
	}

	private void loadWorld(String string) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = CWConnector.getConnection();
			ps = conn.prepareStatement("Select * from `worlds` WHERE name = '"
					+ string + "'");
			rs = ps.executeQuery();
			conn.commit();
			while (rs.next()) {
				if (string.equalsIgnoreCase(rs.getString("name"))) {
					if (rs.getString("environment").equalsIgnoreCase("normal")) {
						plugin.getServer().createWorld(rs.getString("name"),
								Environment.NORMAL);
					} else {
						plugin.getServer().createWorld(rs.getString("name"),
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
						"[CraftWorlds]: Found SQL Exception (on close)");
			}
		}

	}

	private void addWorld(CommandSender sender, String string, String string2) {
		Connection conn = null;
		Statement stmt = null;
		int count = 0;
		try {
			conn = CWConnector.getConnection();
			stmt = conn.createStatement();
			count += stmt.executeUpdate("REPLACE INTO `worlds`"
					+ " (`name`, `environment`, `enabled`)" + " VALUES ('"
					+ string + "', '" + string2 + "', 'true')");
			stmt.close();
		} catch (SQLException ex) {
			sender.sendMessage("[CraftWorlds] World did not save but is loaded.");
		}

	}

}
