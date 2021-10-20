package mint.modules.core;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.events.ClientEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class FontChanger
        extends Module {
    private static FontChanger INSTANCE = new FontChanger();
    public Setting<Boolean> override = this.register(new Setting<>("Full", false));
    public Setting<Integer> fontSize = this.register(new Setting<>("Size", 17, 15, 20));
    public Setting<Integer> w = this.register(new Setting<>("W", 72, 0, 200));
    public Setting<Integer> h = this.register(new Setting<>("H", 72, 0, 200));
    public Setting<Integer> x = this.register(new Setting<>("X", 72, 0, 200));
    public Setting<Integer> y = this.register(new Setting<>("Y", 72, 0, 200));
    public Setting<Style> style = register(new Setting<>("Style", Style.ITALICBOLD));
    public enum Style{NORMAL, ITALIC, BOLD, ITALICBOLD}
    private boolean reloadFont = false;

    public FontChanger() {
        super("Font", Category.CORE, "Changes the font.");
        this.setInstance();
    }

    public static FontChanger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FontChanger();
        }
        return INSTANCE;
    }

    public static boolean checkFont(String font, boolean message) {
        for (String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (!message && s.equals(font)) {
                return true;
            }
            if (!message) continue;
            MessageManager.sendMessage(s);
        }
        return false;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        Setting setting;
        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this)) {
            if (setting.getName().equals("FontName") && !FontChanger.checkFont(setting.getPlannedValue().toString(), false)) {
                MessageManager.sendMessage(ChatFormatting.WHITE + "That font doesnt exist.");
                event.setCanceled(true);
                return;
            }
            this.reloadFont = true;
        }
    }

    @Override
    public void onTick() {
        if (this.reloadFont) {
            Mint.textManager.init();
            this.reloadFont = false;
        }
    }
}

