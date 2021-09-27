package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;

public class Help extends Command {

    public Help() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        Mint.messageManager.sendMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + "Commands: ");
        for (Command command : Mint.commandManager.getCommands()) {
            Mint.messageManager.sendMessage(ChatFormatting.WHITE + " \u2022 " + command.getName());
        }
    }
}

