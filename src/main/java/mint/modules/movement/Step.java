package mint.modules.movement;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.Module;

public class Step extends Module {

    public Step() {
        super("Step", Module.Category.MOVEMENT, "Allows you to step up blocks.");
    }

    public Setting<Integer> height = register(new Setting("Height", 2, 0, 2));

    @Override
    public void onEnable() {
        Mint.INSTANCE.mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onDisable() {
        Mint.INSTANCE.mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        Mint.INSTANCE.mc.player.stepHeight = height.getValue();
    }
}