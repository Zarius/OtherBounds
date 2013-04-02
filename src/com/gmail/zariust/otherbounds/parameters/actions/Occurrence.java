package com.gmail.zariust.otherbounds.parameters.actions;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class Occurrence {
    LivingEntity attacker;
    LivingEntity victim;
    Location location;
    
    
    public Occurrence(LivingEntity attacker, LivingEntity victim,
            Location location) {
        super();
        this.attacker = attacker;
        this.victim = victim;
        this.location = location;
    }

    /** Returns either a player or creature that attacked
     * @return
     */
    public LivingEntity getAttacker() {
        return attacker;
    }

    /** Returns either a player or creature victim
     * 
     * @return
     */
    public LivingEntity getVictim() {
        return victim;
    }

    public Location getLocation() {
        return location;
    }

}
