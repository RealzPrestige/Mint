package mint.modules.movement;

import com.google.common.eventbus.Subscribe;
import mint.modules.Module;
import mint.utils.EntityUtil;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

public class Clip extends Module {

    public Clip() {
        super("Clip", Module.Category.MOVEMENT, "VClip bypass for crystalpvp cc.");
    }

    public BlockPos startPos = null;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            disable();
            return;
        }
        //todo return if freecam is enabled, a player is using elyta
        startPos = new BlockPos(mc.player.getPositionVector());
    }

    @Override
    @Subscribe
    public void onUpdate() {
        if (fullNullCheck()) {
            disable();
            return;
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        EntityUtil.packetJump(true);

        //todo oml what is dis,
        switch (mc.player.getPosition().getY()) {

            case 6:
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY -7.4, mc.player.posZ, true));
                break;

            case 5:
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY -6.4, mc.player.posZ, true));
                break;

            case 4:
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY -5.4, mc.player.posZ, true));
                break;

            case 3:
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY -4.4, mc.player.posZ, true));
                break;

            case 2:
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY -3.4, mc.player.posZ, true));
                break;

            case 1:
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY -2.4, mc.player.posZ, true));
                break;

            case 0:
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY -1.4, mc.player.posZ, true));
                break;

        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        disable();
    }
}
