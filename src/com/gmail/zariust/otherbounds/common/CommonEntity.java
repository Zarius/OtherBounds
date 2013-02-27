package com.gmail.zariust.otherbounds.common;

import org.bukkit.Material;
import static org.bukkit.Material.*;
import org.bukkit.entity.*;

import static org.bukkit.entity.CreatureType.*;
import org.bukkit.material.MaterialData;

public final class CommonEntity {
	public static CreatureType getCreatureType(String name) {
		if (name == null || name.isEmpty()) return null;

		name = name.replace("CREATURE_", "");
		String argName = name.toLowerCase().replaceAll("[ -_]", "");
		argName = argName.replace("mooshroom", "mushroomcow");
		for (CreatureType creature : CreatureType.values())
		{
			if (argName.equalsIgnoreCase(creature.name().toLowerCase().replaceAll("[ -_]", ""))) return creature;
		}
		return null;
	}
	
	public static CreatureType getCreatureType(Entity e) {
		if(e instanceof Blaze) 		return BLAZE;
		if(e instanceof CaveSpider) return CAVE_SPIDER;
		if(e instanceof Chicken)	return CHICKEN;
		if(e instanceof Creeper)	return CREEPER;
		if(e instanceof EnderDragon)return ENDER_DRAGON;
		if(e instanceof Enderman)   return ENDERMAN;
		if(e instanceof Ghast)		return GHAST;
		if(e instanceof Giant)		return GIANT;
		if(e instanceof MagmaCube)  return MAGMA_CUBE;
		if(e instanceof MushroomCow)return MUSHROOM_COW;
		if(e instanceof Pig)		return PIG;
		if(e instanceof PigZombie)	return PIG_ZOMBIE;
		if(e instanceof Sheep)		return SHEEP;
		if(e instanceof Silverfish) return SILVERFISH;
		if(e instanceof Skeleton)	return SKELETON;
		if(e instanceof Slime)	 	return SLIME;
		if(e instanceof Snowman)    return SNOWMAN;
		if(e instanceof Squid)	 	return SQUID;
		if(e instanceof Villager)   return VILLAGER;
		if(e instanceof Wolf)	 	return WOLF;
		
		// These are supertypes of at least one of the others
		if(e instanceof Spider)	 	return SPIDER;
		if(e instanceof Zombie)	 	return ZOMBIE;
		if(e instanceof Cow)		return COW; // this must be _after_ mushroom cow (otherwise mushroom_cow will return COW)
		return null;
	}
	
	public static Material getVehicleType(Entity e) {
		if(e instanceof Boat)			 return BOAT;
		if(e instanceof PoweredMinecart) return POWERED_MINECART;
		if(e instanceof StorageMinecart) return STORAGE_MINECART;
		if(e instanceof Minecart)		 return MINECART;
		return null;
	}
	
	public static Material getProjectileType(Entity e) {
		if(e instanceof Arrow)		return ARROW;
		if(e instanceof Fish)		return FISHING_ROD;
		if(e instanceof Fireball)	return FIRE;
		if(e instanceof Egg)		return EGG;
		if(e instanceof Snowball)	return SNOW_BALL;
		return null;
	}

	public static int getCreatureData(Entity entity) {
		if(entity == null) return 0;
		CreatureType creatureType = getCreatureType(entity);
		if(creatureType == null) return 0;
		switch(creatureType) {
		case CREEPER:
			return ((Creeper)entity).isPowered() ? 1 : 0;
		case PIG:
			return ((Pig)entity).hasSaddle() ? 1 : 0;
		case SHEEP:
			return ((Sheep)entity).getColor().getData() + (((Sheep)entity).isSheared() ? 32 : 0);
		case SLIME:
			return ((Slime)entity).getSize();
		case WOLF:
			return ((Wolf)entity).isAngry() ? 1 : (((Wolf)entity).isTamed() ? 2 : 0);
		case PIG_ZOMBIE:
			return ((PigZombie)entity).getAnger();
		case ENDERMAN:
			MaterialData data = ((Enderman)entity).getCarriedMaterial();
			if(data == null) return 0;
			return data.getItemTypeId() | (data.getData() << 8);
		default:
			return 0;
		}
	}
	
	public static int getCreatureId(CreatureType type) {
		switch(type) {
	     case CREEPER: return 50;
	     case SKELETON: return 51;
	     case SPIDER: return 52;
	     case GIANT: return 53;
	     case ZOMBIE: return 54;
	     case SLIME: return 55;
	     case GHAST: return 56;
	     case PIG_ZOMBIE: return 57;
	     case ENDERMAN: return 58;
	     case CAVE_SPIDER: return 59;
	     case SILVERFISH: return 60;
	     case BLAZE: return 61;
	     case MAGMA_CUBE: return 62;
	     case ENDER_DRAGON: return 63;
	     case PIG: return 90;
	     case SHEEP: return 91;
	     case COW: return 92;
	     case CHICKEN: return 93;
	     case SQUID: return 94;
	     case WOLF: return 95;
	     case MUSHROOM_COW: return 96;
	     case SNOWMAN: return 97;
	     case VILLAGER: return 120;
		default: return 0; // Note: MONSTER is no longer supported
		}
	}
	
	public static CreatureType getCreatureType(int id) {
		switch(id) {
	     case 50: return CREEPER;
	     case 51: return SKELETON;
	     case 52: return SPIDER;
	     case 53: return GIANT;
	     case 54: return ZOMBIE;
	     case 55: return SLIME;
	     case 56: return GHAST;
	     case 57: return PIG_ZOMBIE;
	     case 58: return ENDERMAN;
	     case 59: return CAVE_SPIDER;
	     case 60: return SILVERFISH;
	     case 61: return BLAZE;
	     case 62: return MAGMA_CUBE;
	     case 63: return ENDER_DRAGON;
	     case 90: return PIG;
	     case 91: return SHEEP;
	     case 92: return COW;
	     case 93: return CHICKEN;
	     case 94: return SQUID;
	     case 95: return WOLF;
	     case 96: return MUSHROOM_COW;
	     case 97: return SNOWMAN;
	     case 120: return VILLAGER;
	     default: return null;
		}
	}

	public static Material getExplosiveType(Entity e) {
		if(e instanceof Fireball)	return Material.FIRE;
		if(e instanceof TNTPrimed)	return Material.TNT;
		return null;
	}
}
