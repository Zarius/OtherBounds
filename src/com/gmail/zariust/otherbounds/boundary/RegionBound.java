package com.gmail.zariust.otherbounds.boundary;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherbounds.Dependencies;
import com.gmail.zariust.otherbounds.Log;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionBound extends Boundary {
    public String regionName; // worldGuard region name 
    public String regionType; // (support worldguard/regios/izone/others perhaps?)
    
    public RegionBound(String regionName2) {
		this.regionName = regionName2;
	}

	@Override
    public boolean isInside(Player player, Boundary boundary) {
        if (Dependencies.hasWorldGuard()) {
        	Set<String> regions = getRegions(player);
        	Log.dMsg("Regionname is: "+regionName+", Player in regions: "+regions.toString());
        	
        	if (regions.contains(regionName)) {
        		return true;
        	}
        }
        return false;
    }
    
    @Override
	public String toString() {
        return "RegionBound ("+this.name+")";
      }

	private Set<String> getRegions(Player player) {
		Set<String> regions = new HashSet<String>();
		World world = player.getWorld();
		Location location = player.getLocation();
		
		Map<String, ProtectedRegion> regionMap = Dependencies.getWorldGuard().getGlobalRegionManager().get(world).getRegions();
		Vector vec = new Vector(location.getX(), location.getY(), location.getZ());
		for(String region : regionMap.keySet()) {
			if(regionMap.get(region).contains(vec))
				regions.add(region);
		}
		
		return regions;
	}    
}