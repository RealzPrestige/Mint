package mint.modules.miscellaneous;

import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

public class SinglePieceMend extends Module {

    public Setting<Integer> thresholdValue = register(new Setting<>("Threshold Value", 90, 1, 100));
    public Setting<Boolean> throwExp = register(new Setting<>("Auto Throw Exp", false));
    public Setting<Boolean> throwDown = register(new Setting("Throw Down", false, v -> throwExp.getValue()));
    public Setting<Boolean> bindOnly = register(new Setting("Bind Only", false, v -> throwExp.getValue()));
    public Setting<Bind> expBind = register(new Setting<>("Exp Bind", new Bind(-1), v -> throwExp.getValue() && bindOnly.getValue()));

    public SinglePieceMend() {
        super("Single Piece Mend", Category.MISCELLANEOUS, "Mends armor piece by piece.");
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        switch (getCompatibleArmorPiece(thresholdValue.getValue())) {
            case -1:
                reset();
                break;
            case 5:
                if (throwExp.getValue())
                    if (bindOnly.getValue()) {
                        if (expBind.getValue().getKey() != -1)
                            if (Keyboard.isKeyDown(expBind.getValue().getKey())) {
                                throwExp();
                                takeOff(6, 7, 8);
                            }
                    } else {
                        takeOff(6, 7, 8);
                        throwExp();
                    }
                break;
            case 6:
                if (throwExp.getValue())
                    if (bindOnly.getValue()) {
                        if (expBind.getValue().getKey() != -1)
                            if (Keyboard.isKeyDown(expBind.getValue().getKey())) {
                                takeOff(5, 7, 8);
                                throwExp();
                            }
                    } else {
                        takeOff(5, 7, 8);
                        throwExp();
                    }
                break;
            case 7:
                if (throwExp.getValue())
                    if (bindOnly.getValue()) {
                        if (expBind.getValue().getKey() != -1)
                            if (Keyboard.isKeyDown(expBind.getValue().getKey())) {
                                takeOff(5, 6, 8);
                                throwExp();
                            }
                    } else {
                        takeOff(5, 6, 8);
                        throwExp();
                    }
                break;
            case 8:
                if (throwExp.getValue())
                    if (bindOnly.getValue()) {
                        if (expBind.getValue().getKey() != -1)
                            if (Keyboard.isKeyDown(expBind.getValue().getKey())) {
                                takeOff(5, 6, 7);
                                throwExp();
                            }
                    } else {
                        takeOff(5, 6, 7);
                        throwExp();
                    }
                break;
        }
    }

    public void throwExp() {
        int expItem = InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE);
        int currentItem = mc.player.inventory.currentItem;

        InventoryUtil.switchToSlot(expItem);

        if (throwDown.getValue())
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 90, mc.player.onGround));

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));

        mc.player.inventory.currentItem = currentItem;
        mc.playerController.updateController();
    }

    public int getCompatibleArmorPiece(int thresholdValue) {
        if (getHelmetPercent() != -1)
            if (getHelmetPercent() < thresholdValue)
                return 5;

        if (getChestPercent() != -1)
            if (getChestPercent() < thresholdValue)
                return 6;

        if (getLegsPercent() != -1)
            if (getLegsPercent() < thresholdValue)
                return 7;
        if (getFeetPercent() != -1)
            if (getFeetPercent() < thresholdValue)
                return 8;

        return -1;
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

    public void takeOff(int s1, int s2, int s3) {
        if (!mc.player.inventory.getStackInSlot(s1).isEmpty)
            mc.playerController.windowClick(0, s1, 0, ClickType.QUICK_MOVE, mc.player);
        if (!mc.player.inventory.getStackInSlot(s2).isEmpty)
            mc.playerController.windowClick(0, s2, 0, ClickType.QUICK_MOVE, mc.player);
        if (!mc.player.inventory.getStackInSlot(s3).isEmpty)
            mc.playerController.windowClick(0, s3, 0, ClickType.QUICK_MOVE, mc.player);
    }

    public void reset() {
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty) {
            int item = InventoryUtil.getItemSlot(Items.DIAMOND_HELMET);
            if (item != -1) {
                mc.playerController.windowClick(0, item, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 5, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, item, 0, ClickType.PICKUP, mc.player);
            }
        }
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty) {
            int item = InventoryUtil.getItemSlot(Items.DIAMOND_CHESTPLATE);
            if (item != -1) {
                mc.playerController.windowClick(0, item, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, item, 0, ClickType.PICKUP, mc.player);
            }
        }
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty) {
            int item = InventoryUtil.getItemSlot(Items.DIAMOND_LEGGINGS);
            if (item != -1) {
                mc.playerController.windowClick(0, item, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 7, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, item, 0, ClickType.PICKUP, mc.player);
            }
        }
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty) {
            int item = InventoryUtil.getItemSlot(Items.DIAMOND_BOOTS);
            if (item != -1) {
                mc.playerController.windowClick(0, item, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 8, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, item, 0, ClickType.PICKUP, mc.player);
            }
        }
    }
}
