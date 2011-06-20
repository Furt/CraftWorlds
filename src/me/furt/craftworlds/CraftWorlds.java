package me.furt.craftworlds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import me.furt.craftworlds.commands.WorldCommand;
import me.furt.craftworlds.listeners.CWPlayerListener;
import me.furt.craftworlds.sql.WorldTable;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
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
	public boolean permEnabled;

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
		this.setupPermissions();
		this.setupDatabase();
		this.loadWorlds();
		getCommand("world").setExecutor(new WorldCommand(this));
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(pdfFile.getName() + " v" + pdfFile.getVersion()
				+ " is enabled!");

	}

	private void setupPermissions() {
		Plugin test = this.getServer().getPluginManager()
				.getPlugin("Permissions");

		if (CraftWorlds.Permissions == null) {
			if (test != null) {
				CraftWorlds.Permissions = ((Permissions) test).getHandler();
				this.permEnabled = true;
			} else {
				log.info("Permission plugin not detected, using internal permissions.");
				this.permEnabled = false;
			}
		}
	}

	private void loadWorlds() {
		List<WorldTable> getWorld = getDatabase().find(WorldTable.class)
				.where().eq("worldEnabled", true).findList();
		int count = getWorld.size();
		for (int i = 0; i < count; i++) {
			String wName = getWorld.get(i).getWorldName();
			String env = getWorld.get(i).getEnvironment();
			long seed = getWorld.get(i).getSeed();
			boolean pvp = getWorld.get(i).isPvpEnabled();
			if (seed == 0) {
				this.getServer().createWorld(wName, Environment.valueOf(env));
			} else {
				this.getServer().createWorld(wName, Environment.valueOf(env),
						seed);
			}
			World world = this.getServer().getWorld(wName);
			world.setPVP(pvp);
			this.getServer().broadcastMessage(
					"[CraftWorlds] Map: " + wName + " loaded.");
		}

	}

	private void setupDatabase() {
		try {
			File ebeans = new File("ebean.properties");
			if (!ebeans.exists()) {
				try {
					ebeans.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			getDatabase().find(WorldTable.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("[CraftWorld] Installing database.");
			installDDL();
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(WorldTable.class);
		return list;
	}

	public boolean isPlayer(CommandSender sender) {
		if (!(sender instanceof Player)) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean hasPerm(CommandSender sender, Command cmd) {
		if (this.permEnabled) {
			if ((!sender.isOp()) && (sender instanceof Player)) {
				Player p = (Player) sender;
				return Permissions.has(p, "craftworld." + cmd);
			}
		}
		return true;
	}

}
