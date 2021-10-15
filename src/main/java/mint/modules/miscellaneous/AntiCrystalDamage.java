package mint.modules.miscellaneous;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.clickgui.setting.Setting;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class AntiCrystalDamage extends Module {
    public Setting<Integer> placeDelay = register(new Setting<>("Place Delay", 100, 0, 500));
    public Setting<Boolean> calculate = register(new Setting<>("Calculate", false));
    public Setting<Float> minimumSelfDamage = register(new Setting<>("Minimum Self Damage", 6.0f, 0.0f, 12.0f));
    public Setting<Float> placeRange = register(new Setting<>("Place Range", 5.0f, 0.0f, 6.0f));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", false));
    public Setting<Boolean> packet = register(new Setting<>("PacketPlace", false));
    public Setting<Boolean> swing = register(new Setting<>("Swing", false));
    public Setting<Hand> enumHand = register(new Setting<>("Hand", Hand.Mainhand, v -> swing.getValue()));

    public enum Hand {Mainhand, Offhand}

    Timer timer = new Timer();
    public AntiCrystalDamage() {
        super("Anti Crystal Damage", Category.MISCELLANEOUS, "Places strings inside crystals to avoid damage.");
    }

    public void onUpdate() {
        int stringSlot = InventoryUtil.getItemFromHotbar(Items.STRING);
        int currentItem = mc.player.inventory.currentItem;
        if(timer.passedMs(placeDelay.getValue())) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {

                    if (mc.player.getDistanceSq(entity) > MathUtil.square(placeRange.getValue()))
                        continue;

                    if (calculate.getValue() && EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, mc.player) < minimumSelfDamage.getValue())
                        continue;

                    if (stringSlot != -1)
                        InventoryUtil.switchToSlot(stringSlot);
                    else {
                        MessageManager.sendMessage(ChatFormatting.BOLD + " Anti Crystal Damage: " + ChatFormatting.RESET + "No " + ChatFormatting.RED + "Strings" + ChatFormatting.RESET + " found, toggling!");
                        disable();
                    }
                    if (mc.world.getBlockState(new BlockPos(entity.posX, entity.posY, entity.posZ)).getBlock().equals(Blocks.AIR))
                        BlockUtil.placeBlock(new BlockPos(entity.posX, entity.posY, entity.posZ), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), enumHand.getValue().equals(Hand.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);

                    mc.player.inventory.currentItem = currentItem;
                    mc.playerController.updateController();
                    timer.reset();
                }
            }
        }
    }
}