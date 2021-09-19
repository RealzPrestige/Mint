package mint.modules.movement;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

public class ReverseStep extends Module {

    public ReverseStep() {
        super("ReverseStep", Module.Category.MOVEMENT, "Lets you to fall faster.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Vanilla));
    public Setting<Boolean> sneak = register(new Setting("NoDesync", true));
    public Setting<Float> vanillaSpeed = register(new Setting("VanillaSpeed", 9.0f, 0.1f, 9.0f, v -> mode.getValue() == Mode.Vanilla));
    public Setting<Float> strictSpeed = register(new Setting("StrictSpeed", 1.75f, 0.01f, 2.00f));

    @Override
    public void onUpdate() {
        if (fullNullCheck() || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown() || mc.player.isDead || Mint.moduleManager.isModuleEnabled("SelfFill")) {
            return;
        }

        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
            switch (mode.getValue()) {

                case Vanilla:
                    for (float y = 0.0f; y < 3.0f; y += 0.01f) {
                        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {

                            if (sneak.getValue()) {
                                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                            }

                            mc.player.motionY = -vanillaSpeed.getValue();

                            if (sneak.getValue()) {
                                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                            }
                        }
                    }
                    break;

                case Strict:
                    for (float y = 0.0f; y < 3.0f; y += 0.01f) {
                        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {

                            if (sneak.getValue()) {
                                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                            }

                            mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
                            mc.player.motionY *= strictSpeed.getValue();
                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));

                            if (sneak.getValue()) {
                                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                            }
                        }
                    }
                    break;

            }
        }
    }
    public enum Mode {
        Vanilla,
        Strict
    }
}