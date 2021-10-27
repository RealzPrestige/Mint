package mint.modules.movement;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.NullUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

@ModuleInfo(name = "Phase", category = Module.Category.Movement, description = "Lets you phase through blocks.")
public class Phase extends Module {
    static Phase INSTANCE = new Phase();

    public Phase() {
        this.setInstance();
    }

    public EnumSetting mode = new EnumSetting("Mode", Mode.Clip, this);

    public enum Mode {Clip, Test}

    public BooleanSetting packet = new BooleanSetting("Packet Move", true, this);
    public IntegerSetting speed = new IntegerSetting("Speed", 1, 1, 10, this, v -> mode.getValue() == Mode.Test);

    public BooleanSetting spoofonGround = new BooleanSetting("Spoof onGround", true, this);
    public BooleanSetting offground = new BooleanSetting("Off ground", false, this);

    public static Phase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Phase();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onToggle() {
        mc.player.noClip = false;
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        Vec3d direction = new Vec3d(Math.cos((mc.player.rotationYaw + 90.0f) * 3.141592653589793 / 180.0f), 0.0, Math.sin((mc.player.rotationYaw + 90f) * 3.141592653589793 / 180.0f));

        mc.player.noClip = true;
        mc.player.setVelocity(0.0, 0.0, 0.0);

        //since we dont want to step up when phasing in a block, we do this
        mc.player.collidedHorizontally = false;
        mc.player.collidedVertically = false;

        if (spoofonGround.getValue()) {
            mc.player.onGround = false;
        }

        if (mode.getValue().equals(Mode.Clip)) {
            if (packet.getValue()) {

                //horizontal
                if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX + 0.0624 * speed.getValue(), mc.player.posY, mc.player.posZ + 0.0624 * speed.getValue(), offground.getValue()));
                }

                //down
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.0624 * speed.getValue(), mc.player.posZ, offground.getValue()));
                }

                //up
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0624 * speed.getValue(), mc.player.posZ, offground.getValue()));
                }

            } else { //if packet isnt enabled, then do this

                //horizontal
                if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                    mc.player.setPosition(mc.player.posX + 0.0624 * speed.getValue(), mc.player.posY, mc.player.posZ + 0.0624 * speed.getValue());
                }

                //down
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY - 0.0624 * speed.getValue(), mc.player.posZ);
                }

                //up
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 0.0624 * speed.getValue(), mc.player.posZ);
                }
            }
        } else {
            if (packet.getValue()) {
                //horizontal
                if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX + direction.x * speed.getValue(), mc.player.posY, mc.player.posZ + direction.z * speed.getValue(), offground.getValue()));
                }

                //down
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - direction.y * speed.getValue(), mc.player.posZ, offground.getValue()));
                }

                //up
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + direction.y * speed.getValue(), mc.player.posZ, offground.getValue()));
                }

            } else { //if packet isnt enabled, then do this

                // horizontal
                if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                    mc.player.setPosition(mc.player.posX + direction.x * speed.getValue(), mc.player.posY, mc.player.posZ + direction.z * speed.getValue());
                }

                //down
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY - direction.y * speed.getValue(), mc.player.posZ);
                }

                //up
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY + direction.y * speed.getValue(), mc.player.posZ);
                }
            }
        }
    }
}