package me.furt.craftworlds.commands;

import me.furt.craftworlds.AimBlock;
import me.furt.craftworlds.CraftWorlds;
import me.furt.craftworlds.Teleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherCommand implements CommandExecutor {
	private CraftWorlds plugin;

	public WeatherCommand(CraftWorlds instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (plugin.isPlayer(sender)) {
			if (!CraftWorlds.Permissions.has((Player) sender,
					"craftworlds.weather")) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have proper permissions for that command.");
				return true;
			}
		}

		if (!plugin.isPlayer(sender)) {
			CraftWorlds.log.info("[CraftWorlds] Cannot be used in console.");
			return false;
		}
		Player player = (Player) sender;
		World world = player.getWorld();
		if (args[0].equalsIgnoreCase("thunder")) {
			if (args.length == 2) {
				if (!world.isThundering())
					world.setThundering(true);
				world.setThunderDuration(Integer.parseInt(args[1]));
				return true;
			}
			if (world.isThundering())
				world.setThundering(false);
			return true;
		}

		if (args[0].equalsIgnoreCase("storm")) {
			if (world.hasStorm()) {
				world.setStorm(false);
			} else {
				world.setStorm(true);
			}
			return true;
		}

		if ((args[0].equalsIgnoreCase("duration")) && (args.length == 2)) {
			world.setWeatherDuration(Integer.parseInt(args[1]));
			return true;
		}

		if (args[0].equalsIgnoreCase("strike")) {
			if (!CraftWorlds.Permissions.has((Player) sender,
					"craftworlds.weather.strike")) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have proper permissions for that command.");
				return true;
			}
			if (args.length == 2) {
				Player p = null;
				p = plugin.getServer().getPlayer(args[1]);
				if (p == null) {
					sender.sendMessage("Player not found.");
					return true;
				}
				world.strikeLightning(p.getLocation());
				return true;
			} else {
				AimBlock aiming = new AimBlock(player);
				Block block = aiming.getTargetBlock();
				if (block == null) {
					player.sendMessage(ChatColor.RED
							+ "Not pointing to valid block");
				} else {
					int x = block.getX();
					int y = block.getY() + 1;
					int z = block.getZ();
					Location location = new Location(world, x, y, z, player
							.getLocation().getYaw(), player.getLocation()
							.getPitch());
					Location loc = new Teleport(plugin)
							.getDestination(location);
					world.strikeLightning(loc);
				}
				return true;
			}
		}

		return false;
	}

}
