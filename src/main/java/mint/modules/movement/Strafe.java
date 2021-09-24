package mint.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.events.MoveEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.MathUtil;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Strafe extends Module {
    private static Strafe INSTANCE = new Strafe();
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.STRAFE));
    public Setting<Bind> switchBind = register(new Setting<>("SwitchBind", new Bind(-1)));
    public enum Mode{STRAFE, INSTANT}
    public boolean changeY = false;
    public double minY = 0.0;
    int ticks;
    int delay;
    private static float speed;
    private double speed1;
    private int stage;
    private boolean disabling;
    private boolean stopMotionUntilNext;
    private double moveSpeed;
    private boolean spedUp;
    public static boolean canStep;
    private double lastDist;
    public static double yOffset;
    private boolean cancel;

    public Strafe() {
        super("Strafe", Category.MOVEMENT, "Tweaks and speeds up movement.");
        setInstance();
        speed = 0.08f;
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
    
    public void onTick() {
        if(ticks < 12) {
            ++ticks;
        }
        if(ticks > 10) {
            if (switchBind.getValue().getKey() > -1) {
                if (Keyboard.isKeyDown(switchBind.getValue().getKey())) {
                    if (mode.getValue() == Mode.INSTANT) {
                        mode.setValue(Mode.STRAFE);
                        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.BOLD + " Strafe: " + ChatFormatting.AQUA + "Mode set to: " + ChatFormatting.DARK_AQUA + ChatFormatting.BOLD + "Strafe"), 1);
                        ticks = 0;
                    } else if (mode.getValue() == Mode.STRAFE) {
                        mode.setValue(Mode.INSTANT);
                        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.BOLD + " Strafe: " + ChatFormatting.AQUA + "Mode set to: " + ChatFormatting.DARK_AQUA + ChatFormatting.BOLD + "Instant"), 1);
                        ticks = 0;
                    }
                }
            }
        }
    }

    
    public void onUpdate() {
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if(mode.getValue() == Mode.STRAFE){
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
}
