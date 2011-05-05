package me.furt.craftworlds.sql;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "cw_world")
public class WorldTable {
	@Id
	private int id;

	@NotNull
	private String worldName;

	@NotNull
	private String environment;

	@NotNull
	private long seed;

	@NotNull
	private boolean pvpEnabled;

	@NotNull
	private boolean worldEnabled;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}

	public void setPvpEnabled(boolean pvpEnabled) {
		this.pvpEnabled = pvpEnabled;
	}

	public boolean isPvpEnabled() {
		return pvpEnabled;
	}

	public void setWorldEnabled(boolean worldEnabled) {
		this.worldEnabled = worldEnabled;
	}

	public boolean isWorldEnabled() {
		return worldEnabled;
	}
}
