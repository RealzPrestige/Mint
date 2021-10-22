package mint.modules.combat;

import com.google.common.collect.Sets;
import mint.clickgui.setting.Setting;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.movement.Step;
import mint.utils.*;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Surround extends Module {
    public int maxBlocks;
    public int itemSlot;
    public HashMap<BlockPos, Integer> surroundFadeBlock = new HashMap<>();
    public HashSet<BlockPos> staticBlocks = Sets.newHashSet();
    public Setting<Boolean> modesParent = register(new Setting<>("Modes", true, false));
    public Setting<PlaceMode> placeMode = register(new Setting<>("Place Mode", PlaceMode.Vanilla, v-> modesParent.getValue()));
    public Setting<SwingMode> swingMode = register(new Setting<>("Swing Mode", SwingMode.Mainhand, v-> modesParent.getValue()));
    public Setting<DisableMode> disableMode = register(new Setting<>("Disable Mode", DisableMode.Smart, v-> modesParent.getValue()));
    public Setting<BlockSelection> blocks = register(new Setting<>("Blocks", BlockSelection.Obsidian, v-> modesParent.getValue()));
    public Setting<Boolean> miscParent = register(new Setting<>("Misc", true, false));
    public Setting<Boolean> rotate = register(new Setting("Rotate", false, v-> miscParent.getValue()));
    public Setting<Boolean> bottomBlocks = register(new Setting("Bottom Blocks", false, v-> miscParent.getValue()));
    public Setting<Boolean> bottomBlocksExtend = register(new Setting("Bottom Blocks Extend", false, v -> bottomBlocks.getValue() && miscParent.getValue()));
    public Setting<Boolean> maxBlock = register(new Setting("Max Blocks", false, v-> miscParent.getValue()));
    public Setting<Integer> maxBlocksAmount = register(new Setting<>("Max Blocks Amount", 10, 0, 20, v -> maxBlock.getValue() && miscParent.getValue()));
    public Setting<Boolean> renderParent = register(new Setting<>("Renders", true, false));
    public Setting<Boolean> render = register(new Setting("Render", false, v -> renderParent.getValue()));
    public Setting<RenderMode> renderMode = register(new Setting<>("Render Mode", RenderMode.Static, v -> render.getValue() && renderParent.getValue()));
    public Setting<Boolean> box = register(new Setting("Box", false, v -> render.getValue() && renderParent.getValue()));
    public Setting<Integer> red = register(new Setting<>("Box Red", 255, 0, 255, v -> render.getValue() && box.getValue() && renderParent.getValue()));
    public Setting<Integer> green = register(new Setting<>("Box Green", 255, 0, 255, v -> render.getValue() && box.getValue() && renderParent.getValue()));
    public Setting<Integer> blue = register(new Setting<>("Box Blue", 255, 0, 255, v -> render.getValue() && box.getValue() && renderParent.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("Box Alpha", 255, 0, 255, v -> render.getValue() && renderMode.getValue().equals(RenderMode.Static) && box.getValue() && renderParent.getValue()));
    public Setting<Boolean> outline = register(new Setting("Outline", false, v -> render.getValue() && renderParent.getValue()));
    public Setting<Integer> outlineRed = register(new Setting<>("Outline Red", 255, 0, 255, v -> render.getValue() && outline.getValue() && renderParent.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("Outline Green", 255, 0, 255, v -> render.getValue() && outline.getValue() && renderParent.getValue()));
    public Setting<Integer> outlineBlue = register(new Setting<>("Outline Blue", 255, 0, 255, v -> render.getValue() && outline.getValue() && renderParent.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("Outline Alpha", 255, 0, 255, v -> render.getValue() && renderMode.getValue().equals(RenderMode.Static) && outline.getValue() && renderParent.getValue()));
    public Setting<Float> lineWidth = register(new Setting<>("Line Width", 1.0f, 0.0f, 5.0f, v -> render.getValue() && outline.getValue() && renderParent.getValue()));
    public Setting<Integer> startAlpha = register(new Setting<>("Start Alpha", 255, 0, 255, v -> render.getValue() && renderMode.getValue().equals(RenderMode.Fade) && renderParent.getValue()));
    public Setting<Integer> endAlpha = register(new Setting<>("End Alpha", 0, 0, 255, v -> render.getValue() && renderMode.getValue().equals(RenderMode.Fade) && renderParent.getValue()));
    public Setting<Integer> fadeStep = register(new Setting<>("Fade Step", 20, 10, 100, v -> render.getValue() && renderMode.getValue().equals(RenderMode.Fade) && renderParent.getValue()));

    public enum PlaceMode {Vanilla, Packet}

    public enum SwingMode {Mainhand, Offhand}

    public enum DisableMode {OnComplete, Motion, Onground, Smart, StepHeight}

    public enum BlockSelection {Obsidian, Echest, Auto}

    public enum RenderMode {Static, Fade}

    public Surround() {
        super("SurroundRewrite", Category.COMBAT, "Surrounds you with Obsidian.");
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

        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.down().north()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.down().north(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.down().east()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.down().east(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.down().south()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.down().south(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.down().west()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.down().west(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.down()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.down(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.north()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.north(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.east()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.east(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.south()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.south(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) || mc.world.getBlockState(center.west()).getBlock().equals(Blocks.AIR))
            placeSurroundBlocks(center.west(), rotate.getValue(), placeMode.getValue().equals(PlaceMode.Packet));
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (!render.getValue())
            return;
        if (renderMode.getValue().equals(RenderMode.Fade)) {
            for (Map.Entry<BlockPos, Integer> entry : surroundFadeBlock.entrySet()) {
                surroundFadeBlock.put(entry.getKey(), entry.getValue() - (fadeStep.getValue() / 10));
                if (entry.getValue() <= endAlpha.getValue())
                    surroundFadeBlock.remove(entry.getKey());
                RenderUtil.drawBoxESP(entry.getKey(), new Color(red.getValue(), green.getValue(), blue.getValue(), entry.getValue()), true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), entry.getValue()), lineWidth.getValue(), outline.getValue(), box.getValue(), entry.getValue(), true);
            }
        } else
            for (BlockPos pos : staticBlocks)
                RenderUtil.drawBoxESP(pos, new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()), true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), lineWidth.getValue(), outline.getValue(), box.getValue(), alpha.getValue(), true);
    }

    void addRender(BlockPos pos) {
        if (render.getValue() && renderMode.getValue().equals(RenderMode.Fade))
            surroundFadeBlock.put(pos, startAlpha.getValue());
        else if (render.getValue() && renderMode.getValue().equals(RenderMode.Static))
            staticBlocks.add(pos);
    }

    void placeSurroundBlocks(BlockPos pos, boolean rotate, boolean packet) {
        int currentItem = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = itemSlot;
        mc.playerController.updateController();
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate, packet, false, false, EnumHand.MAIN_HAND);
        mc.player.inventory.currentItem = currentItem;
        mc.playerController.updateController();
        ++maxBlocks;
        addRender(pos);
    }
}
