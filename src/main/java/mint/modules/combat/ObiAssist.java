package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.BlockUtil;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import mint.utils.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static mint.modules.miscellaneous.FakePlayer.calculateDamage;


/**
 * @author kambing
 * @since 30/8/21
 * credits: salhack for calcs
 */

public class ObiAssist extends Module {
    private final Setting<Boolean> packet = register(new Setting<>("PacketSwitch", true));
    private final Setting<Boolean> packethand = register(new Setting<>("PacketHand", true));
    private final Setting<Boolean> render = register(new Setting<>("Render", true));
    private final Setting<Double> range = register(new Setting<>("TargetMaxRange", 10.0, 5.0, 15.0));
    private final Setting<Integer> delay = register(new Setting<>("MSDelay", 200, 0, 500));
    private final Timer delayTimer = new Timer();

    public ObiAssist() {
        super("Obby Assist", Category.COMBAT, "Place obsidian to support your AutoCrystal in pvp.");
    }


    @Override
    public void onUpdate() {
        EntityPlayer target = AutoCrystal.getInstance().targetPlayer;
        int slot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int old = mc.player.inventory.currentItem;
        EnumHand hand = null;

        if (target == null)
            return;

        if (AutoCrystal.getInstance().finalPos != null)
            return;

        //switch shit kinda messy
        if (AutoCrystal.getInstance().isEnabled() && target != null) {
            if (slot != -1) {
                if (delayTimer.passedMs(delay.getValue())) {
                    if (mc.player.inventory.currentItem != slot) {
                        if (packet.getValue()) {
                            if (mc.player.isHandActive()) {
                                hand = mc.player.getActiveHand();
                            }
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                        }
                    }
                }
            }
        }
        try {
            if (!(target.getDistance(mc.player) > range.getValue())) {
                float range = AutoCrystal.getInstance().placeRange.getValue();
                float targetDMG = 0.0f;
                float minDmg = AutoCrystal.getInstance().minimumDamage.getValue();
                BlockPos targetPos = null;
                for (BlockPos pos : BlockUtil.getSphere(getPlayerPosFloored(), range, (int) range, false, true, 0)) {
                    validResult result = valid(pos);
                    if (result != validResult.Ok) continue;
                    if (!canPlaceCrystalIfObbyWasAtPos(pos)) continue;
                    float tempDMG = calculateDamage(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, target);
                    final float self = EntityUtil.calculatePos(pos, mc.player);
                    if (EntityUtil.getHealth(mc.player) > self + 0.5f && AutoCrystal.getInstance().maximumSelfDamage.getValue() > self && (tempDMG = EntityUtil.calculatePos(pos, target)) > AutoCrystal.getInstance().maximumSelfDamage.getValue() && tempDMG > self) {
                        if (tempDMG <= minDmg) {
                            if (tempDMG <= 2.0f) {
                                continue;
                            }
                        }
                    }
                    if (tempDMG >= targetDMG) {
                        targetPos = pos;
                        targetDMG = tempDMG;
                    }
                }

                if (targetPos != null && render.getValue()) {
                    // AutoCrystal.getInstance().renderPos = targetPos;
                }

                if (targetPos != null) {
                    BlockUtil.placeBlock(targetPos);
                    delayTimer.reset();
                }
                if (packet.getValue()) {
                    if (slot != -1) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(old));
                        if (packethand.getValue() && hand != null) {
                            mc.player.setActiveHand(hand);
                        }
                    }
                }
            }

        } catch (NullPointerException ignored) {
            //to avoid ticking entity crash
        }
    }

    /**
     * @author kambing
     * 23/9/2021
     */
    public BlockPos getBestBlock() {
        float range = AutoCrystal.getInstance().placeRange.getValue();
        final List<BlockPos> blocks = BlockUtil.getSphere(range, true);
        EntityPlayer target = AutoCrystal.getInstance().targetPlayer;
        for (BlockPos block : blocks) {
            /** all the checks **/
            if (target.getDistance(mc.player) >= range) continue;
            if (target == mc.player || !(target instanceof EntityPlayer)) continue;
            if (target == null) continue;
            if (target.isDead || target.getHealth() <= 0) continue;
            /** /////////// **/
            double bestDmg = 0;
            BlockPos bestBlock = null;
            float targetDmg = calculateDamage(block.getX() + 0.5, block.getY() + 1.0, block.getZ() + 0.5, target);
            float minDmg = AutoCrystal.getInstance().minimumDamage.getValue();
            if (targetDmg < minDmg) continue;
            if (targetDmg > bestDmg) {
                bestBlock = block;
            }
            return bestBlock;
        }
    return null;
    }



    public enum validResult {
        NoEntityCollision,
        AlreadyBlockThere,
        NoNeighbors,
        Ok,
    }

    @Override
    public void onDisable() {
        delayTimer.reset();
    }

    public static boolean canPlaceCrystalIfObbyWasAtPos(final BlockPos pos) {

        final Block floor = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
        final Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

        if (floor == Blocks.AIR && ceil == Blocks.AIR) {
            return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0))).isEmpty();
        }

        return false;
    }

    /*
     * this is kinda chinese
     */

    public static BlockPos getPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static validResult valid(BlockPos pos) {
        // There are no entities to block placement,
        if (!mc.world.checkNoEntityCollision(new AxisAlignedBB(pos)))
            return validResult.NoEntityCollision;

        if (!checkForNeighbours(pos))
            return validResult.NoNeighbors;

        IBlockState l_State = mc.world.getBlockState(pos);

        if (l_State.getBlock() == Blocks.AIR) {
            final BlockPos[] l_Blocks =
                    {pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};

            for (BlockPos l_Pos : l_Blocks) {
                IBlockState l_State2 = mc.world.getBlockState(l_Pos);

                if (l_State2.getBlock() == Blocks.AIR)
                    continue;

                for (final EnumFacing side : EnumFacing.values()) {
                    final BlockPos neighbor = pos.offset(side);

                    if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                        return validResult.Ok;
                    }
                }
            }

            return validResult.NoNeighbors;
        }

        return validResult.AlreadyBlockThere;
    }

    public static boolean checkForNeighbours(BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour))
                    return true;
            }
            return false;
        }
        return true;
    }

    public static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable())
                return true;
        }
        return false;
    }
}

