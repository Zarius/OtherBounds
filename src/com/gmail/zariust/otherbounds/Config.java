package com.gmail.zariust.otherbounds;

import static com.gmail.zariust.otherbounds.common.Verbosity.HIGH;
import static com.gmail.zariust.otherbounds.common.Verbosity.HIGHEST;
import static com.gmail.zariust.otherbounds.common.Verbosity.NORMAL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.zariust.otherbounds.boundary.Boundary;
import com.gmail.zariust.otherbounds.common.CommonPlugin;
import com.gmail.zariust.otherbounds.common.Verbosity;

public class Config {

	public static long taskDelay;
	public static Verbosity verbosity;
	public static boolean safeInsideBoundary;
	private final Main parent;

	// Track loaded files so we don't get into an infinite loop
	Set<String> loadedDropFiles = new HashSet<String>();

	public Config(Main instance) {
		parent = instance;

		verbosity = HIGH;

		taskDelay = 120;
	}


	public void loadConfig() {
		parent.getDataFolder().mkdirs();
		String filename = "otherbounds-config.yml";

		File global = new File(parent.getDataFolder(), filename);
		YamlConfiguration globalConfig = YamlConfiguration.loadConfiguration(global);

		// Make sure config file exists (even for reloads - it's possible this did not create successfully or was deleted before reload)
		// TODO: create the folder if it doesn't exist
		if (!global.exists()) {
			writeDefaultConfig(global);
		}
		// Load in the values from the configuration file
		try {
			globalConfig.load(global);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		globalConfig.set("verbosity", "normal");

		verbosity = CommonPlugin.getConfigVerbosity(globalConfig);
		//enableBlockTo = globalConfig.getBoolean("enableblockto", false);
		String boundariesFile = globalConfig.getString("rootconfig", "otherbounds-config.yml");

		safeInsideBoundary = globalConfig.getBoolean("safeinsideboundary", true);
		taskDelay = globalConfig.getInt("ticks", 10);
		if (taskDelay < 5) taskDelay = 5; // a minimum for safety
		Main.logInfo("Loaded global config ("+global+"), keys found: "+globalConfig.getKeys(false).toString() + " (verbosity="+verbosity+")");

		loadIncludeFile(boundariesFile);
	}


	private void writeDefaultConfig(File global) {
		try {
			global.createNewFile();
			Main.logInfo("Created an empty file " + parent.getDataFolder() +"/"+global.getName()+", please edit it!");

			PrintWriter stream = null;
			stream = new PrintWriter(global);
			//Let's write our goods ;)
			stream.println("verbosity: normal");
			stream.println("ticks: 40");
			stream.println("safeinsideboundary: false");
			stream.println("");
			stream.println("boundaries:");
			stream.println("  main_boundary:");
			stream.println("    radius: 200");
			stream.println("    centre-x: 0");
			stream.println("    centre-z: 0");
			stream.println("    world: ALL");
			stream.println("    damage: 0");
			stream.println("    except: [Xyzzy]  # optional list of player names, eg. [plugh, xarqn, fred]");
			stream.println("    exceptpermissions: [mainBoundaryOverride]  # give players otherbounds.custom.mainBoundaryOverride (note, cannot include _ characters)");
			stream.println("    messagedanger: Warning - high radiation level detected.");
			stream.println("    messagesafe: You have returned to safer lands.");
			stream.println("");
			stream.close();
			//globalConfig.save(global);
		} catch (IOException ex){
			Main.logWarning("Could not generate "+global.getName()+". Are the file permissions OK?");
		}
	}		



	private void loadIncludeFile(String filename) {
		// Check for infinite include loops
		if(loadedDropFiles.contains(filename)) {
			Main.logWarning("Infinite include loop detected at " + filename);
			return;
		} else loadedDropFiles.add(filename);

		Main.logInfo("Loading file: "+filename,HIGH);

		File yml = new File(parent.getDataFolder(), filename);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(yml);

		// Make sure config file exists (even for reloads - it's possible this did not create successfully or was deleted before reload) 
		if (!yml.exists())
		{
			try {
				yml.createNewFile();
				Main.logInfo("Created an empty file " + parent.getDataFolder() +"/"+filename+", please edit it!");
				config.set("boundaries", null);
				config.set("include-files", null);
				config.set("defaults", null);
				config.set("aliases", null);
				config.save(yml);
			} catch (IOException ex){
				Main.logWarning(parent.getDescription().getName() + ": could not generate "+filename+". Are the file permissions OK?");
			}
			// Nothing to load in this case, so exit now
			return;
		}

		try {
			config.load(yml);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Load the drops
		ConfigurationSection node = config.getConfigurationSection("boundaries");
		Set<String> blocks = null;

		if (node != null) {
			blocks = node.getKeys(false);
			for(String name : blocks) {
				String boundaryName = name;
				loadBoundary(node.getConfigurationSection(name), boundaryName);
			}
		}

		// Load the include files
		List<String> includeFiles = config.getStringList("include-files");
		for(String include : includeFiles) loadIncludeFile(include);
	}


	private void loadBoundary(ConfigurationSection node, String name) {
		if (node == null) Main.logInfo("No options found for boundary ("+name+")", HIGH);
		//		for(String childName : node.getKeys(name)) {
		//		ConfigurationNode subNode = node.getNode(name+"/"+childName);
		//Main.logInfo("Parsing boundary ("+name+")", Verbosity.HIGH);
		//boolean isGroup = dropNode.getKeys().contains("dropgroup");
		Boundary boundary = Boundary.parseFrom(name, node);

		// loop through worlds and if positive add the boundary to that world
		if (boundary == null) {
			Main.logWarning("Boundary failed [null] ("+name+")", NORMAL);
			return;
		} else if(boundary.worlds == null) {
			Main.logWarning("No worlds found for boundary ("+name+")", NORMAL);
			return;
		}

		for (World world : Bukkit.getServer().getWorlds()) {
			Boolean activeWorld = boundary.worlds.get(world.getName()); 
			if (activeWorld == null) {
				//Main.logWarning("Error: world ("+world.getName()+") is null in boundary.");
				activeWorld = false;
			}
			Main.logInfo("Boundary worlds: "+activeWorld);
			if (activeWorld || boundary.worlds.get(null)) {
				Main.boundaryList.add(world, boundary); 
				Main.logInfo("Adding boundary to world ("+world.getName()+").", HIGH);
			}
		}
		//		}
	}

	public static List<String> getMaybeList(ConfigurationSection node, String key) {
		if(node == null) return new ArrayList<String>();
		Object prop = node.get(key);
		List<String> list;
		if(prop == null) return new ArrayList<String>();
		else if(prop instanceof List) list = node.getStringList(key);
		else list = Collections.singletonList(prop.toString());
		return list;
	}

	public static Map<String, Boolean> parseWorldsFrom(ConfigurationSection node, Map<String, Boolean> def) {
		Main.logInfo(node.toString(),HIGHEST);
		List<String> worlds = getMaybeList(node, "world");
		List<String> worldsExcept = getMaybeList(node, "worldexcept");

		if(worlds.isEmpty() && worldsExcept.isEmpty()) return def;
		Map<String, Boolean> result = new HashMap<String,Boolean>();
		result.put(null, false); 
		for(String name : worlds) {
			if(name.equalsIgnoreCase("ALL") || name.equalsIgnoreCase("ANY")) {
				result.put(null, true);
				continue;
			}
			World world = Bukkit.getServer().getWorld(name);
			if(world == null && name.startsWith("-")) {
				world = Bukkit.getServer().getWorld(name.substring(1));
				if(world == null) {
					Main.logWarning("Invalid world " + name + "; skipping...");
					continue;
				}
				result.put(world.getName(), false);
			} else if (world != null) result.put(world.getName(), true);
			// wildcard
		}
		for(String name : worldsExcept) {
			World world = Bukkit.getServer().getWorld(name);
			if(world == null) {
				Main.logWarning("Invalid world exception " + name + "; skipping...");
				continue;
			}
			result.put(world.getName(), false);
		}
		if(result.isEmpty()) return null;
		return result;
	}


}

