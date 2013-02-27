package com.gmail.zariust.otherbounds;
import org.bukkit.Location;

class Effects {
    
    // This class should be read-only in the Sync task and write/read from the Async task only
    int damagePerCheck = 0;
    int invertedDamagePerCheck = 0;
    Boolean glow = false;
    Location teleportTo = null;
}