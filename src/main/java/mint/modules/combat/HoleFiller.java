package mint.modules.combat;

import com.google.common.collect.Sets;
import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HoleFiller extends Module {
    public static final List<net.minecraft.block.Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    public static final List<net.minecraft.block.Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    int blockSlot;
    HashMap<BlockPos, Integer> filledFadeHoles = new HashMap();
    HashSet<BlockPos> fillableHoles = Sets.newHashSet();
    public Setting<Mode> mode = register(new Setting<>("FillMode", Mode.NORMAL));
    public enum Mode{NORMAL, SMART}
    public Setting<PlaceMode> placeMode = register(new Setting<>("PlaceMode", PlaceMode.VANILLA));
    public enum PlaceMode{VANILLA, PACKET}
    public Setting<SwingMode> swingMode = register(new Setting<>("SwingMode", SwingMode.MAINHAND));
    public enum SwingMode{MAINHAND, OFFHAND, NONE}
    public Setting<Block> block = register(new Setting<>("Block", Block.OBSIDIAN));
    public enum Block{OBSIDIAN, ECHEST, WEB}
    public Setting<OnGroundChecks> onGroundChecks = register(new Setting<>("OnGroundChecks", OnGroundChecks.NONE));
    public enum OnGroundChecks{SELF, TARGET, BOTH, NONE}
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", false));
    public Setting<Boolean> autoDisable = register(new Setting<>("AutoDisable", false));
    public Setting<Boolean> autoSwitch = register(new Setting<>("AutoSwitch", false));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch", false, v-> autoSwitch.getValue()));
    public Setting<Boolean> doubles = register(new Setting<>("DoubleHoles", false));
    public Setting<Boolean> throughWalls = register(new Setting<>("ThroughWalls", false));
    public Setting<Boolean> swordCheck = register(new Setting<>("SwordCheck", false));
    public Setting<Boolean> targetUnSafe = register(new Setting("TargetUnSafe", false, v-> mode.getValue() == Mode.SMART));
    public Setting<Integer> smartRange = register(new Setting<>("Smart-Range", 5, 0, 6, v-> mode.getValue() == Mode.SMART));
    public Setting<Integer> targetRange = register(new Setting<>("TargetRange", 9, 1, 15, v-> mode.getValue() == Mode.SMART));
    public Setting<Integer> rangeX = register(new Setting<>("X-Range", 5, 1, 6));
    public Setting<Integer> rangeY = register(new Setting<>("Y-Range", 5, 1, 6));
    public Setting<Boolean> render = register(new Setting<>("Render", false));
    public Setting<RenderMode> renderMode = register(new Setting<>("RenderMode", RenderMode.STATIC, v-> render.getValue()));
    public enum RenderMode{STATIC, FADE}
    public Setting<Boolean> box = register(new Setting("Box", false, v-> render.getValue()));
    public Setting<Integer> red = register(new Setting<>("BoxRed", 255, 0, 255, v-> render.getValue() && box.getValue()));
    public Setting<Integer> green = register(new Setting<>("BoxGreen", 255, 0, 255, v-> render.getValue() && box.getValue()));
    public Setting<Integer> blue = register(new Setting<>("BoxBlue", 255, 0, 255, v-> render.getValue() && box.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("BoxAlpha", 255, 0, 255, v-> render.getValue() && renderMode.getValue() == RenderMode.STATIC && box.getValue()));
    public Setting<Boolean> outline = register(new Setting("Outline", false, v-> render.getValue()));
    public Setting<Integer> outlineRed = register(new Setting<>("OutlineRed", 255, 0, 255, v-> render.getValue() && outline.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("OutlineGreen", 255, 0, 255, v-> render.getValue() && outline.getValue()));
    public Setting<Integer> outlineBlue = register(new Setting<>("OutlineBlue", 255, 0, 255, v-> render.getValue() && outline.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("OutlineAlpha", 255, 0, 255, v-> render.getValue() && renderMode.getValue() == RenderMode.STATIC && outline.getValue()));
    public Setting<Float> lineWidth = register(new Setting<>("LineWidth", 1.0f, 0.0f, 5.0f, v-> render.getValue() && outline.getValue()));
    public Setting<Integer> startAlpha = register(new Setting<>("StartAlpha", 255, 0, 255, v-> render.getValue() && renderMode.getValue() == RenderMode.FADE));
    public Setting<Integer> endAlpha = register(new Setting<>("EndAlpha", 0, 0, 255, v-> render.getValue() && renderMode.getValue() == RenderMode.FADE));
    public Setting<Integer> fadeStep = register(new Setting<>("FadeStep", 20, 10, 100, v-> render.getValue() && renderMode.getValue() == RenderMode.FADE));
    public HoleFiller(){
        super("Hole Filler", Category.COMBAT, "Fills safe spots (near enemies).");
    }

    public void onTick() {
            fillableHoles.clear();
            findFillableHoles();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if(render.getValue()) {
            if(renderMode.getValue() == RenderMode.FADE) {
                for (Map.Entry<BlockPos, Integer> entry : filledFadeHoles.entrySet()) {
                    filledFadeHoles.put(entry.getKey(), entry.getValue() - (fadeStep.getValue() / 10));
                    if (entry.getValue() <= endAlpha.getValue()) {
                        filledFadeHoles.remove(entry.getKey());
                        return;
                    }
                    RenderUtil.drawBoxESP(entry.getKey(), new Color(red.getValue(), green.getValue(), blue.getValue(), entry.getValue()),true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), entry.getValue()), lineWidth.getValue(), outline.getValue(), box.getValue(), entry.getValue(), true);
               }
            } else {
                for(BlockPos pos : fillableHoles){
                    RenderUtil.drawBoxESP(pos, new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()),true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), lineWidth.getValue(), outline.getValue(), box.getValue(), alpha.getValue(), true);
                }
            }
        }
    }

    public void onUpdate(){
        for(BlockPos pos : fillableHoles){
            switch(block.getValue()){
                case OBSIDIAN:
                    blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                    break;
                case ECHEST:
                    blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                    break;
                case WEB:
                    blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.WEB));
                    break;
            }
            switch (onGroundChecks.getValue()){
                case SELF:
                    if(!mc.player.onGround){
                        return;
                    }
                    break;
                case TARGET:
                    if(!getPlayerTarget(targetRange.getValue()).onGround){
                        return;
                    }
                    break;
                case BOTH:
                    if(!getPlayerTarget(targetRange.getValue()).onGround){
                        return;
                    }
                    if(!mc.player.onGround){
                        return;
                    }
                    break;
                case NONE:
                    break;
            }
        if(mode.getValue() == Mode.NORMAL){
            if(swordCheck.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD){
                return;
            }
            if (this.blockSlot == -1) {
                return;
            }
            int lastSlot = mc.player.inventory.currentItem;
            if(autoSwitch.getValue()) {
                if (silentSwitch.getValue()) {
                    InventoryUtil.SilentSwitchToSlot(blockSlot);
                } else {
                    mc.player.inventory.currentItem = blockSlot;
                }
            }
            if(throughWalls.getValue()) {
                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValue() == PlaceMode.PACKET, false, swingMode.getValue() != SwingMode.NONE, swingMode.getValue() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                        if (render.getValue() && renderMode.getValue() == RenderMode.FADE) {
                            if (!filledFadeHoles.containsKey(pos)) {
                                filledFadeHoles.put(pos, startAlpha.getValue());
                        }
                    }
                }
            } else if(canBlockBeSeen(pos)) {
                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValue() == PlaceMode.PACKET, false, swingMode.getValue() != SwingMode.NONE, swingMode.getValue() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                        if (render.getValue() && renderMode.getValue() == RenderMode.FADE) {
                            if (!filledFadeHoles.containsKey(pos)) {
                                filledFadeHoles.put(pos, startAlpha.getValue());
                            }
                    }
                }
            }
                if (autoSwitch.getValue() && silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
            }
                if(autoDisable.getValue()){
                    disable();
                }
          }

            if(mode.getValue() == Mode.SMART){
                if(swordCheck.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD){
                    return;
                }
                if (this.blockSlot == -1) {
                    return;
                }
                int lastSlot = mc.player.inventory.currentItem;
                blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                if(autoSwitch.getValue()) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(blockSlot);
                    } else {
                        mc.player.inventory.currentItem = blockSlot;
                    }
                }
                if(getPlayerTarget(targetRange.getValue()) != null && Objects.requireNonNull(getPlayerTarget(targetRange.getValue())).getDistanceSq(pos) < smartRange.getValue()) {
                    if(targetUnSafe.getValue()) {
                        if(getPlayerTarget(targetRange.getValue()) != null && !EntityUtil.isSafe(getPlayerTarget(targetRange.getValue()))) {
                            if(throughWalls.getValue()) {
                                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValue() == PlaceMode.PACKET, false, swingMode.getValue() != SwingMode.NONE, swingMode.getValue() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                    if (render.getValue() && renderMode.getValue() == RenderMode.FADE) {
                                        if (!filledFadeHoles.containsKey(pos)) {
                                            filledFadeHoles.put(pos, startAlpha.getValue());
                                       }
                                    }
                                }
                            } else if (canBlockBeSeen(pos)){
                                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValue() == PlaceMode.PACKET, false, swingMode.getValue() != SwingMode.NONE, swingMode.getValue() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                    if (render.getValue() && renderMode.getValue() == RenderMode.FADE) {
                                        if (!filledFadeHoles.containsKey(pos)) {
                                            filledFadeHoles.put(pos, startAlpha.getValue());
                                       }
                                    }
                                }
                            }
                        }
                    } else {
                        if(throughWalls.getValue()) {
                                if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValue() == PlaceMode.PACKET, false, swingMode.getValue() != SwingMode.NONE, swingMode.getValue() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                if (render.getValue() && renderMode.getValue() == RenderMode.FADE) {
                                    if (!filledFadeHoles.containsKey(pos)) {
                                        filledFadeHoles.put(pos, startAlpha.getValue());
                                   }
                                }
                            }
                        } else if (canBlockBeSeen(pos)){
                                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValue() == PlaceMode.PACKET, false, swingMode.getValue() != SwingMode.NONE, swingMode.getValue() == SwingMode.MAINHAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                    if (render.getValue() && renderMode.getValue() == RenderMode.FADE) {
                                        if (!filledFadeHoles.containsKey(pos)) {
                                            filledFadeHoles.put(pos, startAlpha.getValue());
                                    }
                                }
                            } else {
                                if (render.getValue() && renderMode.getValue() == RenderMode.FADE) {
                                    if (!filledFadeHoles.containsKey(pos)) {
                                        filledFadeHoles.put(pos, startAlpha.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
                if(autoSwitch.getValue() && silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
                }
                if(autoDisable.getValue()){
                    disable();
                }
            }
        }
    }


    public void findFillableHoles() {
        assert (mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - rangeX.getValue(); x < playerPos.getX() + rangeX.getValue(); ++x) {
            for (int z = playerPos.getZ() - rangeX.getValue(); z < playerPos.getZ() + rangeX.getValue(); ++z) {
                for (int y = playerPos.getY() + rangeY.getValue(); y > playerPos.getY() - rangeY.getValue(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                        if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            fillableHoles.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)) {
                            fillableHoles.add(pos);
                        }
                        if(doubles.getValue()) {
                            if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                                fillableHoles.add(pos);
                                fillableHoles.add(pos.north());
                            } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK)) {
                                fillableHoles.add(pos);
                                fillableHoles.add(pos.north());
                            } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK) {
                                fillableHoles.add(pos);
                                fillableHoles.add(pos.west());
                            } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.OBSIDIAN)) {
                                fillableHoles.add(pos);
                                fillableHoles.add(pos.west());
                        }
                    }
                }
            }
        }
    }

    public static EntityPlayer getPlayerTarget(int targetRange){
        EntityPlayer target = EntityUtil.getTarget(targetRange);
        if(target != null){
            return target;
        }
        return null;
    }

    public static boolean canBlockBeSeen(final BlockPos blockPos) {
        return Mint.INSTANCE.mc.world.rayTraceBlocks(new Vec3d(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + Mint.INSTANCE.mc.player.getEyeHeight(), Mint.INSTANCE.mc.player.posZ), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true, false) == null;
    }
}
