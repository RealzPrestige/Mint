package mint.modules.core;

import mint.Mint;
import mint.events.RenderOverlayEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.ColorSetting;

import java.awt.*;

@ModuleInfo(name = "Hud", category = Module.Category.Core, description = "Draws hud stuff")
public class Hud extends Module {

    static Hud INSTANCE = new Hud();
    public ColorSetting color = new ColorSetting("Color", new Color(-1), this);

    public Hud() {
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
