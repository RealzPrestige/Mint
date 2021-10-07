package mint.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.events.ClientEvent;
import mint.events.Render2DEvent;
import mint.events.Render3DEvent;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.modules.core.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public class Module extends Feature {
    public static Minecraft mc = Minecraft.getMinecraft();
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled = register(new Setting<>("Enabled", false));
    public boolean drawn = false;
    public Setting<Bind> bind = register(new Setting<>("Keybind", new Bind(-1)));
    public Setting<String> displayName;
    public boolean hidden;
    public boolean sliding;

    public Module(String name, Category category, String description) {
        super(name);
        this.displayName = register(new Setting<>("DisplayName", name));
        this.description = description;
        this.category = category;
    }

    public Minecraft getMc(){
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

    public void onRender2D(Render2DEvent event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    public void onUnload() {
    }

    public String getDisplayInfo() {
        return null;
    }

    public boolean isOn() {
        return this.enabled.getValue();
    }

    public boolean isOff() {
        return !this.enabled.getValue();
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public void enable() {
        enabled.setValue(Boolean.TRUE);
        onToggle();
        onEnable();
        TextComponentString text = new TextComponentString(ChatFormatting.AQUA + "" + ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + ChatFormatting.RESET + ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + " " + this.getDisplayName().replace("_", " ") + ChatFormatting.RESET + " was toggled " + ChatFormatting.GREEN + "" + ChatFormatting.BOLD + "on!");
        if(Notifications.getInstance().isEnabled() && (Notifications.getInstance().mode.getValue() == Notifications.Mode.CHAT || Notifications.getInstance().mode.getValue() == Notifications.Mode.BOTH)) {
            Mint.INSTANCE.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
        if(Notifications.getInstance().isEnabled() && Notifications.getInstance().modules.getValue()  && (Notifications.getInstance().mode.getValue() == Notifications.Mode.HUD || Notifications.getInstance().mode.getValue() == Notifications.Mode.BOTH)) {
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
        MinecraftForge.EVENT_BUS.unregister(this);
        enabled.setValue(false);
        TextComponentString text = new TextComponentString(ChatFormatting.AQUA + "" + ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + ChatFormatting.RESET + ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + " " + this.getDisplayName().replace("_", " ") + ChatFormatting.RESET + " was toggled " + ChatFormatting.RED + "" + ChatFormatting.BOLD + "off!");
        if(Notifications.getInstance().isEnabled() && (Notifications.getInstance().mode.getValue() == Notifications.Mode.CHAT || Notifications.getInstance().mode.getValue() == Notifications.Mode.BOTH)) {
            Mint.INSTANCE.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        }
        onToggle();
        onDisable();
        if(Notifications.getInstance().isEnabled() && Notifications.getInstance().modules.getValue() && (Notifications.getInstance().mode.getValue() == Notifications.Mode.HUD || Notifications.getInstance().mode.getValue() == Notifications.Mode.BOTH)) {
            Notifications.getInstance().notification.clear();
            Notifications.getInstance().hasReachedEndState = false;
            Notifications.getInstance().waitTime = 0;
            Notifications.getInstance().width = 0;
            Notifications.getInstance().lefinalewidth = false;
            Notifications.getInstance().notification.put(this.getName().replace("_", " ") + " was toggled " + ChatFormatting.RED + "" + ChatFormatting.BOLD + "off!", 1000);
        }
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.setEnabled(!this.isEnabled());
        }
    }

    public String getDisplayName() {
        return this.displayName.getValue();
    }


    public String getDescription() {
        return this.description;
    }

    public boolean isDrawn() {
        return this.drawn;
    }

    public void setDrawn() {
        this.drawn = true;
    }
    public void setUndrawn() {
        this.drawn = false;
    }

    public Category getCategory() {
        return this.category;
    }


    public Bind getBind() {
        return this.bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }

    public boolean listening() {
        return true;
    }

    public String getFullArrayString() {
        return this.getDisplayName() + ChatFormatting.GRAY + (this.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + this.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
    }

    public enum Category {
        COMBAT("Combat"),
        MISCELLANEOUS("Miscellaneous"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        VISUAL("Visual"),
        CORE("Core");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

