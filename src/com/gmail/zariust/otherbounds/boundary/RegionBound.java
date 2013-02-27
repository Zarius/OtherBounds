package com.gmail.zariust.otherbounds.boundary;

import org.bukkit.entity.Player;

public class RegionBound extends Boundary {
    public String regionName; // worldGuard region name 
    public String regionType; // (support worldguard/regios/izone/others perhaps?)
    
    @Override
    public boolean isInside(Player player, Boundary boundary) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public String toString() {
        return "RegionBound ("+this.name+")";
      }

}