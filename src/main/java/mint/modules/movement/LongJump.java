package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LongJump extends Module {

    public LongJump() {
        super("LongJump", Module.Category.MOVEMENT, "Jump farther.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Factor));

    public enum Mode {Factor}

    public Setting<Float> acceleration = register(new Setting("Acceleration", 0.3f, 0.0f, 1.0f, v -> mode.getValue() == Mode.Factor));
    public Setting<Boolean> autoJump = register(new Setting("Auto Jump", true));
    public Setting<Integer> jumpHeight = register(new Setting("Jump Height", 390, 350, 415));
    public Setting<Boolean> disableOnLag = register(new Setting("Disable On Lag", true));
    double playerSpeed;
    float jumpFactor;

    @Override
    public void onEnable() {
        playerSpeed = EntityUtil.getDefaultSpeed();
        jumpFactor = mc.player.jumpMovementFactor;
        if (mc.player.onGround && autoJump.getValue() && mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f) {
            mc.player.motionY = jumpHeight.getValue() / 1000;
        }
    }

    @Override
    public void onDisable() {
        mc.player.jumpMovementFactor = jumpFactor;
    }

    @Override
    public void onUpdate() {
        if (mode.getValue() == Mode.Factor) {
            if (mc.player.onGround) {
                mc.player.jumpMovementFactor = jumpFactor;
            } else if (!(mc.player.jumpMovementFactor > 0.1f)) {
                mc.player.jumpMovementFactor += acceleration.getValue();
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook && disableOnLag.getValue()) {
            if (isEnabled())
                disable();
        }
    }
}