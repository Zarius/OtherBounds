package com.gmail.zariust.otherbounds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class OtherBoundsConfig {

	public static long taskDelay;
	public static boolean safeInsideBoundary;
	private final OtherBounds parent;

	// Track loaded files so we don't get into an infinite loop
	Set<String> loadedDropFiles = new HashSet<String>();
	private String boundariesFile;

	public OtherBoundsConfig(OtherBounds instance) {
		parent = instance;
		taskDelay = 120;
	}

	public void load() {
        loadConfig();
		Dependencies.init();
		loadIncludeFile(boundariesFile);

        OtherBounds.disableOtherBounds();
		OtherBounds.damageList.clear();
        OtherBounds.enableOtherBounds();
		OtherBounds.damageList.clear();
		Log.high("DAMAGE CLEARED"+OtherBounds.damageList.toString());
	}
	
	public void loadConfig() {
		loadedDropFiles.clear();
		
		parent.getDataFolder().mkdirs();
		String filename = "config.yml";
		String boundariesFilename = "boundaries.yml";

		File global = new File(parent.getDataFolder(), filename);
		YamlConfiguration globalConfig = YamlConfiguration.loadConfiguration(global);

		Log.setConfigVerbosity(globalConfig);  // yes, already loaded in plugin.onEnable() - this is for reloads

		// Make sure config file exists (even for reloads - it's possible this did not create successfully or was deleted before reload)
		try {
            firstRun();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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

		//enableBlockTo = globalConfig.getBoolean("enableblockto", false);
		boundariesFile = globalConfig.getString("rootconfig", boundariesFilename);

		safeInsideBoundary = globalConfig.getBoolean("safeinsideboundary", true);
		taskDelay = globalConfig.getInt("ticks", 10);
		if (taskDelay < 5) taskDelay = 5; // a minimum for safety
		Log.high("Loaded global config ("+global+"), keys found: "+globalConfig.getKeys(false).toString() + " (verbosity="+Log.verbosity+")");

	}

    /**
     * Check for config files and other settings (events & includes), if not
     * found then export the resource from plugin jar file.
     * 
     * @throws Exception
     */
    private void firstRun() throws Exception {
        if (!checkIfAllowedToRefreshFiles())
            return;

        List<String> files = new ArrayList<String>();
        files.add("config.yml");
        files.add("boundaries.yml");

        for (String filename : files) {
            File file = new File(parent.getDataFolder(), filename);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                copy(parent.getResource(filename), file);
            }
        }
    }

    private boolean checkIfAllowedToRefreshFiles()
            throws FileNotFoundException, IOException,
            InvalidConfigurationException {
        File file = new File(parent.getDataFolder(), "config.yml");
        if (file.exists()) {
            YamlConfiguration globalConfig = YamlConfiguration
                    .loadConfiguration(file);
            globalConfig.load(file);
            if (!globalConfig.getBoolean("restore_deleted_config_files", true))
                return false;
        }
        return true;
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void writeDefaultConfig(File global) {
		try {
			global.createNewFile();
			Log.normal("Created an empty file " + parent.getDataFolder() +"/"+global.getName()+", please edit it!");

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
		    Log.warning("Could not generate "+global.getName()+". Are the file permissions OK?");
		}
	}		

	private void loadIncludeFile(String filename) {
		// Check for infinite include loops
		if(loadedDropFiles.contains(filename)) {
		    Log.warning("Infinite include loop detected at " + filename);
			return;
		} else loadedDropFiles.add(filename);

		Log.high("Loading file: "+filename);

		File yml = new File(parent.getDataFolder(), filename);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(yml);

		// Make sure config file exists (even for reloads - it's possible this did not create successfully or was deleted before reload) 
		if (!yml.exists())
		{
			try {
				yml.createNewFile();
				Log.normal("Created an empty file " + parent.getDataFolder() +"/"+filename+", please edit it!");
				config.set("boundaries", null);
				config.set("include-files", null);
				config.set("defaults", null);
				config.set("aliases", null);
				config.save(yml);
			} catch (IOException ex){
				Log.warning(parent.getDescription().getName() + ": could not generate "+filename+". Are the file permissions OK?");
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
		if (node == null) Log.high("No options found for boundary ("+name+")");
		//		for(String childName : node.getKeys(name)) {
		//		ConfigurationNode subNode = node.getNode(name+"/"+childName);
		//Main.logInfo("Parsing boundary ("+name+")", Verbosity.HIGH);
		//boolean isGroup = dropNode.getKeys().contains("dropgroup");
		Boundary boundary = Boundary.parseFrom(name, node);

		// loop through worlds and if positive add the boundary to that world
		if (boundary == null) {
			Log.warning("Boundary failed [null] ("+name+")");
			return;
		} else if(boundary.worlds == null) {
			Log.warning("No worlds found for boundary ("+name+")");
			return;
		}

		for (World world : Bukkit.getServer().getWorlds()) {
			Boolean activeWorld = boundary.worlds.get(world.getName()); 
			if (activeWorld == null) {
				//Main.logWarning("Error: world ("+world.getName()+") is null in boundary.");
				activeWorld = false;
			}
			Log.high("Boundary worlds: "+activeWorld);
			if (activeWorld || boundary.worlds.get(null)) {
				OtherBounds.boundaryList.add(world, boundary); 
				Log.high("Adding boundary to world ("+world.getName()+").");
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
		Log.highest(node.toString());
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
					Log.warning("Invalid world " + name + "; skipping...");
					continue;
				}
				result.put(world.getName(), false);
			} else if (world != null) result.put(world.getName(), true);
			// wildcard
		}
		for(String name : worldsExcept) {
			World world = Bukkit.getServer().getWorld(name);
			if(world == null) {
				Log.warning("Invalid world exception " + name + "; skipping...");
				continue;
			}
			result.put(world.getName(), false);
		}
		if(result.isEmpty()) return null;
		return result;
	}
}

