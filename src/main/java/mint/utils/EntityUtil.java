package mint.utils;

import mint.Mint;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil  {

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{Mint.INSTANCE.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - Mint.INSTANCE.mc.player.rotationYaw), Mint.INSTANCE.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - Mint.INSTANCE.mc.player.rotationPitch)};
    }

    public static EntityPlayer getTarget(final float range) {
        EntityPlayer currentTarget = null;
        for (int size = Mint.INSTANCE.mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = Mint.INSTANCE.mc.world.playerEntities.get(i);
            if (!EntityUtil.isntValid(player, range)) {
                if (currentTarget == null) {
                    currentTarget = player;
                }
                else if (Mint.INSTANCE.mc.player.getDistanceSq( player ) < Mint.INSTANCE.mc.player.getDistanceSq( currentTarget )) {
                    currentTarget = player;
                }
            }
        }
        return currentTarget;
    }
    public static boolean isntValid(Entity entity, double range) {
        return entity == null || EntityUtil.isDead(entity) || entity.equals(Mint.INSTANCE.mc.player) || entity instanceof EntityPlayer && Mint.friendManager.isFriend(entity.getName()) || Mint.INSTANCE.mc.player.getDistanceSq(entity) > MathUtil.square(range);
    }
    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }
    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static boolean isSafe(Entity entity) {
        return EntityUtil.isSafe(entity, 0, false);
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + (double) Mint.INSTANCE.mc.player.getEyeHeight(), Mint.INSTANCE.mc.player.posZ);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<>();
        for (Vec3d vector : EntityUtil.getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = Mint.INSTANCE.mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow))
                continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = EntityUtil.getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }


    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isAlive(Entity entity) {
        return EntityUtil.isLiving(entity) && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0.0f;
    }

    public static boolean isDead(Entity entity) {
        return !EntityUtil.isAlive(entity);
    }
    public static boolean isMoving() {
        return (double) Mint.INSTANCE.mc.player.moveForward != 0.0 || (double) Mint.INSTANCE.mc.player.moveStrafing != 0.0;
    }

    public static void packetJump(boolean offground) {
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + 0.4199999, Mint.INSTANCE.mc.player.posZ, offground));
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + 0.7531999, Mint.INSTANCE.mc.player.posZ, offground));
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + 1.0013359, Mint.INSTANCE.mc.player.posZ, offground));
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + 1.1661092, Mint.INSTANCE.mc.player.posZ, offground));
    }
}

