package com.gmail.zariust.otherbounds.boundary;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherbounds.OtherBounds;
import com.gmail.zariust.otherbounds.common.Verbosity;

public class CircleBound extends Boundary {
    double radius;

    int radiusSq;
    int definiteSq;

    public CircleBound(double X, double Z, double Radius)
    {
      this.centerX = X;
      this.centerZ = Z;
      this.radius = Radius;
      this.radiusSq = (int)(this.radius * this.radius);
      this.definiteSq = (int)Math.sqrt(0.5D * this.radiusSq);
    }
    
    public String toString() {
      return "Circlebound ("+this.name+") X: " + this.centerX + " Z: " + this.centerZ + " Radius: " + this.radius + " Safemessage: "+this.safeMessage+" Dangermessage: "+this.dangerMessage;
    }
    
    /* (non-Javadoc)  Circle isInside code courtesy of Reil, used with permission (https://github.com/Reil/rBorders/commit/b99da976a8406120e65623689d44a5b6b5493d2f#src/com/reil/bukkit/rBorder/BorderPlugin.java)
     * @see com.gmail.zariust.bukkit.otherbounds.boundary.Boundary#isInside(java.lang.String, com.gmail.zariust.bukkit.otherbounds.boundary.Boundary)
     */
    @Override
    public boolean isInside(Player player, Boundary boundary) {
        CircleBound amIHere = (CircleBound)boundary;
        Location checkHere = player.getLocation();
            int X = (int) Math.abs(amIHere.centerX - checkHere.getBlockX());
            int Z = (int) Math.abs(amIHere.centerZ - checkHere.getBlockZ());
            // If statements are cheaper than squaring twice!
            // Definitely in the circle
            if (X < amIHere.definiteSq && Z < amIHere.definiteSq)
                return true;
            // Definitely not in the circle?
            if (X > amIHere.radius || Z > amIHere.radius)
                return false;
            // Must know for sure.
            if ( X*X + Z*Z < amIHere.radiusSq )
                return true;
        
        return false;
    }
    

}