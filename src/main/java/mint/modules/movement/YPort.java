package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.NullUtil;

public class YPort extends Module {

    public YPort() {
        super("YPort", Module.Category.MOVEMENT,"idk");
    }

    public Setting<Double> speed = register(new Setting("YPortSpeed", 0.1d, 0.0d, 1.0d));
    public Setting<Float> fallSpeed = register(new Setting("FallSpeed", 0.8f, 0.1f, 9.0f));
    public Setting<Integer> yMotion = register(new Setting("YMotion", 390, 350, 420));

    @Override
    public void onToggle() {
        mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            disable();
            return;
        }
        if (mc.player.isSneaking() || EntityUtil.isInLiquid() || mc.player.isOnLadder()) {
            return;
        }
        mc.player.stepHeight = 2.0f;
        if (mc.player.onGround) {
            EntityUtil.setSpeed(mc.player, EntityUtil.getDefaultMoveSpeed() + speed.getValue());
            mc.player.motionY = yMotion.getValue() / 1000.0f;
        } else {
            for (double y = 0.0; y < 2.5 + 0.5; y += 0.01) {
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                    mc.player.motionY = -fallSpeed.getValue();
                    break;
                }
            }
        }
    }
}