package mint.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.Module;
import org.lwjgl.input.Keyboard;

public class Bind
        extends Command {
    public Bind() {
        super("bind", new String[]{"<module>", "<bind>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Mint.messageManager.sendMessage("Please specify a module.");
            return;
        }
        String rkey = commands[1];
        String moduleName = commands[0];
        Module module = Mint.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            Mint.messageManager.sendMessage("Unknown module '" + module + "'!");
            return;
        }
        if (rkey == null) {
            Mint.messageManager.sendMessage(module.getName() + " is bound to " + ChatFormatting.GRAY + module.getBind().toString());
            return;
        }
        int key = Keyboard.getKeyIndex(rkey.toUpperCase());
        if (rkey.equalsIgnoreCase("none")) {
            key = -1;
        }
        if (key == 0) {
            Mint.messageManager.sendMessage("Unknown key '" + rkey + "'!");
            return;
        }
        module.bind.setValue(new mint.clickgui.setting.Bind(key));
        Mint.messageManager.sendMessage("Bind for " + ChatFormatting.GREEN + module.getName() + ChatFormatting.WHITE + " set to " + ChatFormatting.GRAY + rkey.toUpperCase());
    }
}

