package mint.modules.movement;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.events.MoveEvent;
import mint.events.Render2DEvent;
import mint.events.UpdateWalkingPlayerEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 *
 * @Author zPrestige (& Kambing)
 * @Since 24/09/2021
 *
 */
public class Speed extends Module {

    private int level;
    private double moveSpeed;
    private double lastDist;
    private int timerDelay;
    public double speedometerCurrentSpeed = 0.0;

    public Setting<Boolean> useTimer = register(new Setting<>("Use Timer", false));

    public Speed() {
        super("Speed", Category.MOVEMENT, "");
    }

    @SubscribeEvent
    public void onPlayerUpdateWalking(UpdateWalkingPlayerEvent event) {
        final double xDist = mc.player.posX - mc.player.prevPosX;
        final double zDist = mc.player.posZ - mc.player.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        ++timerDelay;
        timerDelay %= 5;
        if (timerDelay != 0) {
            if (useTimer.getValue()) {
                mc.timer.tickLength = 50.0f / 1.0f;
            }
        } else if (EntityUtil.isMoving()) {
            if (useTimer.getValue()) {
                mc.timer.tickLength = 50.0f / 1.3f;
            }
            final EntityPlayerSP player2 = mc.player;
            player2.motionX *= 1.0199999809265137;
            final EntityPlayerSP player3 = mc.player;
            player3.motionZ *= 1.0199999809265137;
        }
        if (mc.player.onGround && EntityUtil.isMoving()) {
            level = 2;
        }
        if (round(mc.player.posY - (int) mc.player.posY) == round(0.138)) {
            final EntityPlayerSP player4;
            final EntityPlayerSP player = player4 = mc.player;
            player4.motionY -= 0.08;
            event.y = (event.y - 0.09316090325960147);
            player.posY -= 0.09316090325960147;
        }
        if (level == 1 && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
            level = 2;
            moveSpeed = 1.35 * getDefaultSpeed() - 0.01;
        } else if (level == 2) {
            level = 3;
            event.y = (mc.player.motionY = 0.399399995803833);
            moveSpeed *= 2.149;
        } else if (level == 3) {
            level = 4;
            final double difference = 0.66 * (lastDist - getDefaultSpeed());
            moveSpeed = lastDist - difference;
        } else {
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) {
                level = 1;
            }
            moveSpeed = lastDist - lastDist / 159.0;
        }
        moveSpeed = Math.max(moveSpeed, getDefaultSpeed());
        final MovementInput movementInput = mc.player.movementInput;
        float forward = movementInput.moveForward;
        float strafe = movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0f && strafe == 0.0f) {
            event.x = (0.0);
            event.z = (0.0);
        } else if (forward != 0.0f) {
            if (strafe >= 1.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
                strafe = 0.0f;
            } else if (strafe <= -1.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
                strafe = 0.0f;
            }
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double mx2 = Math.cos(Math.toRadians(yaw + 90.0f));
        final double mz2 = Math.sin(Math.toRadians(yaw + 90.0f));
        event.x = (forward * moveSpeed * mx2 + strafe * moveSpeed * mz2);
        event.z = (forward * moveSpeed * mz2 - strafe * moveSpeed * mx2);
        mc.player.stepHeight = 0.6f;
        if (forward == 0.0f && strafe == 0.0f) {
            event.x = (0.0);
            event.z = (0.0);
        }
    }

    double round(final double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    static double getDefaultSpeed() {
        double defaultSpeed = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }
        return defaultSpeed;
    }
}
