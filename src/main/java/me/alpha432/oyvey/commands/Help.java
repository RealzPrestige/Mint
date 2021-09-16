package me.alpha432.oyvey.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;

public class Help
        extends Command {
    public Help() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        Help.sendMessage("Commands: ");
        for (Command command : OyVey.commandManager.getCommands()) {
            Help.sendMessage(ChatFormatting.GRAY + OyVey.commandManager.getPrefix() + command.getName());
        }
    }
}

