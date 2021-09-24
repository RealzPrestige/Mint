package mint.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.events.MoveEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Strafe extends Module {
    private static Strafe INSTANCE = new Strafe();
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.STRAFE));
    public Setting<Bind> switchBind = register(new Setting<>("SwitchBind", new Bind(-1)));
    public enum Mode{STRAFE, INSTANT}
    public Setting<Double> airSpeed = register(new Setting("Air Speed", 1.1, 0.5, 1.3, v-> mode.getValue() == Mode.STRAFE));
    public Setting<Double> downSpeed = register(new Setting("Down Speed", 1.1, 0.5, 1.3, v-> mode.getValue() == Mode.STRAFE));
    public Setting<Double> groundSpeed = register(new Setting("Ground Speed", 1.1, 0.5, 1.3, v-> mode.getValue() == Mode.STRAFE));
    public Setting<Double> jumpSpeed = register(new Setting("Jump Speed", 1.1, 0.5, 1.3, v-> mode.getValue() == Mode.STRAFE));
    boolean jumpBoosting;
    public boolean changeY = false;
    public double minY = 0.0;
    int delay;
    public Strafe() {
        super("Strafe", Category.MOVEMENT, "");
        jumpBoosting = false;
        this.setInstance();
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
        this.changeY = false;
    }
    
    public void onTick() {
        if(delay < 12) {
            ++delay;
        }
        if(delay > 10) {
            if (switchBind.getValue().getKey() > -1) {
                if (Keyboard.isKeyDown(switchBind.getValue().getKey())) {
                    if (mode.getValue() == Mode.INSTANT) {
                        mode.setValue(Mode.STRAFE);
                        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.BOLD + " Strafe: " + ChatFormatting.AQUA + "Mode set to: " + ChatFormatting.DARK_AQUA + ChatFormatting.BOLD + "Strafe"), 1);
                        delay = 0;
                    } else if (mode.getValue() == Mode.STRAFE) {
                        mode.setValue(Mode.INSTANT);
                        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.BOLD + " Strafe: " + ChatFormatting.AQUA + "Mode set to: " + ChatFormatting.DARK_AQUA + ChatFormatting.BOLD + "Instant"), 1);
                        delay = 0;
                    }
                }
            }
        }
    }

    
    public void onUpdate() {
            if (mode.getValue() == Mode.STRAFE && EntityUtil.isMoving(mc.player) && mc.player.onGround) {
                mc.player.jump();
            }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if(mode.getValue() == Mode.STRAFE) {
            if (EntityUtil.isMoving(mc.player)) {
                final float speed = mc.player.onGround ? groundSpeed.getValue().floatValue() : (this.jumpBoosting ? jumpSpeed.getValue().floatValue() : ((event.y > 0.0) ? airSpeed.getValue().floatValue() : downSpeed.getValue().floatValue()));
                final double[] motion = EntityUtil.forward(EntityUtil.getDefaultMoveSpeed() * speed);
                event.x = motion[0];
                event.z = motion[1];
            }
            else {
                event.x = 0.0;
                event.z = 0.0;
            }
            if (this.jumpBoosting) {
                this.jumpBoosting = false;
            }
        } else if(mode.getValue() == Mode.INSTANT){
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
        }
    }

    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        jumpBoosting = true;
    }
}
