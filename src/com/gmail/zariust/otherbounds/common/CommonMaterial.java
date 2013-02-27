package com.gmail.zariust.otherbounds.common;

import java.util.List;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.material.Step;

public final class CommonMaterial {
	
	public static List<Material> getSynonymValues(String string) {
		return MaterialGroup.get(string).materials();
	}
	
	public static Set<String> getValidSynonyms() {
		return MaterialGroup.all();
	}
	
	public static boolean isValidSynonym(String string) {
		return MaterialGroup.isValid(string);
	}
	
	public static boolean isSynonymFor(String string, Material material) {
		if(!isValidSynonym(string)) return false;
		return getSynonymValues(string).contains(material);
	}
	
	// Colors
	public static int getWoolColor(DyeColor color) {
		return color.getData();
	}

	public static int getDyeColor(DyeColor color) {
		return 0xF - color.getData();
	}
	
	@SuppressWarnings("incomplete-switch")
	public static Integer parseBlockOrItemData(Material mat, String state) {
		switch(mat) {
		case LOG:
		case LEAVES:
		case SAPLING:
			TreeSpecies species = TreeSpecies.valueOf(state);
			if(species != null) return (int) species.getData();
			break;
		case WOOL:
			DyeColor wool = DyeColor.valueOf(state);
			if(wool != null) return getWoolColor(wool);
			break;
		case DOUBLE_STEP:
		case STEP:
			Material step = Material.valueOf(state);
			if(step == null) throw new IllegalArgumentException("Unknown material " + state);
			switch(step) {
			case STONE: return 0;
			case COBBLESTONE: return 3;
			case SANDSTONE: return 1;
			case WOOD: return 2;
			default:
				throw new IllegalArgumentException("Illegal step material " + state);
			}
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public static String getBlockOrItemData(Material mat, int data) {
		switch(mat) {
		case LOG:
		case LEAVES:
		case SAPLING:
			return TreeSpecies.getByData((byte)((0x3) & data)).toString(); // (0x3) & data to remove leaf decay flag
		case WOOL:
			return DyeColor.getByData((byte)data).toString();
		case DOUBLE_STEP:
		case STEP:
			Step step = new Step(mat, (byte)data);
			return step.getMaterial().toString();
		}
		if(data > 0) return Integer.toString(data);
		return "";
	}
}
