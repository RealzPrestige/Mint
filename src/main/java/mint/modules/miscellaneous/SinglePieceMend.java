package mint.modules.miscellaneous;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

import java.util.ArrayList;

public class SinglePieceMend extends Module {
    public Setting<Integer> thresholdValue = register(new Setting<>("Threshold Value", 90, 1, 100));

    public SinglePieceMend() {
        super("Single Piece Mend", Category.MISCELLANEOUS, "Mends armor piece by piece.");
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;
        if (getCompatibleArmorPiece(thresholdValue.getValue()) != -1)
            switch (getCompatibleArmorPiece(thresholdValue.getValue())) {
                case 5:
                    mc.playerController.windowClick(0, 6, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.windowClick(0, 7, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.windowClick(0, 8, 0, ClickType.QUICK_MOVE, mc.player);
                    if (getHelmetPercent() >= thresholdValue.getValue()) {
                        for (int i = 45; i > 0; --i) {
                            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemArmor)
                                mc.playerController.windowClick(0, i, 0, ClickType.QUICK_MOVE, mc.player);
                        }
                    }
                    break;
                case 6:
                    mc.playerController.windowClick(0, 5, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.windowClick(0, 7, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.windowClick(0, 8, 0, ClickType.QUICK_MOVE, mc.player);
                    break;
                case 7:
                    mc.playerController.windowClick(0, 5, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.windowClick(0, 6, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.windowClick(0, 8, 0, ClickType.QUICK_MOVE, mc.player);
                    break;
                case 8:
                    mc.playerController.windowClick(0, 5, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.windowClick(0, 6, 0, ClickType.QUICK_MOVE, mc.player);
                    mc.playerController.windowClick(0, 7, 0, ClickType.QUICK_MOVE, mc.player);
                    break;
            }
    }

    public int getCompatibleArmorPiece(int thresholdValue) {
        if (getHelmetPercent() != -1 && getHelmetPercent() < thresholdValue)
            return 5;
        else if (getChestPercent() != -1 && getChestPercent() < thresholdValue)
            return 6;
        else if (getLegsPercent() != -1 && getLegsPercent() < thresholdValue)
            return 7;
        else if (getFeetPercent() != -1 && getFeetPercent() < thresholdValue)
            return 8;
        else return -1;
    }

    public int getHelmetPercent() {
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty)
            return -1;

        float i = (mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getMaxDamage() - (float) mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItemDamage()) / mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getMaxDamage();
        float o = 1.0f - i;

        return 100 - (int) (o * 100.0f);
    }

    public int getChestPercent() {
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty)
            return -1;

        float i = (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getMaxDamage() - (float) mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItemDamage()) / mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getMaxDamage();
        float o = 1.0f - i;

        return 100 - (int) (o * 100.0f);
    }

    public int getLegsPercent() {
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty)
            return -1;

        float i = (mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getMaxDamage() - (float) mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItemDamage()) / mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getMaxDamage();
        float o = 1.0f - i;

        return 100 - (int) (o * 100.0f);
    }

    public int getFeetPercent() {
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty)
            return -1;

        float i = (mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getMaxDamage() - (float) mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItemDamage()) / mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getMaxDamage();
        float o = 1.0f - i;

        return 100 - (int) (o * 100.0f);
    }

    public ArrayList<Integer> findArmorPieces() {
        ArrayList<Integer> armorPieces = new ArrayList<>();
        if (InventoryUtil.getItemSlot(Items.DIAMOND_HELMET) != -1 && InventoryUtil.getItemSlot(Items.DIAMOND_HELMET) != 5)
            armorPieces.add(InventoryUtil.getItemSlot(Items.DIAMOND_HELMET));

        if (InventoryUtil.getItemSlot(Items.DIAMOND_CHESTPLATE) != -1 && InventoryUtil.getItemSlot(Items.DIAMOND_HELMET) != 6)
            armorPieces.add(InventoryUtil.getItemSlot(Items.DIAMOND_CHESTPLATE));

        if (InventoryUtil.getItemSlot(Items.DIAMOND_LEGGINGS) != -1 && InventoryUtil.getItemSlot(Items.DIAMOND_HELMET) != 7)
            armorPieces.add(InventoryUtil.getItemSlot(Items.DIAMOND_LEGGINGS));

        if (InventoryUtil.getItemSlot(Items.DIAMOND_BOOTS) != -1 && InventoryUtil.getItemSlot(Items.DIAMOND_HELMET) != 8)
            armorPieces.add(InventoryUtil.getItemSlot(Items.DIAMOND_BOOTS));

        return armorPieces;
    }

}
