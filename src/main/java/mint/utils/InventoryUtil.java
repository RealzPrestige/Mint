package mint.utils;

import mint.Mint;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil {

    public static int getItemFromHotbar(final Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Mint.INSTANCE.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                slot = i;
            }
        }
        return slot;
    }

    public static void SilentSwitchToSlot(int slot) {
        if (Mint.INSTANCE.mc.player.inventory.currentItem == slot || slot == -1) {
            return;
        }
        Mint.INSTANCE.mc.player.inventory.currentItem = slot;
        Mint.INSTANCE.mc.playerController.updateController();
    }

}