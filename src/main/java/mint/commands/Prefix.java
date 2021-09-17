package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;

public class Prefix
        extends Command {
    public Prefix() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + Mint.commandManager.getPrefix());
            return;
        }
        Mint.commandManager.setPrefix(commands[0]);
        Command.sendMessage("Prefix changed to " + ChatFormatting.GRAY + commands[0]);
    }
}

