package tv.tirco.parkmanager.storage.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import tv.tirco.parkmanager.ParkManager;
import tv.tirco.parkmanager.config.Config;
import tv.tirco.parkmanager.storage.playerdata.PlayerProfile;
import tv.tirco.parkmanager.storage.playerdata.UserManager;
import tv.tirco.parkmanager.util.MessageHandler;

public class PlayerFileManager implements DatabaseManager {
	
	/**
	 * Based on https://github.com/mcMMO-Dev/mcMMO/blob/dacd846fe76e762f6ecfaeea8bb84a2c89917a43/src/main/java/com/gmail/nossr50/database/FlatfileDatabaseManager.java#L747
	 * By nossr50
	 * 
	 */

	// FLATFILE VERSION
	//PlayerName 0: UUID 1: LastSeen 2: 3+ -> unlocked 0=0: BREAK:
	static int PLAYERNAME_POSITION = 0;
	static int UUID_POSITION = 1;
	static int LAST_SEEN_POSITION = 2;
	//static int AMOUNT_FOUND_POSITION = 3;

	private static final Object fileWritingLock = new Object();
    public static final int TIME_CONVERSION_FACTOR = 1000;
    public static final int TICK_CONVERSION_FACTOR = 20;

	@SuppressWarnings("unused")
	private final long UPDATE_WAIT_TIME = 600000L; // 10 minutes - TODO add autoUpdate?

	private final File usersFile;

	public PlayerFileManager() {
		usersFile = new File(ParkManager.getUsersFilePath());
		checkStructure();
	}

	public void onDisable() {

	}

	public int purgePowerlessUsers() {
		//Should never be used? - Powerless users are not saved.
		int purgedUsers = 0;

		MessageHandler.getInstance().log("Purging powerless users...");

		BufferedReader in = null;
		FileWriter out = null;
		String usersFilePath = ParkManager.getUsersFilePath();

		// This code is O(n) instead of O(n�)
		synchronized (fileWritingLock) {
			try {
				in = new BufferedReader(new FileReader(usersFilePath));
				StringBuilder writer = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					ArrayList<String> character = new ArrayList<String>(Arrays.asList(line.split(":")));
					boolean powerless = isPowerLess(character);
					

					// If they're still around, rewrite them to the file.
					if (!powerless) {
						writer.append(line).append("\r\n");
					} else {
						purgedUsers++;
					}
				}

				// Write the new file
				out = new FileWriter(usersFilePath);
				out.write(writer.toString());
			} catch (IOException e) {
				// RecklessRPG.p.logError("Exception while reading " + usersFilePath + " (Are
				// you sure you formatted it correctly?)" + e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}

		MessageHandler.getInstance().log("Purged " + purgedUsers + " users from the database.");
		return purgedUsers;

	}

	private boolean isPowerLess(ArrayList<String> character) {
		//String lastSeen = character.get(2);
		//String uuid = character.get(1);
		//String name = character.get(0);
		
		character.remove(character.size()-1); //Remove BREAK
		character.remove(3); //Remove AmountFound
		character.remove(2); //Remove LastSeen
		character.remove(1); //Remove UUID
		character.remove(0); //Remove Name
		
		boolean powerless = true;
		for(String s : character) {
			if(s.substring(s.length() - 1).equalsIgnoreCase("1")) {
				powerless = false;
				break;
			}
		}
		
		return powerless;
	}

	public void purgeOldUsers() {
		int removedPlayers = 0;
		long currentTime = System.currentTimeMillis();
		

		if(Config.getInstance().getOldUsersCutoff() == 0) {
			return;
		}

		MessageHandler.getInstance().log("Purging old users...");

		BufferedReader in = null;
		FileWriter out = null;
		String usersFilePath = ParkManager.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				in = new BufferedReader(new FileReader(usersFilePath));
				StringBuilder writer = new StringBuilder();
				String line;
				

				while ((line = in.readLine()) != null) {
					String[] character = line.split(":");
					String uuid = character[UUID_POSITION];
					long lastPlayed = 0;
					boolean rewrite = false;
					try {
						lastPlayed = Long.parseLong(character[LAST_SEEN_POSITION]) * TIME_CONVERSION_FACTOR;
					} catch (NumberFormatException e) {
					}
					if (lastPlayed == 0) {
						//Last played not found in config, getting last time player was online on server
						OfflinePlayer player = ParkManager.plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
						lastPlayed = player.getLastPlayed();
						rewrite = true;
					}

					if ((currentTime - lastPlayed > PURGE_TIME)) {
						removedPlayers++;
					} else {
						if (rewrite) {
							// Rewrite their data with a valid time
							character[LAST_SEEN_POSITION] = Long.toString(lastPlayed);
							String newLine = org.apache.commons.lang.StringUtils.join(character, ":");
							writer.append(newLine).append("\r\n");
						} else {
							writer.append(line).append("\r\n");
						}
					}
				}

				// Write the new file
				out = new FileWriter(usersFilePath);
				out.write(writer.toString());
			} catch (IOException e) {
				MessageHandler.getInstance().log("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)"
						+ e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}

		MessageHandler.getInstance().log("Purged " + removedPlayers + " users from the database due to age.");
		MessageHandler.getInstance().log("Purge time: " + Config.getInstance().getOldUsersCutoff() + " months.");
	}

	public boolean removeUser(String playerName) {
		boolean worked = false;

		BufferedReader in = null;
		FileWriter out = null;
		String usersFilePath = ParkManager.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				in = new BufferedReader(new FileReader(usersFilePath));
				StringBuilder writer = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					// Write out the same file but when we get to the player we want to remove, we
					// skip his line.
					if (!worked && line.split(":")[PLAYERNAME_POSITION].equalsIgnoreCase(playerName)) {
						// RecklessRPG.p.log("User found, removing...");
						worked = true;
						continue; // Skip the player
					}

					writer.append(line).append("\r\n");
				}

				out = new FileWriter(usersFilePath); // Write out the new file
				out.write(writer.toString());
			} catch (Exception e) {
				MessageHandler.getInstance().log("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)"
						+ e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}

		UserManager.profileCleanup(playerName);

		return worked;
	}

	public boolean saveUser(PlayerProfile profile) {
		boolean ignorethis = true;
		if(ignorethis) {
			return true;
		}
		String playerName = profile.getPlayerName();
		UUID uuid = profile.getUuid();

		BufferedReader in = null;
		FileWriter out = null;
		String usersFilePath = ParkManager.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				// open the file
				in = new BufferedReader(new FileReader(usersFilePath));
				StringBuilder writer = new StringBuilder();
				String line;

				boolean wroteUser = false;
				while ((line = in.readLine()) != null) {
					// read the line in and copy it to the output if it's not the player we want to
					// edit.
					String[] character = line.split(":");
					if (!(uuid != null && character[UUID_POSITION].equalsIgnoreCase(uuid.toString()))
							&& !character[PLAYERNAME_POSITION].equalsIgnoreCase(playerName)) {
						writer.append(line).append("\r\n");
						//UUID and Name did not match, so write line to file as normal.
					} else {
						writeUserToLine(profile, playerName, uuid, writer);
						wroteUser = true;
						
					}

				}
				
                /*
                 * If we couldn't find the user in the DB we need to add him
                 * Extra failsafe after so many first time users just going poof :I
                 */
                if(!wroteUser)
                {
                    writeUserToLine(profile, playerName, uuid, writer);
                    MessageHandler.getInstance().debug("Player " + playerName + " was not in the database! This would've resulted in a failed save previously.");
                }

				// write new file
				out = new FileWriter(usersFilePath);
				out.write(writer.toString());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						MessageHandler.getInstance().debug("IOException when closing line IN");
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						MessageHandler.getInstance().debug("IOException when closing line OUT");
					}
				}
			}
		}
	}

	private void writeUserToLine(PlayerProfile profile, String playerName, UUID uuid, StringBuilder writer) {
		// otherwise write the new player information
		writer.append(playerName).append(":"); // PlayerName - line 0
		writer.append(uuid != null ? uuid.toString() : "NULL").append(":"); // UUID - 1
		writer.append(String.valueOf(System.currentTimeMillis() / TIME_CONVERSION_FACTOR))
		.append(":"); // LastLogin - 2
		
		//Loop through all skulls and save the ones that are true.
		int saved = 0;
//		for(int i : profile.getFound().keySet()) {
//			if(profile.getFound().get(i)) {
//				writer.append(i+"=1").append(":");
//				saved++;
//			}
//		}
		if(saved < 1) {
			writer.append("0=0:"); //Make sure that saved heads isn't empty!
			//Allthough... if it is, why would it try to be saved? o.o
			MessageHandler.getInstance().debug("SaveData for player " + playerName + " was saved with an empty saved-list.");
		}
		writer.append("BREAK").append(":");
		writer.append("\r\n");
		MessageHandler.getInstance().debug("SaveData for player " + playerName + " is now written to file.");
		
	}

	public void newUser(String playerName, UUID uuid) {
		boolean test = true;
		if(test) {
			return;
		} else {
			BufferedWriter out = null;
			synchronized (fileWritingLock) {
				try {
					// open the file to write the player
					out = new BufferedWriter(new FileWriter(ParkManager.getUsersFilePath(), true));

					// add the player + stats to the end. All stats are 0 by default.
					//PlayerName 0: UUID 1: LastSeen 2: 3+ -> unlocked 0=0: BREAK:
					out.append(playerName).append(":"); // PlayerName - line 0
					out.append(uuid != null ? uuid.toString() : "NULL").append(":"); // UUID - 1
					out.append(String.valueOf(System.currentTimeMillis() / TIME_CONVERSION_FACTOR)).append(":"); //Time - 2
					out.append("0:"); //AMOUNT_FOUND_POSITION 3
					out.append("0=0:");//Unlocked 4 - ?
					out.append("BREAK:"); // unspent level points - Final
					out.append("\r\n");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			}
		}
		
	}

	public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create) {
		BufferedReader in = null;
		String usersFilePath = ParkManager.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				// Open the user file
				in = new BufferedReader(new FileReader(usersFilePath));
				String line;

				while ((line = in.readLine()) != null) {
					// Find if the line contains the player we want.
					String[] character = line.split(":");

					// Compare names because we don't have a valid uuid for that player even
					// if input uuid is not null
					if (character[UUID_POSITION].equalsIgnoreCase("NULL")) {
						if (!character[PLAYERNAME_POSITION].equalsIgnoreCase(playerName)) {
							continue;
						}
					}
					// If input uuid is not null then we should compare uuids
					else if ((uuid != null && !character[UUID_POSITION].equalsIgnoreCase(uuid.toString()))
							|| (uuid == null && !character[PLAYERNAME_POSITION].equalsIgnoreCase(playerName))) {
						continue;
					}

					// Update playerName in database after name change
					if (!character[PLAYERNAME_POSITION].equalsIgnoreCase(playerName)) {
						MessageHandler.getInstance().log("Name change detected: " + character[PLAYERNAME_POSITION] + " => " + playerName);
						character[PLAYERNAME_POSITION] = playerName;
					}

					return loadFromLine(character);
				}

				// Didn't find the player, create a new one
				if (create) {
					MessageHandler.getInstance().log("Didn't find player, creating new one...");
					if (uuid == null) {
						MessageHandler.getInstance().log("UUID of new player is NULL");
						newUser(playerName, uuid);
						return new PlayerProfile(playerName, null);
					}

					newUser(playerName, uuid);
					return new PlayerProfile(playerName, uuid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// I have no idea why it's necessary to inline tryClose() here, but it removes
				// a resource leak warning, and I'm trusting the compiler on this one.
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}

		// Return unloaded profile
		if (uuid == null) {
			return new PlayerProfile(playerName, null);
		}

		return new PlayerProfile(playerName, uuid);
	}

	private PlayerProfile loadFromLine(String[] character) {
		UUID uuid;
		try {
			uuid = UUID.fromString(character[UUID_POSITION]);
		} catch (Exception e) {
			uuid = null;
		}
		String playerName = character[PLAYERNAME_POSITION];
		

		return new PlayerProfile(playerName, uuid);
	}

	public List<String> getStoredUsers() {
		ArrayList<String> users = new ArrayList<String>();
		BufferedReader in = null;
		String usersFilePath = ParkManager.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				// Open the user file
				in = new BufferedReader(new FileReader(usersFilePath));
				String line;

				while ((line = in.readLine()) != null) {
					String[] character = line.split(":");
					users.add(character[PLAYERNAME_POSITION]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}
		return users;
	}

	/**
	 * Checks that the file is present and valid
	 */
	private void checkStructure() {
		boolean ignoreThis = true;
		if(ignoreThis) {
			return;
		}
		
		if (usersFile.exists()) {
			BufferedReader in = null;
			FileWriter out = null;
			String usersFilePath = ParkManager.getUsersFilePath();

			synchronized (fileWritingLock) {
				try {
					in = new BufferedReader(new FileReader(usersFilePath));
					StringBuilder writer = new StringBuilder();
					String line;
					HashSet<String> usernames = new HashSet<String>();
					HashSet<String> players = new HashSet<String>();

					while ((line = in.readLine()) != null) {
						// Remove empty lines from the file
						if (line.isEmpty()) {
							continue;
						}

						// Length checks depend on last character being ':'
						if (line.charAt(line.length() - 1) != ':') {
							line = line.concat(":");
						}
						boolean updated = false;
						String[] character = line.split(":");

						// Prevent the same username from being present multiple times
						if (!usernames.add(character[PLAYERNAME_POSITION])) {
							character[PLAYERNAME_POSITION] = "_INVALID_OLD_USERNAME_'";
							updated = true;
							if (character.length < UUID_POSITION + 1 || character[UUID_POSITION].equals("NULL")) {
								continue;
							}
						}

						// Prevent the same player from being present multiple times
						if (character.length >= 12 && (!character[UUID_POSITION].isEmpty()
								&& !character[UUID_POSITION].equals("NULL") && !players.add(character[UUID_POSITION]))) {
							continue;
						}

						// If they're valid, rewrite them to the file.
						if (!updated) {
							writer.append(line).append("\r\n");
							continue;
						}

						if (updated) {
							line = new StringBuilder(org.apache.commons.lang.StringUtils.join(character, ":"))
									.append(":").toString();
						}

						writer.append(line).append("\r\n");
					}

					// Write the new file
					out = new FileWriter(usersFilePath);
					out.write(writer.toString());
				} catch (IOException e) {
					// logError("Exception while reading " + usersFilePath + " (Are
					// you sure you formatted it correctly?)" + e.toString());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							// Ignore
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			}
			return;
		}

		usersFile.getParentFile().mkdir();

		try {
			new File(ParkManager.getUsersFilePath()).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create, boolean retry) {
		// TODO Auto-generated method stub
		return null;
	}


//	@Override
//	public PlayerProfile loadPlayerProfile(UUID uuid, boolean createNew) {
//		return loadPlayerProfile("", uuid, createNew);
//	}


}
