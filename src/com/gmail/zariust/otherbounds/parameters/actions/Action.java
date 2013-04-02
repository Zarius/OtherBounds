package com.gmail.zariust.otherbounds.parameters.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.zariust.otherbounds.Log;

public abstract class Action {
    protected static Set<Action> actions = new HashSet<Action>();

    public abstract boolean act(Occurrence occurrence);

    public static boolean registerAction(Action register) {
        Log.high("Actions - registering: " + register.toString());
        actions.add(register);
        return false;
    }

    public static List<Action> parseNodes(ConfigurationSection node) {
        List<Action> actionsList = new ArrayList<Action>();
        for (Action action : actions) {
            actionsList.addAll(action.parse(node));
        }
        return actionsList;
    }

    abstract public List<Action> parse(ConfigurationSection parseMe);

    public static void registerDefaultActions() {
        registerAction(new DamageAction(null, null));
        registerAction(new MessageAction(null, null));
        registerAction(new PotionAction(null, null));
        registerAction(new SoundAction(null, null));
        registerAction(new CommandAction(null));
    }

}
