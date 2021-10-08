package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.EntityUtil;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class Clip extends Module {

    public Clip() {
        super("Clip", Module.Category.MOVEMENT, "VClip bypass for crystalpvp cc.");
    }

    public Setting<Float> offset = register(new Setting("Y Offset", 1.4f, 1.1f, 2.0f));
    public Setting<Boolean> offground = register(new Setting("Offground", false));

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            disable();
            return;
        }
        if (mc.player.posY < 6 && !mc.player.isElytraFlying() && mc.player.onGround) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            EntityUtil.packetJump(offground.getValue());
            double y = (mc.player.posY + offset.getValue()) * -1;
            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, y, mc.player.posZ, offground.getValue()));
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            disable();
        }
    }
}