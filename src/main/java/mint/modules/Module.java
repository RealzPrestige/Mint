package mint.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.events.ClientEvent;
import mint.events.Render2DEvent;
import mint.events.Render3DEvent;
import mint.commands.Command;
import mint.clickgui.setting.BindSetting;
import mint.clickgui.setting.Setting;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;

public class Module
        extends Feature {
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled = register(new Setting<>("Enabled", false));
    public boolean drawn = false;
    public Setting<BindSetting> bind = register(new Setting<>("Keybind", new BindSetting(-1)));
    public Setting<String> displayName;
    public boolean hidden;
    public boolean sliding;

    public Module(String name, Category category, String description) {
        super(name);
        this.displayName = register(new Setting<>("DisplayName", name));
        this.description = description;
        this.category = category;
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
        TextComponentString text = new TextComponentString(ChatFormatting.AQUA + "" + ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + ChatFormatting.RESET + ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + " " + this.getDisplayName() + ChatFormatting.RESET + " was toggled " + ChatFormatting.BOLD + "" + ChatFormatting.GREEN + "on!");
        Mint.INSTANCE.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        enabled.setValue(false);
        TextComponentString text = new TextComponentString(ChatFormatting.AQUA + "" + ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + ChatFormatting.RESET + ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + " " + this.getDisplayName() + ChatFormatting.RESET + " was toggled " + ChatFormatting.BOLD + "" + ChatFormatting.RED + "off!");
        //TextComponentString text = new TextComponentString(ChatFormatting.AQUA + Mint.commandManager.getClientMessage() + " " + ChatFormatting.RED + this.getDisplayName() + " disabled.");
        Mint.INSTANCE.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
        onToggle();
        onDisable();
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


    public BindSetting getBind() {
        return this.bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(new BindSetting(key));
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
        VISUAL("Visual"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
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

