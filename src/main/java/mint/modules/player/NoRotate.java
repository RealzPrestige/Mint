package mint.modules.player;


import com.google.common.eventbus.Subscribe;
import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.BlockUtil;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

/**
 * @author kambing
 * @since 24/9/21 11:42PM
 */
public class NoRotate extends Module {


    public Setting<Boolean> packet = register(new Setting("PacketJump", true));
    public NoRotate() {
    super("ZPRESTIGEAURA",Category.MOVEMENT,"zprsdsihjfipawe");}

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
        startPos = new BlockPos(mc.player.getPosition());
    }

    @Override
    @Subscribe
    public void onUpdate() {
        if (fullNullCheck()) {
            disable();
            return;
        }
        int oldSlot = mc.player.inventory.currentItem;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.findHotbarBlock(BlockObsidian.class)));
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        if (packet.getValue()) {
            EntityUtil.packetJump(true);
            BlockUtil.placeBlock(startPos);
            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.4, mc.player.posZ, true));
        }else{
            mc.player.jump();
            BlockUtil.placeBlock(startPos);
            if (startPos.getY() != mc.player.getPosition().getY()) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.4, mc.player.posZ, true));
            }
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        disable();
    }
}
