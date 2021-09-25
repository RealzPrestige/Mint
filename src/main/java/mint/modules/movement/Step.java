package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer;

public class Step extends Module {
    public Setting<Boolean> vanilla = register(new Setting<>("Vanilla",false));
    public Setting<Boolean> cancelLiquids = register(new Setting("CancelInLiquid", true)); //todo uwu rename it to something non-british
    public Setting<Integer> stepHeight = register(new Setting<>("Height",2,1,4, v -> ! vanilla.getValue()));
    double[] oneblockPositions = new double[]{0.42, 0.75};
    double[] futurePositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
    double[] twoFiveOffset = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
    double[] fourBlockPositions = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43, 1.78, 1.63, 1.51, 1.9, 2.21, 2.45, 2.43, 2.78, 2.63, 2.51, 2.9, 3.21, 3.45, 3.43};
    double[] selectedPositions = new double[0];
    int packets;

    public Step() {
        super("Step", Category.MOVEMENT,"Allows you to travel up blocks quicker without jumping.");
    }

    @Override
    public void onToggle() {
        mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        if (cancelLiquids.getValue() && (mc.player.isInWater() || mc.player.isInLava())) {
            return;
        }
        if (vanilla.getValue()) {
            mc.player.stepHeight = stepHeight.getValue();
            return;
        }
        switch (stepHeight.getValue()) {
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
        if (mc.player.onGround && mc.player.collidedVertically && mc.player.fallDistance == 0.0f && !mc.gameSettings.keyBindJump.pressed && mc.player.collidedHorizontally && !mc.player.isOnLadder() && (packets > selectedPositions.length - 2 || packets > 0 )) {
            for (double position : selectedPositions) {
                mc.player.connection.sendPacket( new CPacketPlayer.Position(mc.player.posX, mc.player.posY + position, mc.player.posZ, true) );
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + selectedPositions[selectedPositions.length - 1], mc.player.posZ);
            packets = 0;
        }
    }
}