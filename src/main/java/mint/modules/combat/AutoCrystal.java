package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.BlockUtil;
import mint.utils.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AutoCrystal extends Module {

    public Setting<Float> targetRange = register(new Setting("Target Range", 10f, 0f, 15f));

    public Setting<Float> placeRange = register(new Setting("Place Range", 5f, 0f, 6f));
    public Setting<Float> breakRange = register(new Setting("Break Range", 5f, 0f, 6f));

    public Setting<Float> minDamage = register(new Setting("Min Damage", 6f, 0f, 12f));
    public Setting<Float> maxSelfDamage = register(new Setting("Max Self Damage", 8f, 0f, 12f));
    public Setting<Float> minHealth = register(new Setting("Min Health", 10f, 0f, 36f));


    public AutoCrystal(){
        super("AutoCrystal", Category.COMBAT, "");
    }

    public void onUpdate(){
        doPlace(EntityUtil.getTarget(targetRange.getValue()), minDamage.getValue(), maxSelfDamage.getValue(), placeRange.getValue(), minHealth.getValue());
    }
    public void doPlace(EntityPlayer target, float minDamage, float maxSelfDamage, float placeRange, float minHealth) {
        BlockPos placePos = null;
        int crystals = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.END_CRYSTAL)).mapToInt(ItemStack::getCount).sum();
        if (target == null) {
            return;
        }
        final List<BlockPos> sphere = BlockUtil.getSphere(placeRange, true);
        for (int size = sphere.size(), i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);
            float targetDamage = EntityUtil.calculatePos(pos, target);
            float selfDamage = EntityUtil.calculatePos(pos, mc.player);
            if (BlockUtil.canPlaceCrystal(pos, true)) {
                if(selfDamage > maxSelfDamage){
                    return;
                }
                if(targetDamage < minDamage){
                    return;
                }
                if(selfDamage > minHealth){
                    return;
                }
                    placePos = pos;
            }
        }
        if(placePos != null){
            if(crystals == -1){
                return;
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        }
    }
}
