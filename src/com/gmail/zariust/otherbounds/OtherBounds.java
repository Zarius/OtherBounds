package com.gmail.zariust.otherbounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.zariust.otherbounds.boundary.BoundaryList;
import com.gmail.zariust.otherbounds.common.Verbosity;

public class OtherBounds extends JavaPlugin 
{
    
    public static Map <Player, Effects> damageList;
    public static BoundaryList boundaryList;

    // usefull??
    public static Map<String, List<Long>> profileMap;

    static OtherBoundsConfig config;
    static Logger log;
    protected static Random rng;    
    private static Server server;
    static OtherBounds plugin;
	boolean enabled;
    
    ListenerPlayer playerListener;
    
    public static String pluginName;
    public static String pluginVersion;    
    
    static int syncTaskId = 0;
    static int aSyncTaskId = 0;

    public OtherBounds() {
        
        playerListener = new ListenerPlayer();
        
        boundaryList = new BoundaryList();
        
        // this list is used to store the last entity to damage another entity (along with the weapon used and range, if applicable)
        damageList = new HashMap<Player, Effects>();
        
        // this is used to store profiling information (milliseconds taken to complete function runs)
        profileMap = new HashMap<String, List<Long>>();
        profileMap.put("PLAYER_MOVE", new ArrayList<Long>());
    
            
        rng = new Random();
        log = Logger.getLogger("Minecraft");
    }

    @Override
	public void onEnable() {
        OtherBounds.server = getServer();
        OtherBounds.plugin = this;
        pluginName = this.getDescription().getName();
        pluginVersion = this.getDescription().getVersion();
        
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
        aSyncTaskId = server.getScheduler().scheduleAsyncRepeatingTask(OtherBounds.plugin, aSyncRunner, OtherBoundsConfig.taskDelay, OtherBoundsConfig.taskDelay);                     

        // sync - runs every x ticks and checks if players need to be damaged
        RunSync syncRunner = new RunSync();
        syncTaskId = server.getScheduler().scheduleSyncRepeatingTask(OtherBounds.plugin, syncRunner, OtherBoundsConfig.taskDelay+10, OtherBoundsConfig.taskDelay);                     
		
		plugin.enabled = true;
	}

	public static void disableOtherBounds() {
		server.getScheduler().cancelAllTasks();		
		plugin.enabled = false;
	}
    
    @Override
	public void onDisable() {
        // Stop any running scheduler tasks
        //server.getScheduler().cancelAllTasks();  // does this cancel only this plugins tasks?
        server.getScheduler().cancelTask(syncTaskId);
        server.getScheduler().cancelTask(aSyncTaskId);
    };
    
    /**
     * logWarning - display a warning log message with a standard prefix
     * 
     * @param msg Message to be displayed
     */
    static void logWarning(String msg) {
        log.warning("["+pluginName+":"+pluginVersion+"] "+msg);
    }
    
    /**
     * logInfo - display an info log message with a standard prefix
     * 
     * @param msg Message to be displayed
     */
    static void logInfo(String msg) {
        log.info("["+pluginName+":"+pluginVersion+"] "+msg);
    }

	// LogInfo & LogWarning - if given a level will report the message
	// only for that level & above
	public static void logInfo(String msg, Verbosity level) {
		if (OtherBoundsConfig.verbosity.exceeds(level)) logInfo(msg);
	}
	public static void logWarning(String msg, Verbosity level) {
		if (OtherBoundsConfig.verbosity.exceeds(level)) logWarning(msg);
	}

}