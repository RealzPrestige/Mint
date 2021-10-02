package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class Phase extends Module {

    public Phase() {
        super("Phase", Module.Category.MOVEMENT, "Lets you phase through blocks.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Clip));
    public enum Mode {Clip, Test}
    public Setting<Boolean> packet = register(new Setting("Packet", true));
    public Setting<Integer> upFactor = register(new Setting("X Speed", 1, 1, 60, v -> mode.getValue() == Mode.Test));
    public Setting<Integer> downFactor = register(new Setting("ClipFactor", 1, 1, 6)); //v -> mode.getValue() == Mode.Clip
    public Setting<Boolean> spoofonGround = register(new Setting("Spoof onGround", true));
    public Setting<Boolean> onGround = register(new Setting("onGround", false));

    @Override
    public void onToggle() {
        mc.player.noClip = false;

    }
    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        Vec3d direction = new Vec3d(Math.cos((
                mc.player.rotationYaw + 90.0f) * Math.PI / 180.0f),
                0.0,
                Math.sin((mc.player.rotationYaw + 90f) * Math.PI / 180.0f));

        mc.player.noClip = true;
        mc.player.setVelocity(0.0, 0.0, 0.0);
        if (onGround.getValue()) {
            mc.player.onGround = false;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            if (packet.getValue()) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.05 * downFactor.getValue(), mc.player.posZ, onGround.getValue()));
            } else {
                mc.player.setPosition(mc.player.posX, mc.player.posY + 0.05 * downFactor.getValue(), mc.player.posZ);
            }
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            if (packet.getValue()) {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.05 * downFactor.getValue(), mc.player.posZ, onGround.getValue()));
            } else {
                mc.player.setPosition(mc.player.posX, mc.player.posY - 0.05 * downFactor.getValue(), mc.player.posZ);
            }
        }

        switch (mode.getValue()) {

            case Clip:
                if (packet.getValue()) {
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX + 0.05, mc.player.posY, mc.player.posZ + 0.05, onGround.getValue()));
                } else {
                    mc.player.setPosition(mc.player.posX + 0.05, mc.player.posY, mc.player.posZ + 0.05);
                }
            break;

            case Test:
                if (packet.getValue()) {
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX + direction.x * upFactor.getValue(), mc.player.posY, mc.player.posZ + direction.z * upFactor.getValue(), onGround.getValue()));
                } else {
                    mc.player.setPosition(mc.player.posX + direction.x * upFactor.getValue(), mc.player.posY, mc.player.posZ + direction.z * upFactor.getValue());
                }
        }
    }
}