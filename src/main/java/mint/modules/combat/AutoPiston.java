package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class AutoPiston extends Module {
    public Setting<Float> targetRange = register(new Setting("Target Range", 5.0f, 0, 10.0f));
    public Setting<Integer> startDelay = register(new Setting("Start Delay", 100, 0, 1000));
    public Setting<Integer> placeDelay = register(new Setting("Delay", 100, 50, 1000));
    public Setting<Integer> crystalBreakDelay = register(new Setting("Crystal Break Delay", 150, 100, 1000));
    public Setting<Float> breakRange = register(new Setting("Break Range", 5.0f, 0.0f, 6.0f));
    public Setting<Boolean> packet = register(new Setting<>("Packet", false));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", false));
    public Setting<Boolean> blockSwing = register(new Setting<>("Block Swing", false));
    public Setting<BlockSwingHand> blockSwingMode = register(new Setting<>("Block Swing Mode", BlockSwingHand.MAINHAND, v -> blockSwing.getValue()));

    public enum BlockSwingHand {MAINHAND, OFFHAND, PACKET}

    public Setting<Boolean> packetBreak = register(new Setting<>("Packet Break", false, false));
    public Setting<Boolean> crystalPlaceSwing = register(new Setting<>("Crystal Place Swing", false));
    public Setting<PlaceSwingHand> placeSwingHand = register(new Setting<>("Place Swing Hand", PlaceSwingHand.MAINHAND, v -> crystalPlaceSwing.getValue()));

    public enum PlaceSwingHand {MAINHAND, OFFHAND, PACKET}

    public Setting<Boolean> crystalBreakSwing = register(new Setting<>("Crystal Break Swing", false));
    public Setting<BreakSwingHand> breakSwingHand = register(new Setting<>("Break Swing Hand", BreakSwingHand.MAINHAND, v -> crystalBreakSwing.getValue()));

    public enum BreakSwingHand {MAINHAND, OFFHAND, PACKET}


    Timer startTimer = new Timer();
    Timer delayTimer = new Timer();
    Timer crystalBreakDelayTimer = new Timer();
    EntityPlayer target;
    currentPos pos = new currentPos(BlockPos.ORIGIN, -1);

    public AutoPiston() {
        super("Auto Piston", Category.COMBAT, "Pushes crystals into holes using pistons.");
    }

    public void onLogin() {
        disable();
    }

    public void onEnable() {
        startTimer.reset();
        target = null;
    }

    public void onUpdate() {
        target = EntityUtil.getTarget(targetRange.getValue());

        if (pos == null)
            return;

        if (target == null)
            return;

        if (startTimer.passedMs(startDelay.getValue())) {
            if (delayTimer.passedMs(placeDelay.getValue())) {
                BlockPos playerPos = PlayerUtil.getPlayerPos(target);
                pos = findCurrentPos();
                int currentItem = mc.player.inventory.currentItem;
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal) {
                        if (mc.player.getDistance(entity) > breakRange.getValue())
                            continue;
                        if (crystalBreakDelayTimer.passedMs(crystalBreakDelay.getValue())) {
                            if (packetBreak.getValue()) {
                                mc.getConnection().sendPacket(new CPacketUseEntity(entity));
                            } else {
                                mc.playerController.attackEntity(mc.player, entity);
                            }
                            if (crystalBreakSwing.getValue())
                                swingArm(false, false);
                            delayTimer.reset();
                        }
                    }
                }
                if (pos.getBlockPos().equals(playerPos.north())) {
                    InventoryUtil.SilentSwitchToSlot(pos.getBlockSlot());
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.getBlockPos(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentItem;
                    mc.playerController.updateController();
                    crystalBreakDelayTimer.reset();
                    delayTimer.reset();
                } else {
                    InventoryUtil.SilentSwitchToSlot(pos.getBlockSlot());
                    BlockUtil.placeBlock(pos.getBlockPos(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentItem;
                    mc.playerController.updateController();
                    delayTimer.reset();
                }
            }
        }
    }

    public currentPos findCurrentPos() {
        if (target == null)
            return null;
        boolean northOccupied = false;
        BlockPos pos = PlayerUtil.getPlayerPos(target);
        if (!mc.world.getBlockState(pos.up().north()).getBlock().equals(Blocks.AIR)
                || !mc.world.getBlockState(pos.up().up().north()).getBlock().equals(Blocks.AIR)

                || (!mc.world.getBlockState(pos.up().north().north()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.up().north().north()).getBlock().equals(Blocks.PISTON))

                || (!mc.world.getBlockState(pos.up().north().north().north()).getBlock().equals(Blocks.REDSTONE_BLOCK) && !mc.world.getBlockState(pos.up().north().north().north()).getBlock().equals(Blocks.AIR)))
            northOccupied = true;

        if (!northOccupied) {
            if (mc.world.getBlockState(pos.north().north()).equals(Blocks.AIR))
                return new currentPos(pos.north().north(), InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
            else if (mc.world.getBlockState(pos.up().north().north()).getBlock().equals(Blocks.AIR))
                return new currentPos(pos.up().north().north(), InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)));
            else if (mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().up().up()).getBlock().equals(Blocks.AIR) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.north().up())).isEmpty())
                return new currentPos(pos.north(), InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL));
            else if (mc.world.getBlockState(pos.up().north().north().north()).getBlock().equals(Blocks.AIR))
                return new currentPos(pos.up().north().north().north(), InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)));
            else if (mc.world.getBlockState(pos.up().north().north().north()).getBlock().equals(Blocks.REDSTONE_BLOCK))
                return new currentPos(null, mc.player.inventory.currentItem);
        }

        return null;
    }

    public void swingArm(boolean place, boolean blockSwing) {
        if (place) {
            if (blockSwing) {
                switch (blockSwingMode.getValue()) {
                    case MAINHAND:
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        break;
                    case OFFHAND:
                        mc.player.swingArm(EnumHand.OFF_HAND);
                        break;
                    case PACKET:
                        mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
                        break;
                }
            } else {
                switch (placeSwingHand.getValue()) {
                    case MAINHAND:
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        break;
                    case OFFHAND:
                        mc.player.swingArm(EnumHand.OFF_HAND);
                        break;
                    case PACKET:
                        mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
                        break;
                }
            }
        } else {
            switch (breakSwingHand.getValue()) {
                case MAINHAND:
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    break;
                case OFFHAND:
                    mc.player.swingArm(EnumHand.OFF_HAND);
                    break;
                case PACKET:
                    mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
                    break;
            }
        }
    }

    static class currentPos {
        BlockPos blockPos;
        int blockSlot;

        public currentPos(BlockPos blockPos, int blockSlot) {
            this.blockPos = blockPos;
            this.blockSlot = blockSlot;
        }

        public int getBlockSlot() {
            return blockSlot;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

    }
}
