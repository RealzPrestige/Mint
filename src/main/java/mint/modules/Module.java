package mint.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.events.RenderOverlayEvent;
import mint.events.RenderWorldEvent;
import mint.managers.MessageManager;
import mint.modules.core.Notifications;
import mint.settingsrewrite.impl.KeySetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class Module {
    public static Minecraft mc = Minecraft.getMinecraft();

    public String name = getModuleInfo().name();
    public String description = getModuleInfo().description();
    public Category category = getModuleInfo().category();

    public KeySetting bind = new KeySetting("Keybind", Keyboard.KEY_NONE, this);

    public boolean sliding;
    public boolean isOpened = false;
    public boolean drawn = false;
    public boolean enabled = false;

    public Minecraft getMc() {
        return Minecraft.getMinecraft();
    }

    public boolean isSliding() {
        return this.sliding;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void onLoad() {
    }

    public void onTick() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    public void renderOverlayEvent(RenderOverlayEvent event) {
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
    }

    public void onUnload() {
    }

    public boolean isOn() {
        return enabled;
    }

    public void enable() {
        enabled = true;
        onToggle();
        onEnable();
        MessageManager.sendMessage(name + " enabled");
        TextComponentString text = new TextComponentString(ChatFormatting.AQUA + "" + ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + ChatFormatting.RESET + ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + " " + this.getName().replace("_", " ") + ChatFormatting.RESET + " was toggled " + ChatFormatting.GREEN + "" + ChatFormatting.BOLD + "on!");
        if (Notifications.getInstance().isEnabled() && (Notifications.getInstance().mode.getValue() == Notifications.Mode.CHAT || Notifications.getInstance().mode.getValue() == Notifications.Mode.BOTH)) {
            Mint.INSTANCE.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
        if (Notifications.getInstance().isEnabled() && Notifications.getInstance().modules.getValue() && (Notifications.getInstance().mode.getValue() == Notifications.Mode.HUD || Notifications.getInstance().mode.getValue() == Notifications.Mode.BOTH)) {
            Notifications.getInstance().notification.clear();
            Notifications.getInstance().hasReachedEndState = false;
            Notifications.getInstance().waitTime = 0;
            Notifications.getInstance().width = 0;
            Notifications.getInstance().lefinalewidth = false;
            Notifications.getInstance().notification.put(this.getName().replace("_", " ") + " was toggled " + ChatFormatting.GREEN + "" + ChatFormatting.BOLD + "on!", 1000);
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        enabled = false;
        onToggle();
        onDisable();
        MessageManager.sendMessage(name + " disabled");
        TextComponentString text = new TextComponentString(ChatFormatting.AQUA + "" + ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + ChatFormatting.RESET + ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + " " + this.getName().replace("_", " ") + ChatFormatting.RESET + " was toggled " + ChatFormatting.RED + "" + ChatFormatting.BOLD + "off!");
        if (Notifications.getInstance().isEnabled() && (Notifications.getInstance().mode.getValue() == Notifications.Mode.CHAT || Notifications.getInstance().mode.getValue() == Notifications.Mode.BOTH)) {
            Mint.INSTANCE.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
        if (Notifications.getInstance().isEnabled() && Notifications.getInstance().modules.getValue() && (Notifications.getInstance().mode.getValue() == Notifications.Mode.HUD || Notifications.getInstance().mode.getValue() == Notifications.Mode.BOTH)) {
            Notifications.getInstance().notification.clear();
            Notifications.getInstance().hasReachedEndState = false;
            Notifications.getInstance().waitTime = 0;
            Notifications.getInstance().width = 0;
            Notifications.getInstance().lefinalewidth = false;
            Notifications.getInstance().notification.put(this.getName().replace("_", " ") + " was toggled " + ChatFormatting.RED + "" + ChatFormatting.BOLD + "off!", 1000);
        }
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public boolean isDrawn() {
        return this.drawn;
    }


    public Category getCategory() {
        return this.category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBind() {
        return this.bind.getKey();
    }

    public void setBind(int key) {
        this.bind.setValue(key);
    }

    public boolean listening() {
        return true;
    }

    public String getFullArrayString() {
        return getName();
    }

    public ModuleInfo getModuleInfo() {
        return getClass().getAnnotation(ModuleInfo.class);
    }

    public enum Category {
        Combat("Combat"),
        Miscellaneous("Miscellaneous"),
        Movement("Movement"),
        Player("Player"),
        Visual("Visual"),
        Core("Core");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

