package mint.modules.movement;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class Step extends Module {

    public Step() {
        super("Step", Module.Category.MOVEMENT, "Allows you to step up blocks.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Vanilla));
    public Setting<Boolean> pauseParent = register(new Setting("Pause", true, false));
    public Setting<Boolean> pauseInLiquid = register(new Setting("InLiquid", true, v -> pauseParent.getValue()));
    public Setting<Integer> height = register(new Setting("Height", 2, 0, 2, v -> mode.getValue() == Mode.Vanilla));

    @Override
    public void onEnable() {
        Mint.INSTANCE.mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onDisable() {
        Mint.INSTANCE.mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        if ((mc.player.isInWater() || mc.player.isInLava()) && pauseInLiquid.getValue()) {
            return;
        }

        switch (mode.getValue()) {
            case Vanilla:
                Mint.INSTANCE.mc.player.stepHeight = height.getValue();
                break;

                case NCP:

                case Packet:

        }
    }

    public enum Mode {
        Vanilla,
        Packet,
        NCP
    }
}