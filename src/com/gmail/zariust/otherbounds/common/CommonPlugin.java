package com.gmail.zariust.otherbounds.common;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventPriority;

public final class CommonPlugin {
	static public Verbosity getConfigVerbosity(YamlConfiguration config) {
		String verb_string = config.getString("verbosity", "normal");
		if(verb_string.equalsIgnoreCase("low")) return Verbosity.LOW;
		else if(verb_string.equalsIgnoreCase("high")) return Verbosity.HIGH;
		else if(verb_string.equalsIgnoreCase("highest")) return Verbosity.HIGHEST;
		else if(verb_string.equalsIgnoreCase("extreme")) return Verbosity.EXTREME;
		else return Verbosity.NORMAL;
	}

	static public EventPriority getConfigPriority(YamlConfiguration config) {
		String priority_string = config.getString("priority", "lowest");
		if(priority_string.equalsIgnoreCase("low"))	 return EventPriority.LOW;
		else if(priority_string.equalsIgnoreCase("normal")) return EventPriority.NORMAL;
		else if(priority_string.equalsIgnoreCase("high")) return EventPriority.HIGH;
		else if(priority_string.equalsIgnoreCase("highest")) return EventPriority.HIGHEST;
		else return EventPriority.LOWEST;
	}
	
	static public <E extends Enum<E>> E enumValue(Class<E> clazz, String name) {
		try {
			return Enum.valueOf(clazz, name);
		} catch(IllegalArgumentException e) {}
		return null;
	}
}
