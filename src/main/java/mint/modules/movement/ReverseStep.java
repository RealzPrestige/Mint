package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;

public class ReverseStep extends Module {

    public ReverseStep() {
        super("Reverse Step", Module.Category.MOVEMENT, "Fall down faster.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Vanilla));

    public enum Mode {Vanilla, Strict}

    public Setting<Float> vanillaSpeed = register(new Setting("VanillaSpeed", 9.0f, 0.1f, 9.0f, v -> mode.getValue() == Mode.Vanilla));

    @Override
    public void onUpdate() {
        if (mc.player.isInLava() || mc.player.isInWater() || !mc.player.onGround || mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }

        if (mode.getValue() == Mode.Vanilla) {
            mc.player.motionY -= vanillaSpeed.getValue();
        }
        //todo add a switch(mode), for(Y)
        if (mode.getValue() == Mode.Strict) {
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.motionY *= 1.75f;
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }
}