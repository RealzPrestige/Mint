package mint.modules.core;

import mint.Mint;
import mint.setting.Setting;
import mint.modules.Module;
import mint.newgui.NewGui;

public class NewGuiModule extends Module {
    static NewGuiModule INSTANCE = new NewGuiModule();
    public Setting<Integer> topRed = register(new Setting("Top Red", 255, 0, 255));
    public Setting<Integer> topGreen = register(new Setting("Top Green", 255, 0, 255));
    public Setting<Integer> topBlue = register(new Setting("Top Blue", 255, 0, 255));
    public Setting<Integer> topAlpha = register(new Setting("Top Alpha", 100, 0, 255));

    public Setting<Integer> moduleRed = register(new Setting("Module Red", 0, 0, 255));
    public Setting<Integer> moduleGreen = register(new Setting("Module Green", 0, 0, 255));
    public Setting<Integer> moduleBlue = register(new Setting("Module Blue", 0, 0, 255));
    public Setting<Integer> moduleAlpha = register(new Setting("Module Alpha", 100, 0, 255));

    public Setting<Integer> enabledRed = register(new Setting("Enabled Red", 255, 0, 255));
    public Setting<Integer> enabledGreen = register(new Setting("Enabled Green", 255, 0, 255));
    public Setting<Integer> enabledBlue = register(new Setting("Enabled Blue", 255, 0, 255));
    public Setting<Integer> enabledAlpha = register(new Setting("Enabled Alpha", 100, 0, 255));

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
    public void onDisable() {
        assert Mint.configManager != null;
        Mint.configManager.saveConfig("Default");
    }


    public void onUpdate() {
        if (!(mc.currentScreen instanceof NewGui))
            disable();

    }
}
