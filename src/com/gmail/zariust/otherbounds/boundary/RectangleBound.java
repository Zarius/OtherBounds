package com.gmail.zariust.otherbounds.boundary;

import org.bukkit.entity.Player;

public class RectangleBound extends Boundary {
    public int limitLength;
    public int limitWidth;

    public RectangleBound(Double centerX, Double centerZ, Integer length,
			Integer width) {
		// TODO Auto-generated constructor stub
	}
    
	@Override
    public boolean isInside(Player player, Boundary boundary) {
        // TODO Auto-generated method stub
        return false;
    }
	
    public String toString() {
        return "RegionBound ("+this.name+")";
      }

}