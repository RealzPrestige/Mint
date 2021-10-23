package mint.modules.core;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.events.RenderOverlayEvent;
import mint.modules.Module;

import java.awt.*;

public class Hud extends Module {

    static Hud INSTANCE = new Hud();

    public Setting color = register(new Setting<>("Color", new Color(199, 45, 45)));

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
