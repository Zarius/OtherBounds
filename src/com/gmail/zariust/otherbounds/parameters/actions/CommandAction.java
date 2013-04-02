package com.gmail.zariust.otherbounds.parameters.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherbounds.Log;
import com.gmail.zariust.otherbounds.Log.Verbosity;
import com.gmail.zariust.otherbounds.PlayerWrapper;

public class CommandAction extends Action {
    private List<String>            commands;

    @SuppressWarnings("unchecked")
    public CommandAction(Object commandToParse) {
        if (commandToParse == null)
            return; // "Registration" passed a null value

        if (commandToParse instanceof List)
            commands = (List<String>) commandToParse;
        else
            commands = Collections.singletonList(commandToParse.toString());

        // OtherDrops.logInfo("Adding messages: "+messages.toString());

    }

    @Override
    public boolean act(Occurrence occurence) {
        if (commands != null && commands.size() > 0) {
            Player who = null;
            if (occurence.getAttacker() instanceof Player) who = (Player) occurence.getAttacker();
            processCommands(commands, who, occurence);
        } else return false;

        return false;
    }

    private void processCommands(List<String> commands, Player who, Occurrence occurence) {
        if (commands != null) {
            for (String command : commands) {
                boolean suppress = false;
                Boolean override = false;
                // Five possible prefixes (slash is optional in all of them)
                // "/" - Run the command as the player, and send them any result
                // messages
                // "/!" - Run the command as the player, but send result
                // messages to the console
                // "/*" - Run the command as the player with op override, and
                // send them any result messages
                // "/!*" - Run the command as the player with op override, but
                // send result messages to the console
                // "/$" - Run the command as the console, but send the player
                // any result messages
                // "/!$" - Run the command as the console, but send result
                // messages to the console
                if (who != null)
                    command = command.replaceAll("%p", who.getName());
                if (command.startsWith("/"))
                    command = command.substring(1);
                if (command.startsWith("!")) {
                    command = command.substring(1);
                    suppress = true;
                }
                if (command.startsWith("*")) {
                    command = command.substring(1);
                    override = true;
                } else if (command.startsWith("$")) {
                    command = command.substring(1);
                    override = null;
                }

                command = command.trim();
                
                // Just a debug message:
                if (Log.getVerbosity().exceeds(Verbosity.HIGH)) {
                    String runAs = "PLAYER";
                    if (override != null && override == true)
                        runAs = "OP";
                    else if (override == null)
                        runAs = "CONSOLE";

                    String outputTo = "player";
                    if (suppress)
                        outputTo = "console";

                    Log.high("CommandAction: running - '/" + command
                            + "' as " + runAs + ", output to " + outputTo);
                }

                command = MessageAction.parseVariables(command, occurence);

                CommandSender from;
                if (who == null || override == null)
                    from = Bukkit.getConsoleSender();
                else
                    from = new PlayerWrapper(who, override, suppress);
                Bukkit.getServer().dispatchCommand(from, command);
            }
        }
    }

    // @Override
    @Override
    public List<Action> parse(ConfigurationSection parseMe) {
        List<Action> actions = new ArrayList<Action>();

        List<String> commands = getMaybeList(parseMe, "command", "commands");
        if (commands != null && commands.size() > 0) {
            actions.add(new CommandAction(commands));
        }
        return actions;
    }


    public static List<String> getMaybeList(ConfigurationSection node,
            String... keys) {
        if (node == null)
            return new ArrayList<String>();
        Object prop = null;
        String key = null;
        for (int i = 0; i < keys.length; i++) {
            key = keys[i];
            prop = node.get(key);
            if (prop != null)
                break;
        }
        List<String> list;
        if (prop == null)
            return new ArrayList<String>();
        else if (prop instanceof List)
            list = node.getStringList(key);
        else
            list = Collections.singletonList(prop.toString());
        return list;
    }

}
