// Log.java - Bukkit Plugin Logger Wrapper
// Copyright (C) 2012 Zarius Tularial
//
// This file released under Evil Software License v1.1
// <http://fredrikvold.info/ESL.htm>

package com.gmail.zariust.otherbounds;

import com.gmail.zariust.otherbounds.common.Verbosity;

public class Log {

	// LogInfo - display messages with a standard prefix
	private static void logInfo(String msg) {
		OtherBounds.log.info("["+OtherBounds.pluginName+":"+OtherBounds.pluginVersion+"] "+msg);
	}


	public static void low(String msg) {
		if (OtherBoundsConfig.getVerbosity().exceeds(Verbosity.LOW)) logInfo(msg);
	}

	public static void normal(String msg) {
		if (OtherBoundsConfig.getVerbosity().exceeds(Verbosity.NORMAL)) logInfo(msg);
	}

	public static void high(String msg) {
		if (OtherBoundsConfig.getVerbosity().exceeds(Verbosity.HIGH)) logInfo(msg);
	}

	public static void highest(String msg) {
		if (OtherBoundsConfig.getVerbosity().exceeds(Verbosity.HIGHEST)) logInfo(msg);
	}

	public static void extreme(String msg) {
		if (OtherBoundsConfig.getVerbosity().exceeds(Verbosity.EXTREME)) logInfo(msg);
	}

	// TODO: This is only for temporary debug purposes.
	public static void stackTrace() {
		if(OtherBoundsConfig.getVerbosity().exceeds(Verbosity.EXTREME)) Thread.dumpStack();
	}

	public static void dMsg(String msg) {
		if (OtherBoundsConfig.verbosity.exceeds(Verbosity.HIGHEST)) logInfo(msg);
	}
}
