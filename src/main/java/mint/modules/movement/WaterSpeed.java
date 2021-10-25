package mint.modules.movement;

import mint.setting.Setting;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.NullUtil;
import mint.utils.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;

import java.util.Objects;

public class WaterSpeed extends Module {

    public Setting<Boolean> packetSneak = register(new Setting<>("Packet Sneak", false));
    public Setting<Double> upFactor = register(new Setting<>("Up Factor", 1.0, 0.0, 20.0));
    public Setting<Double> downFactor = register(new Setting<>("Down Factor", 1.0, 0.0, 20.0));
    public Setting<Double> horizontalFactor = register(new Setting<>("Horizontal Factor", 1.0, 0.0, 20.0));
    public Setting<Boolean> consistent = register(new Setting<>("Consistent", false));
    public Setting<OnGround> onGround = register(new Setting<>("On Ground", OnGround.Cancel));
    public Setting<Boolean> useTimer = register(new Setting<>("Use Timer", false));
    public Setting<Float> timerAmount = register(new Setting<>("Timer Amount", 1.1f, 1.0f, 2.0f, v -> useTimer.getValue()));

    public enum OnGround {Cancel, Offground}

    boolean isPacketSneaking;

    public WaterSpeed() {
        super("Water Speed", Category.MOVEMENT, "Makes swim fast vroom vroom in le water");
    }

    public void onToggle() {
        mc.timer.tickLength = 50.0f / 1.0f;
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (!(mc.player.isInWater() || mc.player.isInLava()))
            return;

        if (mc.world.getBlockState(PlayerUtil.getPlayerPos(mc.player)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(PlayerUtil.getPlayerPos(mc.player).up()).getBlock().equals(Blocks.AIR))
            return;

        if (onGround.getValue().equals(OnGround.Cancel) && mc.player.onGround)
            return;

        if (onGround.getValue().equals(OnGround.Offground) && mc.player.onGround)
            mc.player.onGround = false;

        if (useTimer.getValue() && EntityUtil.isMoving())
            mc.timer.tickLength = 50.0F / timerAmount.getValue();
        else mc.timer.tickLength = 50.0f / 1.0f;

        if (packetSneak.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown() && !isPacketSneaking) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isPacketSneaking = true;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.player.motionY = upFactor.getValue() / 40.0;
        else if (consistent.getValue())
            mc.player.motionY = 0.0;

        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.player.motionY = -downFactor.getValue() / 40.0;
        else if (consistent.getValue())
            mc.player.motionY = 0.0f;

        if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
            mc.player.motionX *= horizontalFactor.getValue() / 10;
            mc.player.motionZ *= horizontalFactor.getValue() / 10;
        }

        if (isPacketSneaking) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isPacketSneaking = false;
        }

    }
}
