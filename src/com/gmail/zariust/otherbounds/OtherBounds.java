package com.gmail.zariust.otherbounds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.zariust.otherbounds.boundary.BoundaryList;
import com.gmail.zariust.otherbounds.parameters.actions.Action;

public class OtherBounds extends JavaPlugin 
{
    private static Server server;
    public static OtherBounds plugin;
    boolean enabled;
    
    // Global random number generator - used for DamageAction
    public static Random         rng    = new Random();

    public static Map <Player, Effects> damageList;
    public static BoundaryList boundaryList;

    // usefull??
    public static Map<String, List<Long>> profileMap;

    static OtherBoundsConfig config;
    static Logger log;
    
    ListenerPlayer playerListener;
        
    static BukkitTask syncTaskId;
    static BukkitTask aSyncTaskId;

    public OtherBounds() {
        
        playerListener = new ListenerPlayer();
        
        boundaryList = new BoundaryList();

        // this list is used to store the last entity to damage another entity (along with the weapon used and range, if applicable)
        damageList = new HashMap<Player, Effects>();
        
        // this is used to store profiling information (milliseconds taken to complete function runs)
        profileMap = new HashMap<String, List<Long>>();
        profileMap.put("PLAYER_MOVE", new ArrayList<Long>());
                
        log = Logger.getLogger("Minecraft");
    }

    @Override
	public void onEnable() {
        OtherBounds.server = getServer();
        OtherBounds.plugin = this;
        Log.pluginName = this.getDescription().getName();
        Log.pluginVersion = this.getDescription().getVersion();

        File global = new File(getDataFolder(), "otherbounds-config.yml");
        YamlConfiguration globalConfig = YamlConfiguration
                .loadConfiguration(global);
        Log.setConfigVerbosity(globalConfig);
        Action.registerDefaultActions();
        
		this.getCommand("ob").setExecutor(new OtherBoundsCommand(this));

		// Load the config files
        OtherBounds.config = new OtherBoundsConfig(this);
        config.load(); // load config, Dependencies & enable
        
    };
    
	public static void enableOtherBounds() {
        // Grab plugin manager
        // Register Events (OnPlayerMove if enabled in options?)
		//PluginManager pm = Bukkit.getServer().getPluginManager();
        //pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Config.pri, this);

        // Set up scheduler tasks
        // async - runs every x ticks and checks if players are in/outside boundaries
        RunAsync aSyncRunner = new RunAsync(OtherBounds.plugin);
        aSyncTaskId = server.getScheduler().runTaskTimerAsynchronously(OtherBounds.plugin, aSyncRunner, OtherBoundsConfig.taskDelay, OtherBoundsConfig.taskDelay);                     

        // sync - runs every x ticks and checks if players need to be damaged
        RunSync syncRunner = new RunSync();
        syncTaskId = server.getScheduler().runTaskTimer(OtherBounds.plugin, syncRunner, OtherBoundsConfig.taskDelay+10, OtherBoundsConfig.taskDelay);                     
		
		plugin.enabled = true;
	}

	public static void disableOtherBounds() {
        // Stop any running scheduler tasks
        if (syncTaskId != null) syncTaskId.cancel();
        if (aSyncTaskId != null) {
            Bukkit.getScheduler().cancelTask(aSyncTaskId.getTaskId());
        }
		plugin.enabled = false;
	}
    
    @Override
	public void onDisable() {
        disableOtherBounds();
    };
}