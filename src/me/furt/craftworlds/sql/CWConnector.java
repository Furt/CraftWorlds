package me.furt.craftworlds.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.furt.craftworlds.CWConfig;

public class CWConnector {
	public static ResultSet result;

	public static Connection getConnection() {
		try {
			Connection conn = DriverManager.getConnection("jdbc:jdc:jdcpool");
			conn.setAutoCommit(false);
			return conn;
		} catch (SQLException e) {
			Logger.getLogger("Minecraft").log(Level.SEVERE,
					"[CraftEssence] Error getting connection", e);
			e.printStackTrace();
			return null;
		}
	}

	public static Connection createConnection() {
		try {
			new JDCConnectionDriver("com.mysql.jdbc.Driver",
					CWConfig.sqlUrl, CWConfig.sqlUsername,
					CWConfig.sqlPassword);
			Connection ret = DriverManager.getConnection("jdbc:jdc:jdcpool");
			ret.setAutoCommit(false);
			return ret;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
