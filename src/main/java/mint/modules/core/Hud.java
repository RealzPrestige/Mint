package mint.modules.core;

import mint.Mint;
import mint.events.RenderOverlayEvent;
import mint.modules.Module;
import mint.setting.Setting;
import mint.settingsrewrite.impl.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Hud extends Module {

    static Hud INSTANCE = new Hud();
    public Setting color = register(new Setting<>("Color", new Color(199, 45, 45)));
    public BooleanSetting test = new BooleanSetting("Test", false, this);
    public IntegerSetting intTest = new IntegerSetting("Int Test", 10, -20, 20, this);
    public FloatSetting floatTest = new FloatSetting("Float Test", 10.0f, 0.0f, 20.0f, this);
    public DoubleSetting doubleTest = new DoubleSetting("Double Test", 10.0, 0.0, 20.0, this);
    public KeySetting keyTest = new KeySetting("Key Test", Keyboard.KEY_NONE, this);
    public EnumSetting enumTest = new EnumSetting("Enum Test", EnumTest.One, this);
    public enum EnumTest{One, Two, Three, Four, Five, Six, Seven}
    public Hud() {
        super("Hud", Category.CORE, "Draws hud stuff");
        setInstance();
    }

    public static Hud getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Hud();
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void renderOverlayEvent(RenderOverlayEvent event) {
        assert Mint.hudComponentManager != null;
        Mint.hudComponentManager.drawText();
    }
}
