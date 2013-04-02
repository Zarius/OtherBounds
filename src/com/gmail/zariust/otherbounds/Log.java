// Log.java - Bukkit Plugin Logger Wrapper
// Copyright (C) 2012 Zarius Tularial
//
// This file released under Evil Software License v1.1
// <http://fredrikvold.info/ESL.htm>

package com.gmail.zariust.otherbounds;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

public class Log {
    // Place these in your plugins onEnable:
    //      pluginName = this.getDescription().getName();
    //      pluginVersion = this.getDescription().getVersion();

    
    private static Logger log = Logger.getLogger("Minecraft");
    public static Verbosity verbosity = Verbosity.NORMAL;

    /**
     * logInfo - display a log message with a standard prefix
     * 
     * @param msg Message to be displayed
     */
    private static void logInfo(String msg) {
        log.info("["+OtherBounds.pluginName+":"+OtherBounds.pluginVersion+"] "+msg);
    }

    /**
     * logWarning - display a warning log message with a standard prefix
     * 
     * @param msg Message to be displayed
     */
    static void logWarning(String msg) {
        log.warning("["+OtherBounds.pluginName+":"+OtherBounds.pluginVersion+"] "+msg);
    }


    public static void warning(String msg) {
        logWarning(msg);
    }

    public static void low(String msg) {
        if (verbosity.exceeds(Verbosity.LOW)) logInfo(msg);
    }

    public static void normal(String msg) {
        if (verbosity.exceeds(Verbosity.NORMAL)) logInfo(msg);
    }

    public static void high(String msg) {
        if (verbosity.exceeds(Verbosity.HIGH)) logInfo(msg);
    }

    public static void highest(String msg) {
        if (verbosity.exceeds(Verbosity.HIGHEST)) logInfo(msg);
    }

    public static void extreme(String msg) {
        if (verbosity.exceeds(Verbosity.EXTREME)) logInfo(msg);
    }

    // TODO: This is only for temporary debug purposes.
    public static void stackTrace() {
        if(verbosity.exceeds(Verbosity.EXTREME)) Thread.dumpStack();
    }

    public static void dMsg(String msg) {
        if (verbosity.exceeds(Verbosity.HIGHEST)) logInfo(msg);
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
