package com.gmail.zariust.otherbounds.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import static org.bukkit.Material.*;

public enum MaterialGroup {
	// Blocks
	ANY_FURNACE(FURNACE, BURNING_FURNACE),
	ANY_SIGN(SIGN_POST, WALL_SIGN, SIGN),
	ANY_REDSTONE_ORE(REDSTONE_ORE, GLOWING_REDSTONE_ORE),
	ANY_REDSTONE_TORCH(REDSTONE_TORCH_ON, REDSTONE_TORCH_OFF),
	ANY_DIODE(DIODE_BLOCK_ON, DIODE_BLOCK_OFF, DIODE),
	ANY_PISTON(PISTON_BASE, PISTON_STICKY_BASE, PISTON_EXTENSION, PISTON_MOVING_PIECE),
	// Tools
	ANY_SPADE(WOOD_SPADE, STONE_SPADE, GOLD_SPADE, IRON_SPADE, DIAMOND_SPADE),
	ANY_AXE(WOOD_AXE, STONE_AXE, GOLD_AXE, IRON_AXE, DIAMOND_AXE),
	ANY_HOE(WOOD_HOE, STONE_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE),
	ANY_PICKAXE(WOOD_PICKAXE, STONE_PICKAXE, GOLD_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE),
	ANY_SWORD(WOOD_SWORD, STONE_SWORD, GOLD_SWORD, IRON_SWORD, DIAMOND_SWORD),
	ANY_BUCKET(BUCKET, LAVA_BUCKET, WATER_BUCKET, MILK_BUCKET),
	// Armour
	ANY_HELMET(LEATHER_HELMET, CHAINMAIL_HELMET, GOLD_HELMET, IRON_HELMET, DIAMOND_HELMET),
	ANY_CHESTPLATE(LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, GOLD_CHESTPLATE, IRON_CHESTPLATE, DIAMOND_CHESTPLATE),
	ANY_LEGGINGS(LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, GOLD_LEGGINGS, IRON_LEGGINGS, DIAMOND_LEGGINGS),
	ANY_BOOTS(LEATHER_BOOTS, CHAINMAIL_BOOTS, GOLD_BOOTS, IRON_BOOTS, DIAMOND_BOOTS),
	// Minecarts
	ANY_MINECART(MINECART, POWERED_MINECART, STORAGE_MINECART),
	ANY_RAIL(RAILS, POWERED_RAIL, DETECTOR_RAIL),
	// Wildcards
	ANY_TOOL(Arrays.asList(FLINT_AND_STEEL, BOW, FISHING_ROD, SADDLE), ANY_SPADE, ANY_AXE, ANY_HOE, ANY_PICKAXE, ANY_SWORD, ANY_BUCKET),
	ANY_WEAPON(Arrays.asList(BOW, ARROW), ANY_SWORD),
	ANY_ARMOR(ANY_HELMET, ANY_CHESTPLATE, ANY_LEGGINGS, ANY_BOOTS),
	ANY_ARMOUR(ANY_ARMOR),
	ANY_PROJECTILE(FIRE, SNOW_BALL, EGG, ARROW, FISHING_ROD),
	// Add any new ones before this line
	ANY_ITEM, ANY_BLOCK, ANY_OBJECT;
	private static Map<String, MaterialGroup> lookup = new HashMap<String, MaterialGroup>();
	private ArrayList<Material> mat;
	
	static {
		for(Material mat : Material.values()) {
			ANY_OBJECT.mat.add(mat);
			if(mat.isBlock()) ANY_BLOCK.mat.add(mat);
			else ANY_ITEM.mat.add(mat);
		}
		for(MaterialGroup group : values())
			lookup.put(group.name(), group);
	}
	
	private void add(List<Material> materials) {
		mat.addAll(materials);
	}
	
	private MaterialGroup(Material... materials) {
		this();
		add(Arrays.asList(materials));
	}
	
	private MaterialGroup(MaterialGroup... merge) {
		this();
		for(MaterialGroup group : merge)
			add(group.mat);
	}
	
	private MaterialGroup(List<Material> materials, MaterialGroup... merge) {
		this(merge);
		add(materials);
	}
	
	private MaterialGroup() {
		mat = new ArrayList<Material>();
	}
	
	@SuppressWarnings("unchecked")
	public List<Material> materials() {
		return (List<Material>) mat.clone();
	}

	public static MaterialGroup get(String string) {
		return lookup.get(string.toUpperCase());
	}

	public static Set<String> all() {
		return lookup.keySet();
	}

	public static boolean isValid(String string) {
		return lookup.containsKey(string);
	}
	
	public boolean isBlock() {
		for(Material obj : mat)
			if(obj.isBlock()) return true;
		return false;
	}
	
	public boolean isItem() {
		for(Material obj : mat)
			if(!obj.isBlock()) return true;
		return false;
	}

	public boolean contains(Material material) {
		return mat.contains(material);
	}
}
