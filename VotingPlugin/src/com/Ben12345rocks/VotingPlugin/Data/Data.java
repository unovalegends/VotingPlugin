package com.Ben12345rocks.VotingPlugin.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.Ben12345rocks.VotingPlugin.Main;
import com.Ben12345rocks.VotingPlugin.Utils;
import com.Ben12345rocks.VotingPlugin.Config.Config;
import com.Ben12345rocks.VotingPlugin.Files.Files;
import com.Ben12345rocks.VotingPlugin.Objects.User;

public class Data {

	static Data instance = new Data();

	static Main plugin = Main.plugin;

	public static Data getInstance() {
		return instance;
	}

	private Data() {
	}

	public Data(Main plugin) {
		Data.plugin = plugin;
	}

	public void addCumulativeSite(User user, String voteSite) {
		setCumulativeSite(user, voteSite, getCumulativeSite(user, voteSite) + 1);
	}

	public void addTotal(User user, String voteSite) {
		set(user, "Total." + voteSite, getTotal(user, voteSite) + 1);
	}

	public int getAllSitesOffline(User user) {
		return getData(user).getInt("OtherRewards.AllSites");
	}

	public int getCumulativeSite(User user, String voteSite) {
		return getData(user).getInt("Cumulative." + voteSite);
	}

	public FileConfiguration getData(User user) {
		File dFile = getPlayerFile(user);
		FileConfiguration data = YamlConfiguration.loadConfiguration(dFile);
		return data;
	}

	public ArrayList<String> getFiles() {
		File folder = new File(plugin.getDataFolder() + File.separator + "Data");
		String[] fileNames = folder.list();
		return Utils.getInstance().convertArray(fileNames);
	}

	public int getFirstVoteOffline(User user) {
		return getData(user).getInt("OtherRewards.FirstVote");
	}

	public String getName(User user) {
		return getData(user).getString("Name");
	}

	public int getNumberOfVotesOffline(User user) {
		return getData(user).getInt("OtherRewards.NumberOfVotes");
	}

	public int getOfflineVotesSite(User user, String siteName) {
		return getData(user).getInt("OfflineVotes." + siteName);
	}

	public int getOfflineVotesSiteWorld(User user, String reward, String world) {
		if (world == null) {
			world = "AllTheWorlds";
		}
		return getData(user)
				.getInt("OfflineVotesWorld." + reward + "." + world);
	}

	public File getPlayerFile(User user) {
		String playerName = user.getPlayerName();
		String uuid = user.getUUID();
		File dFile = new File(plugin.getDataFolder() + File.separator + "Data",
				uuid + ".yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(dFile);
		if (!dFile.exists()) {
			try {
				data.save(dFile);
				setName(user);
				if (Config.getInstance().getDebugEnabled()) {
					plugin.getLogger().info(
							"Created file: " + uuid + ".yml from player: "
									+ playerName);
				}
			} catch (IOException e) {
				Bukkit.getServer()
				.getLogger()
				.severe(ChatColor.RED + "Could not create " + uuid
						+ ".yml! Name: " + playerName);

			}
		}
		return dFile;

	}

	public ArrayList<String> getPlayerNames() {
		ArrayList<String> files = getFiles();
		ArrayList<String> names = new ArrayList<String>();
		if (files != null) {
			for (String playerFile : files) {
				String uuid = playerFile.replace(".yml", "");
				String playerName = Utils.getInstance().getPlayerName(uuid);
				if (playerName != null) {
					names.add(playerName);
				}
			}
			Set<String> namesSet = new HashSet<String>(names);
			names = Utils.getInstance().convert(namesSet);
			if (names == null) {
				return null;
			} else {
				return names;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	public ArrayList<String> getPlayersUUIDs() {
		ArrayList<String> files = getFiles();
		if (files != null) {
			ArrayList<String> uuids = new ArrayList<String>();
			if (files.size() > 0) {
				for (String playerFile : files) {
					String uuid = playerFile.replace(".yml", "");
					uuids.add(uuid);
				}
				if (uuids == null) {
					return null;
				} else {
					return uuids;
				}
			}
		}
		return null;

	}

	public boolean getReminded(User user) {
		return getData(user).getBoolean("Reminded");
	}

	public long getTimeSite(User user, String voteSite) {
		return getData(user).getLong("LastVote." + voteSite + ".Miliseconds");

	}

	@SuppressWarnings("deprecation")
	public int getTopVoterAwardOffline(User user) {
		return getData(user).getInt(
				"TopVoter." + new Date().getYear() + "."
						+ new Date().getMonth());
	}

	public int getTotal(User user, String voteSite) {
		return getData(user).getInt("Total." + voteSite);
	}

	public Set<User> getUsers() {
		Set<User> users = new HashSet<User>();
		ArrayList<String> players = getPlayerNames();
		if (players != null) {
			for (String playerName : players) {
				User user = new User(playerName);
				users.add(user);
			}
			return users;
		} else {
			return null;
		}
	}

	public boolean hasJoinedBefore(User user) {
		try {
			return getPlayersUUIDs().contains(user.getUUID());
		} catch (Exception ex) {
			return false;
		}
	}

	public void saveData(User user) {
		File dFile = getPlayerFile(user);
		String playerName = user.getPlayerName();
		FileConfiguration data = YamlConfiguration.loadConfiguration(dFile);
		try {
			data.save(dFile);
		} catch (IOException e) {
			Bukkit.getServer()
			.getLogger()
			.severe(ChatColor.RED + "Could not save "
					+ Utils.getInstance().getUUID(playerName) + ".yml!");
		}

	}

	public void set(User user, String path, Object value) {
		File dFile = getPlayerFile(user);
		FileConfiguration data = YamlConfiguration.loadConfiguration(dFile);
		data.set(path, value);
		Files.getInstance().editFile(dFile, data);
	}

	public void setAllSitesOffline(User user, int value) {
		set(user, "OtherRewards.AllSites", value);
	}

	public void setCumulativeSite(User user, String voteSite, int amount) {
		set(user, "Cumulative." + voteSite, amount);
	}

	public void setFirstVoteOffline(User user, int value) {
		set(user, "OtherRewards.FirstVote", value);
	}

	public void setName(User user) {
		set(user, "Name", user.getPlayerName());
	}

	public void setNumberOfVotesOffline(User user, int value) {
		set(user, "OtherRewards.NumberOfVotes", value);
	}

	public void setOfflineVotesSite(User user, String siteName, int value) {
		set(user, "OfflineVotes." + siteName, value);
	}

	public void setOfflineVotesSiteWorld(User user, String reward,
			String world, int value) {
		if (world == null) {
			world = "AllTheWorlds";
		}
		set(user, "OfflineVotesWorld." + reward + "." + world, value);
	}

	public void setReminded(User user, boolean value) {
		set(user, "Reminded", value);
	}

	/*
	 * public int getVotesBonusReward(User user) { return
	 * getData(user).getInt("BonusVotes"); }
	 *
	 * public void setVotesBonusReward(User user, int value) { set(user,
	 * "BonusVotes", value); }
	 */

	public void setTime(String siteName, User user) {
		set(user, "LastVote." + siteName + ".Miliseconds",
				System.currentTimeMillis());
	}

	public void setTimeMill(String siteName, User user, Long mill) {
		set(user, "LastVote." + siteName + ".Miliseconds", mill);
	}

	public void setTimeSite(User user, String voteSite, int value) {
		set(user, "LastVote." + voteSite + ".Miliseconds", value);

	}

	@SuppressWarnings("deprecation")
	public void setTopVoterAwardOffline(User user, int place) {
		set(user,
				"TopVoter." + new Date().getYear() + "."
						+ new Date().getMonth(), place);
	}

	public void setTotal(User user, String voteSite, int amount) {
		set(user, "Total." + voteSite, amount);
	}

	public void setup(User user) {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}

		String uuid = user.getUUID();
		String playerName = user.getPlayerName();
		if (playerName == null) {
			Utils.getInstance().getPlayerName(uuid);
		}

		File dFile = new File(plugin.getDataFolder() + File.separator + "Data",
				uuid + ".yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(dFile);
		if (!dFile.exists()) {
			try {
				data.save(dFile);
				setName(user);
				if (Config.getInstance().getDebugEnabled()) {
					plugin.getLogger().info(
							"Created file: " + uuid + ".yml from player: "
									+ playerName);
				}
			} catch (IOException e) {
				plugin.getLogger().severe(
						ChatColor.RED + "Could not create " + uuid
						+ ".yml! Name: " + playerName);

			}
		}
	}
}
