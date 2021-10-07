package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.EntityUtil;

public class ReverseStep extends Module {

    public ReverseStep() {
        super("Reverse Step", Module.Category.MOVEMENT, "Fall down faster.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Vanilla));

    public enum Mode {Vanilla, Strict}

    public Setting<Float> vanillaSpeed = register(new Setting("VanillaSpeed", 9.0f, 0.1f, 9.0f, v -> mode.getValue() == Mode.Vanilla));
    public Setting<Float> strictSpeed = register(new Setting("StrictSpeed", 17.5f, 10.0f, 30.0f));

    //todo if someone is verie smart then rewrite this cuz i just pasted for() and collisionboxes
    @Override
    public void onUpdate() {
        if (mc.player != null && !EntityUtil.isInLiquid() && mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
            switch (mode.getValue()) {
                case Vanilla:
                    for (double y = 0.0; y < 90 + 0.5; y += 0.01) {
                        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                            mc.player.motionY = -vanillaSpeed.getValue();
                            break;
                        }
                    }
                    break;

                case Strict:
                    for (double y = 0.0; y < 90 + 0.5; y += 0.01) {
                        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                            mc.player.motionY *= strictSpeed.getValue() / 10;
                            break;
                        }
                    }
                    break;
            }
        }
    }
}