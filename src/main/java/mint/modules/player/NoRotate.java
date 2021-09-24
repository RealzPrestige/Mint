package mint.modules.player;


import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.modules.combat.HoleFiller;
import mint.utils.BlockUtil;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import mint.utils.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * @author kambing
 * @since 24/9/21 11:42PM
 */
public class NoRotate extends Module {

    public Setting<Boolean> packetJump = register(new Setting("Packet Jump", true));
    public Setting<Boolean> packetPlace = register(new Setting("Packet Place", true));
    public Setting<Boolean> rotate = register(new Setting("Place Rotate", true));
    BlockPos startpos;

    public NoRotate() {
        super("ZPRESTIGEAURA", Category.MOVEMENT, "zprsdsihjfipawe");
    }

    public void onEnable() {
        startpos = PlayerUtil.getPlayerPos(mc.player);
        if(packetJump.getValue()){
            EntityUtil.packetJump(false);
        } else {
            mc.player.jump();
        }
    }

    public void onUpdate() {
        int blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        int oldSlot = mc.player.inventory.currentItem;
        if(!mc.player.onGround) {
            InventoryUtil.SilentSwitchToSlot(blockSlot);
            BlockUtil.placeBlock(startpos, EnumHand.MAIN_HAND, rotate.getValue(), packetPlace.getValue(), false, false, EnumHand.MAIN_HAND);
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        }
        if(mc.world.getBlockState(startpos).getBlock() == Blocks.OBSIDIAN){
            disable();
        }
    }
}
