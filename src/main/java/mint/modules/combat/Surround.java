package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.modules.movement.Step;
import mint.utils.*;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class Surround extends Module {
    public int maxBlocks;
    public int itemSlot;
    public Setting<Boolean> modesParent = register(new Setting<>("Modes", true, false));
    public Setting<PlaceMode> placeMode = register(new Setting<>("Place Mode", PlaceMode.Vanilla, v -> modesParent.getValue()));
    public Setting<SwingMode> swingMode = register(new Setting<>("Swing Mode", SwingMode.Mainhand, v -> modesParent.getValue()));
    public Setting<DisableMode> disableMode = register(new Setting<>("Disable Mode", DisableMode.Smart, v -> modesParent.getValue()));
    public Setting<BlockSelection> blocks = register(new Setting<>("Blocks", BlockSelection.Obsidian, v -> modesParent.getValue()));
    public Setting<Boolean> miscParent = register(new Setting<>("Misc", true, false));
    public Setting<Integer> placeDelay = register(new Setting<>("Place Delay", 50, 0, 500, v -> miscParent.getValue()));
    public Setting<Boolean> rotate = register(new Setting("Rotate", false, v -> miscParent.getValue()));
    public Setting<Boolean> bottomBlocks = register(new Setting("Bottom Blocks", false, v -> miscParent.getValue()));
    public Setting<Boolean> bottomBlocksExtend = register(new Setting("Bottom Blocks Extend", false, v -> bottomBlocks.getValue() && miscParent.getValue()));
    public Setting<Boolean> maxBlock = register(new Setting("Max Blocks", false, v -> miscParent.getValue()));
    public Setting<Integer> maxBlocksAmount = register(new Setting<>("Max Blocks Amount", 10, 0, 20, v -> maxBlock.getValue() && miscParent.getValue()));

    public enum PlaceMode {Vanilla, Packet}

    public enum SwingMode {Mainhand, Offhand}

    public enum DisableMode {OnComplete, Motion, Onground, Smart, StepHeight}

    public enum BlockSelection {Obsidian, Echest, Auto}

    Timer timer = new Timer();

    public Surround() {
        super("Surround", Category.COMBAT, "Surrounds you with Obsidian yuh.");
    }

    public void onLogin() {
        if (isEnabled())
            disable();
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        BlockPos center = PlayerUtil.getCenterPos(pos.getX(), pos.getY(), pos.getZ());

        switch (blocks.getValue()) {
            case Obsidian:
                itemSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                break;
            case Echest:
                itemSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
                break;
            case Auto:
                if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1)
                    itemSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                else if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1)
                    itemSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
                else disable();
                break;
        }

        if (itemSlot == -1)
            disable();

        switch (disableMode.getValue()) {
            case Smart:
                if (mc.player.motionY > 0.2 && !mc.player.onGround && mc.player.stepHeight > 0.6 || Step.getInstance().isEnabled())
                    disable();
                break;
            case Motion:
                if (mc.player.motionY > 0.2)
                    disable();
                break;
            case Onground:
                if (!mc.player.onGround)
                    disable();
                break;
            case OnComplete:
                if (mc.world.getBlockState(center.north()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(center.east()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(center.south()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(center.west()).getBlock().equals(Blocks.OBSIDIAN))
                    disable();
                break;
            case StepHeight:
                if (mc.player.stepHeight > 1)
                    disable();
                break;
        }

        if(!timer.passedMs(placeDelay.getValue()))
            return;

        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down().north()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down().north(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down().east()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down().east(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down().south()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down().south(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down().west()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down().west(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.north()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.north(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.east()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.east(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.south()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.south(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.west()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.west(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
            timer.reset();
        }
    }

    void placeSurroundBlocks(BlockPos pos, boolean rotate, boolean packet) {
        if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
            return;
        int currentItem = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = itemSlot;
        mc.playerController.updateController();
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate, packet, false, false, EnumHand.MAIN_HAND);
        mc.player.inventory.currentItem = currentItem;
        mc.playerController.updateController();
        ++maxBlocks;
    }
}
