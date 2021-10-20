package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * @author Zenov
 * @author kambing
 * catalyst for ncp
 */
public class Step extends Module {
    public int ticks;
    public Setting<Mode> mode = register(new Setting("Mode", Mode.Vanilla));

    public enum Mode {Vanilla, Normal, Timer, NCP}

    public Setting<Boolean> cancelLiquids = register(new Setting("Pause In Liquids", true));
    public Setting<Integer> height = register(new Setting("Height", 2, 1, 4));
    double[] oneblockPositions = new double[]{0.42, 0.75};
    double[] futurePositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};

    //why do these even exist what the fuck
    double[] twoFiveOffset = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
    double[] fourBlockPositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 1.78, 1.63, 1.51, 1.9, 2.21, 2.45, 2.43, 2.78, 2.63, 2.51, 2.9, 3.21, 3.45, 3.43};
    double[] selectedPositions = new double[0];
    int packets;

    public Step() {
        super("Step", Category.MOVEMENT, "Allows you to step up blocks.");
    }

    @Override
    public void onEnable() {
        ticks = 0;
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        if (cancelLiquids.getValue() && EntityUtil.isInLiquid()) {
            return;
        }
        if (mode.getValue() == Mode.Vanilla) {
            mc.player.stepHeight = height.getValue();

        } else if (mode.getValue() == Mode.Normal) {
            switch (height.getValue()) {
                case 1: {
                    selectedPositions = oneblockPositions;
                    break;
                }
                case 2: {
                    selectedPositions = futurePositions;
                    break;
                }
                case 3: {
                    selectedPositions = twoFiveOffset;
                    break;
                }
                case 4: {
                    selectedPositions = fourBlockPositions;
                    break;
                }
            }
            if (mc.player.collidedHorizontally && mc.player.onGround) {
                ++packets;
            }
            if (mc.player.onGround && mc.player.collidedVertically && mc.player.fallDistance == 0.0f && !mc.gameSettings.keyBindJump.pressed && mc.player.collidedHorizontally && !mc.player.isOnLadder() && (packets > selectedPositions.length - 2 || packets > 0)) {
                for (double position : selectedPositions) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + position, mc.player.posZ, true));
                }
                mc.player.setPosition(mc.player.posX, mc.player.posY + selectedPositions[selectedPositions.length - 1], mc.player.posZ);
                packets = 0;
            }
        }else if (mode.getValue() == Mode.NCP) {
            final double[] forward = forward(0.1);
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            boolean b4 = false;
            if (Step.mc.world.getCollisionBoxes((Entity)Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 2.6, forward[1])).isEmpty() && !Step.mc.world.getCollisionBoxes((Entity)Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 2.4, forward[1])).isEmpty()) {
                b = true;
            }
            if (Step.mc.world.getCollisionBoxes((Entity)Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 2.1, forward[1])).isEmpty()) {
                if (!Step.mc.world.getCollisionBoxes((Entity)Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 1.9, forward[1])).isEmpty()) {
                    b2 = true;
                }
            }
            if (Step.mc.world.getCollisionBoxes((Entity)Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 1.6, forward[1])).isEmpty() && !Step.mc.world.getCollisionBoxes((Entity)Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 1.4, forward[1])).isEmpty()) {
                b3 = true;
            }
            if (Step.mc.world.getCollisionBoxes((Entity)Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 1.0, forward[1])).isEmpty() && !Step.mc.world.getCollisionBoxes((Entity)Step.mc.player, Step.mc.player.getEntityBoundingBox().offset(forward[0], 0.6, forward[1])).isEmpty()) {
                b4 = true;
            }
            if (Step.mc.player.collidedHorizontally && (Step.mc.player.moveForward != 0.0f || Step.mc.player.moveStrafing != 0.0f)) {
                if (Step.mc.player.onGround) {
                    if (b4 && this.height.getValue() >= 1.0) {
                        final double[] array = { 0.42, 0.753 };
                        for (int length = array.length, i = 0; i < length; ++i) {
                            Step.mc.player.connection.sendPacket(new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + array[i], Step.mc.player.posZ, Step.mc.player.onGround));
                        }
                        Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + 1.0, Step.mc.player.posZ);
                        this.ticks = 1;
                    }
                    if (b3 && this.height.getValue() >= 1.5) {
                        final double[] array2 = { 0.42, 0.75, 1.0, 1.16, 1.23, 1.2 };
                        for (int j = 0; j < array2.length; ++j) {
                            Step.mc.player.connection.sendPacket(new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + array2[j], Step.mc.player.posZ, Step.mc.player.onGround));
                        }
                        Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + 1.5, Step.mc.player.posZ);
                        this.ticks = 1;
                    }
                    if (b2 && this.height.getValue() >= 2.0) {
                        final double[] array3 = { 0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43 };
                        for (int k = 0; k < array3.length; ++k) {
                            Step.mc.player.connection.sendPacket(new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + array3[k], Step.mc.player.posZ, Step.mc.player.onGround));
                        }
                        Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + 2.0, Step.mc.player.posZ);
                        this.ticks = 2;
                    }
                    if (b && this.height.getValue() >= 2.5) {
                        final double[] array4 = { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 };
                        for (double v : array4) {
                            Step.mc.player.connection.sendPacket(new CPacketPlayer.Position(Step.mc.player.posX, Step.mc.player.posY + v, Step.mc.player.posZ, Step.mc.player.onGround));
                        }
                        Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + 2.5, Step.mc.player.posZ);
                        this.ticks = 2;
                    }
                }
            }

        } else if (mode.getValue() == Mode.Timer) {
            mc.player.stepHeight = height.getValue();
            if (Step.mc.player.collidedHorizontally && (Step.mc.player.moveForward != 0.0f || Step.mc.player.moveStrafing != 0.0f)) {
                mc.timer.tickLength = 50.0f / 0.6f;
            } else {
                mc.timer.tickLength = 50.0f;
            }
        }
    }
    public static double[] forward(final double kambingdarealnigga) {
        float moveForward = Minecraft.getMinecraft().player.movementInput.moveForward;
        float moveStrafe = Minecraft.getMinecraft().player.movementInput.moveStrafe;
        float n2 = Minecraft.getMinecraft().player.prevRotationYaw + (Minecraft.getMinecraft().player.rotationYaw - Minecraft.getMinecraft().player.prevRotationYaw) * Minecraft.getMinecraft().getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                n2 += ((moveForward > 0.0f) ? -45 : 45);
            }
            else if (moveStrafe < 0.0f) {
                n2 += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(n2 + 90.0f));
        final double cos = Math.cos(Math.toRadians(n2 + 90.0f));
        return new double[] { moveForward * kambingdarealnigga * cos + moveStrafe * kambingdarealnigga * sin, moveForward * kambingdarealnigga * sin - moveStrafe * kambingdarealnigga * cos };
    }
}