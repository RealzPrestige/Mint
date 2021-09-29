package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;

public class ReverseStep extends Module {

    public ReverseStep() {
        super("Reverse Step", Module.Category.MOVEMENT, "Makes downwards motion Instant / Faster");
    }

    public Setting<Float> vanillaSpeed = register(new Setting("VanillaSpeed", 9.0f, 0.1f, 9.0f));

    @Override
    public void onUpdate() {
        if (mc.player.isInLava() || mc.player.isInWater()) {
            return;
        }
        if (mc.player.onGround) {
            mc.player.motionY -= vanillaSpeed.getValue();
        }
    }
}