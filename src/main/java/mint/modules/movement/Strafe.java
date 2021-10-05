package mint.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.events.MoveEvent;
import mint.events.UpdateWalkingPlayerEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Strafe extends Module {
    private static Strafe INSTANCE = new Strafe();
    public Setting<Boolean> strafeTest = register(new Setting<>("Strafe Test", false));
    public Setting<Float> timerAmount = register(new Setting<>("Timer Amount", 1.3f, 1.0f, 2.0f, v -> strafeTest.getValue()));
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.STRAFE, v -> !strafeTest.getValue()));
    public Setting<Bind> switchBind = register(new Setting<>("SwitchBind", new Bind(-1), v -> !strafeTest.getValue()));
    public Setting<Boolean> useTimer = register(new Setting("Use Timer", false, v -> mode.getValue() == Mode.STRAFE && !strafeTest.getValue()));

    private int level;
    private double moveSpeed;
    private double lastDist;
    private int timerDelay;

    public enum Mode {STRAFE, INSTANT}

    public boolean changeY = false;
    public double minY = 0.0;
    private int currentState = 1;
    private double motionSpeed;
    private double prevDist;
    int ticks;
    int delay;

    public Strafe() {
        super("Strafe", Category.MOVEMENT, "Tweaks and speeds up movement.");
        setInstance();
    }

    public static Strafe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Strafe();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        changeY = false;
        delay = 0;
    }

    @SubscribeEvent
    public void onPlayerUpdateWalking(UpdateWalkingPlayerEvent event) {
        if (mode.getValue() == Mode.STRAFE && !strafeTest.getValue()) {
            final double xDist = mc.player.posX - mc.player.prevPosX;
            final double zDist = mc.player.posZ - mc.player.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
        }
    }

    public void onTick() {

        if (ticks < 12) {
            ++ticks;
        }
        if (ticks > 10) {
            if (switchBind.getValue().getKey() > -1) {
                if (Keyboard.isKeyDown(switchBind.getValue().getKey())) {
                    if (mode.getValue() == Mode.INSTANT && !strafeTest.getValue()) {
                        mode.setValue(Mode.STRAFE);
                        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.BOLD + " Strafe: " + ChatFormatting.AQUA + "Mode set to: " + ChatFormatting.DARK_AQUA + ChatFormatting.BOLD + "Strafe"), 1);
                        ticks = 0;
                    } else if (mode.getValue() == Mode.STRAFE && !strafeTest.getValue()) {
                        mode.setValue(Mode.INSTANT);
                        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.BOLD + " Strafe: " + ChatFormatting.AQUA + "Mode set to: " + ChatFormatting.DARK_AQUA + ChatFormatting.BOLD + "Instant"), 1);
                        ticks = 0;
                    }
                }
            }
        }
    }

    public void onLogin() {
        disable();
    }


    public void onUpdate() {
        if (mc.player != null && strafeTest.getValue()) {
            prevDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
            mc.timer.tickLength = 50.0F / timerAmount.getValue();
            if (!mc.player.isSprinting()) {
                mc.player.setSprinting(true);
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }

        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (mode.getValue() == Mode.STRAFE && !strafeTest.getValue()) {
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
                moveSpeed = 1.35 * EntityUtil.getDefaultSpeed() - 0.01;
            } else if (level == 2) {
                level = 3;
                event.y = (mc.player.motionY = 0.399399995803833);
                moveSpeed *= 2.149;
            } else if (level == 3) {
                level = 4;
                final double difference = 0.66 * (lastDist - EntityUtil.getDefaultSpeed());
                moveSpeed = lastDist - difference;
            } else {
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) {
                    level = 1;
                }
                moveSpeed = lastDist - lastDist / 159.0;
            }
            moveSpeed = Math.max(moveSpeed, EntityUtil.getDefaultSpeed());
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
            if (forward == 0.0f && strafe == 0.0f) {
                event.x = (0.0);
                event.z = (0.0);
            }
        } else if (mode.getValue() == Mode.INSTANT && !strafeTest.getValue()) {
            if (!(event.getStage() != 0 || nullCheck() || mc.player.isSneaking() || mc.player.isInWater() || mc.player.isInLava() || mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) || !mc.player.onGround) {
                MovementInput movementInput = mc.player.movementInput;
                float moveForward = movementInput.moveForward;
                float moveStrafe = movementInput.moveStrafe;
                float rotationYaw = mc.player.rotationYaw;
                if ((double) moveForward == 0.0 && (double) moveStrafe == 0.0) {
                    event.x = (0.0);
                    event.z = (0.0);
                } else {
                    if ((double) moveForward != 0.0) {
                        if ((double) moveStrafe > 0.0) {
                            rotationYaw += (float) ((double) moveForward > 0.0 ? -45 : 45);
                        } else if ((double) moveStrafe < 0.0) {
                            rotationYaw += (float) ((double) moveForward > 0.0 ? 45 : -45);
                        }
                        moveStrafe = 0.0f;
                    }
                    moveStrafe = moveStrafe == 0.0f ? moveStrafe : ((double) moveStrafe > 0.0 ? 1.0f : -1.0f);
                    event.x = ((double) moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + (double) moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                    event.z = ((double) moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - (double) moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
                }
            }
        } else if (strafeTest.getValue()) {
            switch (currentState) {
                case 0:
                    ++currentState;
                    prevDist = 0.0D;
                    break;
                case 1:
                default:
                    if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D)).size() > 0 || mc.player.collidedVertically) && currentState > 0) {
                        currentState = mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F ? 0 : 1;
                    }

                    motionSpeed = prevDist - prevDist / 159.0D;
                    break;
                case 2:
                    double var2 = 0.40123128D;
                    if ((mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
                        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                            var2 += (float) (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1F;
                        }

                        event.y = (mc.player.motionY = var2);
                        motionSpeed *= 2.149D;
                    }
                    break;
                case 3:
                    motionSpeed = prevDist - 0.76D * (prevDist - EntityUtil.getBaseMotionSpeed());
            }

            motionSpeed = Math.max(motionSpeed, EntityUtil.getBaseMotionSpeed());
            double var4 = mc.player.movementInput.moveForward;
            double var6 = mc.player.movementInput.moveStrafe;
            double var8 = mc.player.rotationYaw;
            if (var4 == 0.0D && var6 == 0.0D) {
                event.x = (0.0D);
                event.z = (0.0D);
            }

            if (var4 != 0.0D && var6 != 0.0D) {
                var4 *= Math.sin(0.7853981633974483D);
                var6 *= Math.cos(0.7853981633974483D);
            }

            event.x = ((var4 * motionSpeed * -Math.sin(Math.toRadians(var8)) + var6 * motionSpeed * Math.cos(Math.toRadians(var8))) * 0.99D);
            event.z = ((var4 * motionSpeed * Math.cos(Math.toRadians(var8)) - var6 * motionSpeed * -Math.sin(Math.toRadians(var8))) * 0.99D);
            ++currentState;
        }
    }

    double round(final double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
