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

    public Setting<Float> height = register(new Setting("Height", 1.4f, 1.1f, 2.0f));
    public BlockPos startPos = null;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            disable();
            return;
        }
        if (mc.player.isElytraFlying()) {
            return;
        }
        startPos = new BlockPos(mc.player.getPositionVector());
    }

    public void onUpdate() {
        if (fullNullCheck()) {
            disable();
            return;
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        EntityUtil.packetJump(true);
        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, (mc.player.posY + height.getValue()) * -1, mc.player.posZ, true));

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        disable();
    }
}