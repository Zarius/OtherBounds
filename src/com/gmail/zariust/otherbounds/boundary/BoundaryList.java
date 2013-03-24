package com.gmail.zariust.otherbounds.boundary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;

public class BoundaryList {
    Map <World, List<Boundary>> worldMap;
    Map <String, List<Boundary>> playerMap;

    public BoundaryList() {
        worldMap  = new HashMap<World, List<Boundary>>(); // TODO: why does this need to be world and not string?
        playerMap = new HashMap<String, List<Boundary>>();
    }
    
    public List<Boundary> get(World world) {
        return worldMap.get(world);
    }

    public List<Boundary> get(String playerName) {
        return playerMap.get(playerName);
    }
    
    public Set<String> getPlayers() {
    	return playerMap.keySet();
    }
    
    // **** Add ****
    public void add(World world, Boundary boundary) {
        List<Boundary> boundaryList = worldMap.get(world);
        
        if (boundaryList == null) {
            boundaryList = new ArrayList<Boundary>();
        }
        
        boundaryList.add(boundary);
        worldMap.put(world, boundaryList);
    }


    public void add(String playerName, Boundary boundary) {
        List<Boundary> boundaryList = playerMap.get(playerName);
        
        if (boundaryList == null) {
            boundaryList = new ArrayList<Boundary>();
        }
        
        boundaryList.add(boundary);
        playerMap.put(playerName, boundaryList);
    }

    // **** Contains ****
    public boolean contains(World world, Boundary boundary) {
        List<Boundary> boundaryList = worldMap.get(world);
        
        if (boundaryList == null) return false;
        if (boundaryList.contains(boundary)) return true;
        
        return false;
    }
    
    public boolean contains(String playerName, Boundary boundary) {
        List<Boundary> boundaryList = playerMap.get(playerName);
        
        if (boundaryList == null) return false;
        if (boundaryList.contains(boundary)) return true;
        
        return false;
    }


    // **** Remove ****
    public void remove(World world, Boundary boundary) {
        List<Boundary> boundaryList = worldMap.get(world);
        if (boundaryList != null) boundaryList.remove(boundary);
    }

    public void remove(String playerName, Boundary boundary) {
        List<Boundary> boundaryList = playerMap.get(playerName);
        if (boundaryList != null) boundaryList.remove(boundary);
    }

}
