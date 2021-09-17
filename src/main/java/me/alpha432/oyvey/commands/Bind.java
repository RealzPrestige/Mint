package me.alpha432.oyvey.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.Mint;
import me.alpha432.oyvey.modules.Module;
import org.lwjgl.input.Keyboard;

public class Bind
        extends Command {
    public Bind() {
        super("bind", new String[]{"<module>", "<bind>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Bind.sendMessage("Please specify a module.");
            return;
        }
        String rkey = commands[1];
        String moduleName = commands[0];
        Module module = Mint.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            Bind.sendMessage("Unknown module '" + module + "'!");
            return;
        }
        if (rkey == null) {
            Bind.sendMessage(module.getName() + " is bound to " + ChatFormatting.GRAY + module.getBind().toString());
            return;
        }
        int key = Keyboard.getKeyIndex(rkey.toUpperCase());
        if (rkey.equalsIgnoreCase("none")) {
            key = -1;
        }
        if (key == 0) {
            Bind.sendMessage("Unknown key '" + rkey + "'!");
            return;
        }
        module.bind.setValue(new me.alpha432.oyvey.clickgui.setting.Bind(key));
        Bind.sendMessage("Bind for " + ChatFormatting.GREEN + module.getName() + ChatFormatting.WHITE + " set to " + ChatFormatting.GRAY + rkey.toUpperCase());
    }
}

