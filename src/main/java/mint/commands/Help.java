package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;

public class Help
        extends Command {
    public Help() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        Help.sendMessage("Commands: ");
        for (Command command : Mint.commandManager.getCommands()) {
            Help.sendMessage(ChatFormatting.GRAY + Mint.commandManager.getPrefix() + command.getName());
        }
    }
}

