package com.gmail.zariust.otherbounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event;

import com.gmail.zariust.otherbounds.boundary.Boundary;
import com.gmail.zariust.otherbounds.boundary.BoundaryList;
import com.gmail.zariust.otherbounds.common.Verbosity;
import com.gmail.zariust.otherdrops.parameters.actions.DamageAction;

public class Main extends JavaPlugin 
{
    
    public static HashMap <Player, Effects> damageList;
    public static BoundaryList boundaryList;

    // usefull??
    public static HashMap<String, List<Long>> profileMap;

    private static Config config;
    private static Logger log;
    protected static Random rng;    
    private static Server server;
    private static Main plugin;
    
    ListenerPlayer playerListener;
    
    public static String pluginName;
    public static String pluginVersion;    
    
    int syncTaskId = 0;
    int aSyncTaskId = 0;

    public Main() {
        
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

    public void onEnable() {
        Main.server = getServer();
        Main.plugin = this;
        pluginName = this.getDescription().getName();
        pluginVersion = this.getDescription().getVersion();
        
        // Load the config files
        Main.config = new Config(this);
        config.loadConfig();
        
        // Grab plugin manager
        // Register Events (OnPlayerMove if enabled in options?)
        PluginManager pm = getServer().getPluginManager();
        //pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Config.pri, this);

        // Set up scheduler tasks
        // async - runs every x ticks and checks if players are in/outside boundaries
        RunAsync aSyncRunner = new RunAsync(this);
        aSyncTaskId = server.getScheduler().scheduleAsyncRepeatingTask(plugin, aSyncRunner, Config.taskDelay, Config.taskDelay);                     

        // sync - runs every x ticks and checks if players need to be damaged
        RunSync syncRunner = new RunSync();
        syncTaskId = server.getScheduler().scheduleSyncRepeatingTask(plugin, syncRunner, Config.taskDelay+10, Config.taskDelay);                     
    };
    
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
		if (Config.verbosity.exceeds(level)) logInfo(msg);
	}
	public static void logWarning(String msg, Verbosity level) {
		if (Config.verbosity.exceeds(level)) logWarning(msg);
	}

}