package mint.modules.movement;

import mint.setting.Setting;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class Phase extends Module {
    private static final Phase INSTANCE = new Phase();
    public Phase() {
        super("Phase", Module.Category.Movement, "Lets you phase through blocks.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Clip));
    public enum Mode {Clip, Test}

    public Setting<Boolean> packet = register(new Setting("Packet Move", true));
    public Setting<Integer> speed = register(new Setting("Speed", 1, 1, 10, v -> mode.getValue() == Mode.Test));

    public Setting<Boolean> spoofonGround = register(new Setting("Spoof onGround", true));
    public Setting<Boolean> offground = register(new Setting("Off ground", false));

    public static Phase getInstance(){
        return INSTANCE;
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

        switch (mode.getValue()) {
            case Clip:

                if (packet.getValue()) {

                    //horizontal
                    if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX + 0.0624 * speed.getValue(), mc.player.posY, mc.player.posZ + 0.0624 * speed.getValue(), offground.getValue()));
                    }

                    //down
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.0624 * speed.getValue(), mc.player.posZ, offground.getValue()));
                    }

                    //up
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0624 * speed.getValue(), mc.player.posZ, offground.getValue()));
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
                break;

            case Test:
                if (packet.getValue()) {
                    //horizontal
                    if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX + direction.x * speed.getValue(), mc.player.posY, mc.player.posZ + direction.z * speed.getValue(), offground.getValue()));
                    }

                    //down
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - direction.y * speed.getValue(), mc.player.posZ, offground.getValue()));
                    }

                    //up
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + direction.y * speed.getValue(), mc.player.posZ, offground.getValue()));
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
                break;
        }
    }
}