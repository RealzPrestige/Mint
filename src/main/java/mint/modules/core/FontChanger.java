package mint.modules.core;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.events.ClientEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.setting.Setting;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@ModuleInfo(name = "Font", category = Module.Category.Core, description = "Changes the font.")
public class FontChanger extends Module {
    private static FontChanger INSTANCE = new FontChanger();
    public BooleanSetting override = new BooleanSetting("Full", false, this);
    public IntegerSetting fontSize = new IntegerSetting("Size", 17, 15, 20, this);
    public EnumSetting style = new EnumSetting("Style", Style.ITALICBOLD, this);

    public enum Style {NORMAL, ITALIC, BOLD, ITALICBOLD}

    private boolean reloadFont = false;

    public FontChanger() {
        setInstance();
    }

    public static FontChanger getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FontChanger();
        return INSTANCE;
    }

    public static boolean checkFont(String font, boolean message) {
        for (String s : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if (!message && s.equals(font))
                return true;
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

