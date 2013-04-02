package com.gmail.zariust.otherbounds.parameters.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.zariust.otherbounds.Log;

public class PotionAction extends Action {
    // "potioneffect: "
    // message.player, message.radius@<r>, message.world, message.server
    public enum PotionEffectActionType {
        ATTACKER, VICTIM, RADIUS, WORLD, SERVER, DROP
    }

    static Map<String, PotionEffectActionType> matches = new HashMap<String, PotionEffectActionType>();
    static {
        matches.put("potioneffect", PotionEffectActionType.ATTACKER);
        matches.put("potioneffect.attacker", PotionEffectActionType.ATTACKER);
        matches.put("potioneffect.victim", PotionEffectActionType.VICTIM);
        matches.put("potioneffect.target", PotionEffectActionType.VICTIM);
        matches.put("potioneffect.server", PotionEffectActionType.SERVER);
        matches.put("potioneffect.world", PotionEffectActionType.WORLD);
        matches.put("potioneffect.global", PotionEffectActionType.SERVER);
        matches.put("potioneffect.all", PotionEffectActionType.SERVER);
        matches.put("potioneffect.radius", PotionEffectActionType.RADIUS);

        matches.put("potioneffects", PotionEffectActionType.ATTACKER);
        matches.put("potioneffects.attacker", PotionEffectActionType.ATTACKER);
        matches.put("potioneffects.victim", PotionEffectActionType.VICTIM);
        matches.put("potioneffects.target", PotionEffectActionType.VICTIM);
        matches.put("potioneffects.server", PotionEffectActionType.SERVER);
        matches.put("potioneffects.world", PotionEffectActionType.WORLD);
        matches.put("potioneffects.global", PotionEffectActionType.SERVER);
        matches.put("potioneffects.all", PotionEffectActionType.SERVER);
        matches.put("potioneffects.radius", PotionEffectActionType.RADIUS);

    }

    protected PotionEffectActionType           potionEffectActionType;
    protected double                           radius  = 10;                                           // default
                                                                                                        // to
                                                                                                        // 10
                                                                                                        // blocks

    private Collection<PotionEffect>           effects = new ArrayList<PotionEffect>();

    public PotionAction(Collection<PotionEffect> effectsList) {
        this.effects = effectsList;
    }

    public PotionAction(Object object, PotionEffectActionType potionEffectType2) {
        potionEffectActionType = potionEffectType2;

        if (object instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> stringList = (List<String>) object;
            for (String effect : stringList) {
                PotionEffect singleEffect = getEffect(effect);
                if (singleEffect != null)
                    effects.add(singleEffect);
            }
        } else if (object instanceof String) {
            PotionEffect singleEffect = getEffect((String) object);
            if (singleEffect != null)
                effects.add(singleEffect);
        }
    }

    @Override
    public boolean act(Occurrence occurrence) {
        if (this.effects == null) return false;
        Log.normal("Acting on potioneffect!");
        switch (potionEffectActionType) {
        case ATTACKER:
            if (occurrence.getAttacker() != null) applyEffect(occurrence.getAttacker());
            break;
        case VICTIM:
            if (occurrence.getVictim() != null) applyEffect(occurrence.getVictim());
            break;
        case RADIUS:
            // occurence.getLocation().getRadiusPlayers()? - how do we get
            // players around radius without an entity?
            Location loc = occurrence.getLocation();
            for (Player player : loc.getWorld().getPlayers()) {
                if (player.getLocation().getX() > (loc.getX() - radius)
                        || player.getLocation().getX() < (loc.getX() + radius))
                    if (player.getLocation().getY() > (loc.getY() - radius)
                            || player.getLocation().getY() < (loc.getY() + radius))
                        if (player.getLocation().getZ() > (loc.getZ() - radius)
                                || player.getLocation().getZ() < (loc.getZ() + radius))
                            applyEffect(player);
            }

            break;
        case SERVER:
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                applyEffect(player);
            }
            break;
        case WORLD:
            for (Player player : occurrence.getLocation().getWorld()
                    .getPlayers()) {
                applyEffect(player);
            }
            break;
        default:
            break;
        }

        return true;
    }

    private void applyEffect(LivingEntity lEnt) {
        for (PotionEffect eff : this.effects) {
            lEnt.removePotionEffect(eff.getType());
        }
        lEnt.addPotionEffects(this.effects);
    }

    // @Override
    @Override
    public List<Action> parse(ConfigurationSection parseMe) {
        List<Action> actions = new ArrayList<Action>();

        for (String key : matches.keySet()) {
            if (parseMe.get(key) != null)
                actions.add(new PotionAction(parseMe.get(key), matches.get(key)));
        }

        return actions;
    }

    private static PotionEffect getEffect(String effects) {
        String[] split = effects.split("@");
        int duration = 100;
        int strength = 4;

        try {
            if (split.length > 1)
                duration = Integer.parseInt(split[1]);
        } catch (NumberFormatException ex) {
            Log.normal("Potioneffect: invalid duration (" + split[1] + ")");
        }

        try {
            if (split.length > 2)
                strength = Integer.parseInt(split[2]);
        } catch (NumberFormatException ex) {
            Log.normal("Potioneffect: invalid potion level (" + split[2] + ")");
        }

        // modify strength to match in-game values
        if (strength > 0)
            strength--;

        if (split[0].equalsIgnoreCase("nausea"))
            split[0] = "CONFUSION";
        PotionEffectType effect = PotionEffectType.getByName(split[0]);
        if (effect == null) {
            Log.normal("PotionEffect: INVALID effect (" + split[0] + ")");
            return null;
        }
        Log.high("PotionEffect: adding effect (" + split[0] + ", duration: "
                + duration + ", strength: " + strength + ")");

        // FIXME: parse time and modifier
        return new PotionEffect(effect, duration, strength);
    }
}
