package me.furt.craftworlds.commands;

import me.furt.craftworlds.CraftWorlds;
import me.furt.craftworlds.Teleport;
import me.furt.craftworlds.sql.WorldTable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
				Location loc = new Teleport(plugin).getDestination(world
						.getSpawnLocation());
				player.teleport(loc);
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

			if (worldExists(args[1])) {
				sender.sendMessage(ChatColor.YELLOW + args[1]
						+ " already exists...");
				return true;
			}
			Environment env = Environment.NORMAL;
			long seed = 0;
			if (args.length > 2) {
				if (args[2].equalsIgnoreCase("nether")) {
					env = Environment.NETHER;
				}

				if (args[2].equalsIgnoreCase("seed")) {
					String string = this.stringBuilder(args);
					String[] seedSplit = string.split("seed");
					seed = Long.parseLong(seedSplit[1].replace(" ", ""), 36);
				}

				if ((args.length > 3) && (args[3].equalsIgnoreCase("seed"))) {
					String string = this.stringBuilder(args);
					String[] seedSplit = string.split("seed");
					seed = Long.parseLong(seedSplit[1].replace(" ", ""), 36);
				}

			}
			plugin.getServer().broadcastMessage(
					ChatColor.YELLOW + "Attempting to create a new world...");
			if (seed == 0) {
				plugin.getServer().createWorld(args[1], env);
			} else {
				plugin.getServer().createWorld(args[1], env, seed);
			}

			WorldTable wt = new WorldTable();
			wt.setWorldName(args[1]);
			wt.setEnvironment(env.name());
			wt.setSeed(seed);
			wt.setPvpEnabled(true);
			wt.setWorldEnabled(true);
			plugin.getDatabase().save(wt);

			plugin.getServer().broadcastMessage(
					ChatColor.YELLOW + args[1] + " created!");
			return true;
		}

		if (args[0].equalsIgnoreCase("delete")) {
			if (plugin.isPlayer(sender)) {
				if (!CraftWorlds.Permissions.has((Player) sender,
						"craftworlds.delete")) {
					sender.sendMessage(ChatColor.YELLOW
							+ "You to dont have proper permissions for that command.");
					return true;
				}
			}

			WorldTable wt = plugin.getDatabase().find(WorldTable.class).where()
					.ieq("worldName", args[1]).findUnique();
			if (wt != null) {
				plugin.getDatabase().delete(wt);
				sender.sendMessage(ChatColor.YELLOW + args[1]
						+ " deleted, please restart to finish the proccess.");
			} else {
				sender.sendMessage(ChatColor.YELLOW + args[1]
						+ " not found, did you spell it correctly?");
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("set")) {
			if (plugin.isPlayer(sender)) {
				if (!CraftWorlds.Permissions.has((Player) sender,
						"craftworlds.set")) {
					sender.sendMessage(ChatColor.YELLOW
							+ "You to dont have proper permissions for that command.");
					return true;
				}
			}
			World world = plugin.getServer().getWorld(args[1]);
			if (world == null) {
				sender.sendMessage(ChatColor.YELLOW + args[1]
						+ " not found, did you spell it correctly?");
				return true;
			}

			if (args[2].equalsIgnoreCase("toggle")) {
				WorldTable wt = plugin.getDatabase().find(WorldTable.class)
						.where().ieq("worldName", world.getName()).findUnique();
				if (wt == null) {
					sender.sendMessage("We got a problem!");
					return true;
				}
				if (wt.isWorldEnabled()) {
					wt.setWorldEnabled(false);
					sender.sendMessage(ChatColor.YELLOW
							+ world.getName()
							+ " is now disabled, please restart to finish the proccess.");
				} else {
					wt.setWorldEnabled(true);
					sender.sendMessage(ChatColor.YELLOW
							+ world.getName()
							+ " is now enabled, please restart to finish the proccess.");
				}
				plugin.getDatabase().save(wt);
				return true;
			}

			if (args[2].equalsIgnoreCase("pvp")) {
				WorldTable wt = plugin.getDatabase().find(WorldTable.class)
						.where().ieq("worldName", world.getName()).findUnique();
				if (wt == null) {
					sender.sendMessage("We got a problem!");
					return true;
				}
				if (wt.isPvpEnabled()) {
					wt.setPvpEnabled(false);
					world.setPVP(false);
					plugin.getServer().broadcastMessage(
							ChatColor.YELLOW + "PVP is now disabled on "
									+ world.getName());
				} else {
					wt.setPvpEnabled(true);
					world.setPVP(true);
					plugin.getServer().broadcastMessage(
							ChatColor.YELLOW + "PVP is now enabled on "
									+ world.getName());
				}
				plugin.getDatabase().save(wt);
			}
			return true;
		}
		return false;
	}

	private boolean worldExists(String name) {
		WorldTable getWorld = plugin.getDatabase().find(WorldTable.class)
				.where().ieq("worldName", name).findUnique();
		if (getWorld == null)
			return false;

		return true;
	}

	private String stringBuilder(String[] string) {
		StringBuilder list = new StringBuilder();
		for (String loop : string) {
			list.append(loop + " ");
		}
		return list.toString();

	}

}
