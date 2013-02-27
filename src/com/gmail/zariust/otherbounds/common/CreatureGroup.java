package com.gmail.zariust.otherbounds.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.CreatureType;
import static org.bukkit.entity.CreatureType.*;

public enum CreatureGroup {
	CREATURE_HOSTILE(CREEPER, GHAST, GIANT, SKELETON, SLIME, SPIDER, ZOMBIE),
	CREATURE_FRIENDLY(COW, CHICKEN, PIG, SHEEP, SQUID),
	CREATURE_NEUTRAL(PIG_ZOMBIE, WOLF),
	CREATURE_ANIMAL(COW, CHICKEN, PIG, SHEEP, WOLF),
	CREATURE_UNDEAD(PIG_ZOMBIE, ZOMBIE, SKELETON),
	CREATURE_WATER(SQUID),
	// Add any new ones before this line
	CREATURE_ANY;
	private static Map<String, CreatureGroup> lookup = new HashMap<String, CreatureGroup>();
	private ArrayList<CreatureType> mob;
	
	static {
		for(CreatureType mob : CreatureType.values()) {
			CREATURE_ANY.mob.add(mob);
		}
		for(CreatureGroup group : values())
			lookup.put(group.name(), group);
	}
	
	private void add(List<CreatureType> materials) {
		mob.addAll(materials);
	}
	
	private CreatureGroup(CreatureType... materials) {
		this();
		add(Arrays.asList(materials));
	}
	
	private CreatureGroup(CreatureGroup... merge) {
		this();
		for(CreatureGroup group : merge)
			add(group.mob);
	}
	
	private CreatureGroup(List<CreatureType> materials, CreatureGroup... merge) {
		this(merge);
		add(materials);
	}
	
	private CreatureGroup() {
		mob = new ArrayList<CreatureType>();
	}
	
	@SuppressWarnings("unchecked")
	public List<CreatureType> creatures() {
		return (List<CreatureType>) mob.clone();
	}

	public static CreatureGroup get(String string) {
		return lookup.get(string.toUpperCase());
	}

	public static Set<String> all() {
		return lookup.keySet();
	}

	public static boolean isValid(String string) {
		return lookup.containsKey(string);
	}

	public boolean contains(CreatureType material) {
		return mob.contains(material);
	}
}
