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
    public Setting<Boolean> override = register(new Setting<>("Full", false));
    public Setting<Integer> fontSize = register(new Setting<>("Size", 17, 15, 20));
    public Setting<Integer> w = register(new Setting<>("W", 72, 0, 200));
    public Setting<Integer> h = register(new Setting<>("H", 72, 0, 200));
    public Setting<Integer> x = register(new Setting<>("X", 72, 0, 200));
    public Setting<Integer> y = register(new Setting<>("Y", 72, 0, 200));
    public Setting<Float> x1 = register(new Setting<>("X1", 72.0f, 0.0f, 200.0f));
    public Setting<Float> y1 = register(new Setting<>("Y1", 72.0f, 0.0f, 200.0f));
    public Setting color = register(new Setting("Color", new Color(199, 45, 45)));
    public Setting<Style> style = register(new Setting<>("Style", Style.ITALICBOLD));
    public enum Style{NORMAL, ITALIC, BOLD, ITALICBOLD}
    private boolean reloadFont = false;

    public FontChanger() {
        super("Font", Category.CORE, "Changes the font.");
        setInstance();
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
            reloadFont = true;
        }
    }

    @Override
    public void onTick() {
        if (reloadFont) {
            Mint.textManager.init();
            reloadFont = false;
        }
    }
}

