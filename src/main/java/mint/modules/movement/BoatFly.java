package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.MathUtil;
import net.minecraft.entity.item.EntityBoat;

public class BoatFly extends Module {

    public BoatFly() {
        super("BoatFly", Module.Category.MOVEMENT, "Fly using boats.");
    }

    public Setting<Boolean> spoofParent = register(new Setting("Spoof", true, false));
    public Setting<Boolean> noClip = register(new Setting("NoClip", true, v -> spoofParent.getValue()));
    public Setting<Boolean> onGround = register(new Setting("OnGround", false, v -> spoofParent.getValue()));
    public Setting<Boolean> cancelGravity = register(new Setting("CancelGravity", true, v -> spoofParent.getValue()));

    public Setting<Boolean> flightParent = register(new Setting("Flight", true, false));
    public Setting<Boolean> stopUnloaded = register(new Setting("Pause In Unloaded", true));
    public Setting<Float> hSpeed = register(new Setting("Horizontal speed", 2.0f, 0.1f, 2.5f, v -> flightParent.getValue()));
    public Setting<Float> vSpeed = register(new Setting("Vertical speed", 2.0f, 0.1f, 2.5f, v -> flightParent.getValue()));

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }

        if (!(mc.player.ridingEntity instanceof EntityBoat)) {
            return;
        }

        if (EntityUtil.isBorderingChunk(mc.player.ridingEntity, mc.player.ridingEntity.motionX, mc.player.ridingEntity.motionZ) && stopUnloaded.getValue()) {
            return;
        }

        mc.player.ridingEntity.noClip = noClip.getValue();
        mc.player.ridingEntity.setNoGravity(cancelGravity.getValue());
        mc.player.ridingEntity.onGround = onGround.getValue();

        final double[] normalDir = MathUtil.directionSpeed(hSpeed.getValue() / 0.4f);
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.ridingEntity.motionX = normalDir[0];
            mc.player.ridingEntity.motionZ = normalDir[1];
        } else {
            EntityUtil.setMotion(0.0);
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.ridingEntity.motionY = vSpeed.getValue() / 2;
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.ridingEntity.motionY = -vSpeed.getValue() / 2;
        }
    }
}