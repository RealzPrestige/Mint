package mint.utils;

import mint.Mint;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import sun.audio.AudioPlayer;

public class InventoryUtil {

    private static Minecraft mc = Minecraft.getMinecraft();

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

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }
}