package me.furt.craftworlds.listeners;

import me.furt.craftworlds.CraftWorlds;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class CWPlayerListener extends PlayerListener {
	
	public CWPlayerListener(CraftWorlds instance) {
		// TODO Auto-generated constructor stub
	}

	public void onPlayerChat(PlayerChatEvent event) {
		// TODO onPlayerChat
		Player player = event.getPlayer();
		String format = event.getFormat();
		String world = player.getWorld().getName();
		event.setFormat("[" + ChatColor.GOLD + world  + ChatColor.WHITE + "]" + format);
	}

}
