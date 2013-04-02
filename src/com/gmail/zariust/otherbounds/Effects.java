package com.gmail.zariust.otherbounds;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.gmail.zariust.otherbounds.parameters.actions.Action;

class Effects {
    
    // This class should be read-only in the Sync task and write/read from the Async task only
    int damagePerCheck = 0;
    int invertedDamagePerCheck = 0;
    Boolean glow = false;
    Location teleportTo = null;
    List<Action> actions = new ArrayList<Action>();
}