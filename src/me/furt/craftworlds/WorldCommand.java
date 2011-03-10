package me.furt.craftworlds;

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
			if (args.length == 1)
				return false;
			World world = plugin.getServer().getWorld(args[1]);
			if (world != null) {
				Player player = (Player) sender;
				player.teleportTo(world.getSpawnLocation());
			} else {
				sender.sendMessage("World not found.");
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("list")) {
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

			// TODO add config save function
			return true;
		}
		return false;
	}

}
