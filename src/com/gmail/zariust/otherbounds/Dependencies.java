// OtherBounds - a Bukkit plugin
// Copyright (C) 2011 Zarius Tularial
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.otherbounds;

import java.io.IOException;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import com.gmail.zariust.metrics.Metrics;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Dependencies {
	// Plugin Dependencies
	public static WorldGuardPlugin worldGuard = null;			// for WorldGuard support
	boolean enabled;

	static String foundPlugins;
	static String notFoundPlugins;
	
	private static Metrics metrics = null;

	public static void init() {
		try {
			foundPlugins = ""; notFoundPlugins = ""; // need to reset variables to allow for reloads
			enableMetrics();
			worldGuard = (WorldGuardPlugin)getPlugin("WorldGuard");
			
			if (!foundPlugins.isEmpty()) Log.normal("Found plugin(s): '"+foundPlugins+"'");
			if (!notFoundPlugins.isEmpty()) Log.high("Plugin(s) not found: '"+notFoundPlugins+"' (OtherBounds will continue to load)");
		} catch (Exception e) {
			Log.normal("Failed to load one or more optional dependencies - continuing OtherBounds startup.");
			e.printStackTrace();
		}
	}

	public static Plugin getPlugin(String name) {
		Plugin plugin = OtherBounds.plugin.getServer().getPluginManager().getPlugin(name);

		if (plugin == null) {
			if (notFoundPlugins.isEmpty()) notFoundPlugins += name;
			else notFoundPlugins += ", " + name;
		} else {
			if (foundPlugins.isEmpty()) foundPlugins += name;
			else foundPlugins += ", " + name;
		}

		return plugin;
	}

	public static boolean hasPermission(Permissible who, String permission) {
		if (who instanceof ConsoleCommandSender) return true;
		boolean perm = who.hasPermission(permission);
		if (!perm) {
			Log.highest("SuperPerms - permission ("+permission+") denied for "+who.toString());
		} else {
			Log.highest("SuperPerms - permission ("+permission+") allowed for "+who.toString());
		}
		return perm;
	}

	public static void enableMetrics()
	{
		try {
			metrics = new Metrics(OtherBounds.plugin);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
	}
	
	public static boolean hasWorldGuard() {
		return Dependencies.worldGuard != null;
	}

	public static WorldGuardPlugin getWorldGuard() {
		return Dependencies.worldGuard;
	}

	public static boolean hasMetrics() {
		return Dependencies.metrics != null;
	}

	public static Metrics getMetrics() {
		return Dependencies.metrics;
	}
}
