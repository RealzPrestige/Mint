package mint.modules.player;

import mint.setting.Setting;
import mint.modules.Module;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class PacketEXP extends Module {

    public Setting<Integer> packets = register(new Setting("Packets", 1, 0, 10));
    public Setting<Boolean> rightClickOnly = register(new Setting("Right Click Only", false));

    public PacketEXP() {
        super("Packet EXP", Category.Player, "Uses packets to throw Exp.");
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (rightClickOnly.getValue() && !mc.gameSettings.keyBindUseItem.isKeyDown())
            return;

        if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both)) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(HotbarEXP()));
            for (int i = 0; i < packets.getValue(); i++)
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));

            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }

    int HotbarEXP() {
        int slot = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}
