package mint.managers;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class MessageManager {
    public final String messagePrefix = ChatFormatting.AQUA + "<mint> " + ChatFormatting.RESET;
    public final String errorPrefix = ChatFormatting.DARK_RED + "<mint> " + ChatFormatting.RESET;

    public void sendRawMessage(String message) {
        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
        }
    }

    public void sendChatMessage(String message) {
        Minecraft.getMinecraft().player.sendChatMessage(message);
    }

    public void sendMessage(String message) {
        sendRawMessage(messagePrefix + message);
    }

    public void sendError(String message) {
        sendRawMessage(errorPrefix + message);
    }

    public void sendRemovableMessage(String message, int id) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(messagePrefix + message), id);
    }
}
