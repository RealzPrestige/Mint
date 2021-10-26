package mint.modules.core;

import mint.Mint;
import mint.setting.Setting;
import mint.modules.Module;
import mint.newgui.NewGui;

import java.awt.*;

public class NewGuiModule extends Module {
    static NewGuiModule INSTANCE = new NewGuiModule();
    public Setting color = register(new Setting<>("Color", new Color(0, 255, 255, 255)));
    public Setting backgroundColor = register(new Setting<>("Background Color", new Color(0, 0, 0, 50)));


    public NewGuiModule() {
        super("New Gui", Category.CORE, "");
        this.setInstance();
    }

    public static NewGuiModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewGuiModule();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onEnable() {
        mc.displayGuiScreen(NewGui.getInstance());
    }
    @Override
    public void onDisable() {
        assert Mint.configManager != null;
        Mint.configManager.saveConfig("Default");
    }


    public void onUpdate() {
        if (!(mc.currentScreen instanceof NewGui))
            disable();
    }
}
