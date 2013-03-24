package com.gmail.zariust.otherbounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.gmail.zariust.otherbounds.common.Verbosity;

class RunSync implements Runnable {

    public void run() {
    	OtherBounds.logInfo("Sync run... damageList: "+OtherBounds.damageList.keySet().toString(), Verbosity.HIGHEST);
    	List<Player> removeList = new ArrayList<Player>();
        Map<Player, Effects> iterateMap = new HashMap<Player, Effects>();
    	iterateMap.putAll(OtherBounds.damageList);
    	
        // Check if player in list
    	for (Player player : iterateMap.keySet()) {
    		if (!player.isOnline() || player.isDead()) {
    			// add player to new list as we cannot remove from the
    			// damageList whilst iterating through it.
    			removeList.add(player);
    			continue;
    		}
    		Effects effects = iterateMap.get(player);
    		
    		int damage = effects.damagePerCheck + effects.invertedDamagePerCheck;
    		if (damage > 0) { 
    			OtherBounds.logInfo("Damaging player ("+player.getName()+") for "+damage+" damage.", Verbosity.HIGHEST);
    			player.damage(damage);
    		} else if (damage < 0) {
    			player.setHealth(player.getHealth()+damage);
    		}
    	}
    	
    	for (Player player : removeList) {
    		OtherBounds.damageList.remove(player);
    	}
        // damage player

        // update any effects
    }
}