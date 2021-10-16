package mint.modules.core;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.hudeditor.HudScreen;
import mint.modules.Module;

public class PacketManager extends Module {
    static PacketManager INSTANCE = new PacketManager();

    public Setting<Boolean> topParent = register(new Setting<>("Top Color", true, false));
    public Setting<Integer> topRed = register(new Setting<>("Top Red", 255, 0, 255, v -> topParent.getValue()));
    public Setting<Integer> topGreen = register(new Setting<>("Top Green", 255, 0, 255, v -> topParent.getValue()));
    public Setting<Integer> topBlue = register(new Setting<>("Top Blue", 255, 0, 255, v -> topParent.getValue()));
    public Setting<Integer> topAlpha = register(new Setting<>("Top Alpha", 100, 0, 255, v -> topParent.getValue()));
    public Setting<Boolean> componentColor = register(new Setting<>("Component Color", true, false));
    public Setting<Integer> componentRed = register(new Setting<>("Component Red", 255, 0, 255, v -> componentColor.getValue()));
    public Setting<Integer> componentGreen = register(new Setting<>("Component Green", 255, 0, 255, v -> componentColor.getValue()));
    public Setting<Integer> componentBlue = register(new Setting<>("Component Blue", 255, 0, 255, v -> componentColor.getValue()));
    public Setting<Integer> componentAlpha = register(new Setting<>("Component Alpha", 100, 0, 255, v -> componentColor.getValue()));

    public PacketManager() {
        super("Packet Manager", Category.CORE, "Displays hud editor.");
        setInstance();
    }

    public static PacketManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PacketManager();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onEnable() {
        HudScreen.getInstance().currentScreen = HudScreen.CurrentScreen.None;
        Mint.INSTANCE.mc.displayGuiScreen(HudScreen.getInstance());
    }
}
