package com.gmail.zariust.otherbounds.parameters.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherbounds.Log;
import com.gmail.zariust.otherbounds.OtherBounds;

public class MessageAction extends Action {
    // message.player, message.radius@<r>, message.world, message.server
    public enum MessageType {
        ATTACKER, VICTIM, RADIUS, WORLD, SERVER
    }

    static Map<String, MessageType> matches = new HashMap<String, MessageType>();
    static {
        matches.put("message", MessageType.ATTACKER);
        matches.put("message.attacker", MessageType.ATTACKER);
        matches.put("message.victim", MessageType.VICTIM);
        matches.put("message.server", MessageType.SERVER);
        matches.put("message.world", MessageType.WORLD);
        matches.put("message.global", MessageType.SERVER);
        matches.put("message.all", MessageType.SERVER);
        matches.put("message.radius", MessageType.RADIUS);
    }

    protected MessageType           messageType;
    protected double                radius  = 10;
    private List<String>            messages;                                    // this
                                                                                  // can
                                                                                  // contain
                                                                                  // variables,
                                                                                  // parse
                                                                                  // at
                                                                                  // runtime

    public MessageAction(Object messageToParse, MessageType messageType2) {
        this(messageToParse, messageType2, 0);
    }

    @SuppressWarnings("unchecked")
    public MessageAction(Object messageToParse, MessageType messageType2,
            double radius) {
        if (messageToParse == null)
            return; // "Registration" passed a null value

        if (messageToParse instanceof List)
            messages = (List<String>) messageToParse;
        else
            messages = Collections.singletonList(messageToParse.toString());

        // OtherDrops.logInfo("Adding messages: "+messages.toString());

        messageType = messageType2;
        this.radius = radius;

    }

    @Override
    public boolean act(Occurrence occurence) {
        String message = getRandomMessage(occurence, this.messages);
        if (message.isEmpty())
            return false;

        Log.high("Message action - messages = " + messages.toString()
                + ", message=" + message + ", type=" + messageType.toString());

        switch (messageType) {
        case ATTACKER:
            LivingEntity lEnt = occurence.getAttacker();
            if (lEnt != null && lEnt instanceof Player) ((Player)lEnt).sendMessage(message);
            break;
        case VICTIM:
            lEnt = occurence.getVictim();
            if (lEnt != null && lEnt instanceof Player) ((Player)lEnt).sendMessage(message);
            break;
        case RADIUS:
            // occurence.getLocation().getRadiusPlayers()? - how do we get
            // players around radius without an entity?
            Location loc = occurence.getLocation();
            for (Player player : loc.getWorld().getPlayers()) {
                if (player.getLocation().getX() > (loc.getX() - radius)
                        || player.getLocation().getX() < (loc.getX() + radius))
                    if (player.getLocation().getY() > (loc.getY() - radius)
                            || player.getLocation().getY() < (loc.getY() + radius))
                        if (player.getLocation().getZ() > (loc.getZ() - radius)
                                || player.getLocation().getZ() < (loc.getZ() + radius))
                            player.sendMessage(message);
            }

            break;
        case SERVER:
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.sendMessage(message);
            }
            break;
        case WORLD:
            for (Player player : occurence.getLocation().getWorld()
                    .getPlayers()) {
                player.sendMessage(message);
            }
            break;
        }
        return false;
    }

    // @Override
    @Override
    public List<Action> parse(ConfigurationSection parseMe) {
        List<Action> actions = new ArrayList<Action>();

        for (String key : matches.keySet()) {
            if (parseMe.get(key) != null)
                actions.add(new MessageAction(parseMe.get(key), matches
                        .get(key)));
        }
        // messages = OtherDropsConfig.getMaybeList(new
        // ConfigurationNode((Map<?, ?>)parseMe), "message", "messages");
        return actions;
    }

    static public String getRandomMessage(Occurrence occurence, List<String> messages) {
        if (messages == null || messages.isEmpty())
            return "";
        String msg = messages.get(OtherBounds.rng.nextInt(messages.size()));
        msg = parseVariables(msg, occurence);
        return (msg == null) ? "" : msg;
    }

    static public String parseVariables(String msg, String playerName, Location loc) {
        if (msg == null)
            return null;

        msg = msg.replace("%Q", "%q");

        msg = msg.replace("%p", playerName);
        msg = msg.replace("%P", playerName.toUpperCase());

        msg = msg.replace("%l", loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ());

        msg = ChatColor.translateAlternateColorCodes('&', msg);
        // Colors: &([0-9a-fA-F])
        // Magic (random characters): &k
        // Bold: &l
        // Strikethrough: &m
        // Underline: &n
        // Italic: &o
        // Reset: &r

        msg = msg.replace("&&", "&"); // replace "escaped" ampersand

        return msg;
    }

    static public String parseVariables(String msg, Occurrence occurence) {
        if (msg == null)
            return msg;

        String playerName = "";
        if (occurence.getAttacker() instanceof Player)
            playerName = ((Player)occurence.getAttacker()).getName();

        msg = parseVariables(msg, playerName, occurence.getLocation());

        return msg;
    }
}
