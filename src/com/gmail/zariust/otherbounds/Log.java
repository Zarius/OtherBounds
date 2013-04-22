// Log.java - Bukkit Plugin Logger Wrapper
// Copyright (C) 2012 Zarius Tularial
//
// This file released under Evil Software License v1.1
// <http://fredrikvold.info/ESL.htm>

package com.gmail.zariust.otherbounds;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Log {
    // Place these in your plugins onEnable:
    //      pluginName = this.getDescription().getName();
    //      pluginVersion = this.getDescription().getVersion();

    
    private static Logger log = OtherBounds.plugin.getLogger();
    public static Verbosity verbosity = Verbosity.NORMAL;
    public static String pluginName = "";
    public static String pluginVersion = "";
    static ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
 

    /**
     * logInfo - display a log message with a standard prefix
     * 
     * @param msg Message to be displayed
     */
    private static void logInfo(String msg) {
        log.info("["+pluginName+":"+pluginVersion+"] "+msg);
    }

    // LogInfo & LogWarning - if given a level will report the message
    // only for that level & above
    public static void logInfo(String msg, Verbosity level) {
        if (verbosity.exceeds(level)) {
            //if (OtherDropsConfig.gColorLogMessages) {
                ChatColor col = ChatColor.GREEN;
                switch (level) {
                case EXTREME:
                    col = ChatColor.GOLD;
                    break;
                case HIGHEST:
                    col = ChatColor.YELLOW;
                    break;
                case HIGH:
                    col = ChatColor.AQUA;
                    break;
                case NORMAL:
                    col = null;
                    break;
                case LOW:
                    col = ChatColor.GRAY;
                    break;
                default:
                    break;
                }
                console.sendMessage((col==null?"":col) + "[" + pluginName + ":"
                        + pluginVersion + "] " + (col==null?"":ChatColor.RESET)
                        + msg);
        }
    }

    /**
     * logWarning - display a warning log message with a standard prefix
     * 
     * @param msg Message to be displayed
     */
    static void logWarning(String msg) {
        log.warning("["+pluginName+":"+pluginVersion+"] "+msg);
    }


    public static void warning(String msg) {
        logWarning(msg);
    }

    public static void low(String msg) {
        if (verbosity.exceeds(Verbosity.LOW)) logInfo(msg, Verbosity.LOW);
    }

    public static void normal(String msg) {
        if (verbosity.exceeds(Verbosity.NORMAL)) logInfo(msg, Verbosity.NORMAL);
    }

    public static void high(String msg) {
        if (verbosity.exceeds(Verbosity.HIGH)) logInfo(msg, Verbosity.HIGH);
    }

    public static void highest(String msg) {
        if (verbosity.exceeds(Verbosity.HIGHEST)) logInfo(msg, Verbosity.HIGHEST);
    }

    public static void extreme(String msg) {
        if (verbosity.exceeds(Verbosity.EXTREME)) logInfo(msg, Verbosity.EXTREME);
    }

    // TODO: This is only for temporary debug purposes.
    public static void stackTrace() {
        if(verbosity.exceeds(Verbosity.EXTREME)) Thread.dumpStack();
    }

    /**
     * dMsg - used for debug messages that are expected to be removed before
     * distribution
     * 
     * @param msg
     */
    public static void dMsg(String msg) {
        // Deliberately doesn't check gColorLogMessage as I want these messages
        // to stand out in case they
        // are left in by accident
        if (verbosity.exceeds(Verbosity.HIGHEST))
            console.sendMessage(ChatColor.RED + "[" + pluginName
                    + ":" + pluginVersion + "] " + ChatColor.RESET
                    + msg);

    }
    
    public enum Verbosity {
        LOW(1), NORMAL(2), HIGH(3), HIGHEST(4), EXTREME(5);
        private final int level;
        
        private Verbosity(int lvl) {
            level = lvl;
        }
        
        public boolean exceeds(Verbosity other) {
            if(level >= other.level) return true;
            return false;
        }
    }

    static public void setConfigVerbosity(FileConfiguration config) {
        String verbosityString = config.getString("verbosity", "normal").toLowerCase();
        if(verbosityString.equals("low")) Log.verbosity = Verbosity.LOW;
        else if(verbosityString.equals("high")) Log.verbosity =  Verbosity.HIGH;
        else if(verbosityString.equals("highest")) Log.verbosity =  Verbosity.HIGHEST;
        else if(verbosityString.equals("extreme")) Log.verbosity =  Verbosity.EXTREME;
        else Log.verbosity =  Verbosity.NORMAL;
    }

    public static Verbosity getVerbosity() {
        return verbosity;
    }

}
