package me.alpha432.oyvey.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.modules.Feature;

import java.util.ArrayList;
import java.util.LinkedList;

public class CommandManager
        extends Feature {
    private final ArrayList<Command> commands = new ArrayList();
    private String clientMessage = "<OyVey>";
    private String prefix = ".";

    public CommandManager() {
        super("Command");
        commands.add(new Bind());
        commands.add(new Prefix());
        commands.add(new Config());
        commands.add(new Friend());
        commands.add(new Help());
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        LinkedList<String> result = new LinkedList<>();
        for (int i = 0; i < input.length; ++i) {
            if (i == indexToDelete) continue;
            result.add(input[i]);
        }
        return result.toArray(input);
    }

    private static String strip(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring("\"".length(), str.length() - "\"".length());
        }
        return str;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String name = parts[0].substring(1);
        String[] args = CommandManager.removeElement(parts, 0);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null) continue;
            args[i] = CommandManager.strip(args[i]);
        }
        for (Command c : this.commands) {
            if (!c.getName().equalsIgnoreCase(name)) continue;
            c.execute(parts);
            return;
        }
        Command.sendMessage(ChatFormatting.GRAY + "Command not found, type 'help' for the commands list.");
    }

    public ArrayList<Command> getCommands() {
        return this.commands;
    }

    public String getClientMessage() {
        return this.clientMessage;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

