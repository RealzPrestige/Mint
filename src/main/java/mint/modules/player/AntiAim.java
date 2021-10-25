package mint.modules.player;

import mint.Mint;
import mint.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiAim extends Module {

    public AntiAim() {
        super("Anti Aim", Module.Category.PLAYER, "Spoofs your yaw and pitch.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Custom));
    public Setting<Integer> yaw = register(new Setting("Yaw", 90, -90, 90, v -> mode.getValue() == Mode.Custom));
    public Setting<Integer> pitch = register(new Setting("Pitch", 90, -90, 90, v -> mode.getValue() == Mode.Custom));
    public Setting<Integer> spinSpeed = register(new Setting("SpinSpeed", 10, 0, 50, v -> mode.getValue() == Mode.Spin));
    int nextValue;

    @Override
    public void onUpdate() {
        nextValue += spinSpeed.getValue();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (!isEnabled()) {
            return;
        }
        if (e.getPacket() instanceof CPacketPlayer && !Mint.INSTANCE.mc.player.isHandActive()) {
            switch (mode.getValue()) {
                case Custom:
                    ((CPacketPlayer)e.getPacket()).yaw = yaw.getValue();
                    ((CPacketPlayer)e.getPacket()).pitch = pitch.getValue();
                    break;

                case Spin:
                    ((CPacketPlayer)e.getPacket()).yaw = nextValue;
                    ((CPacketPlayer)e.getPacket()).pitch = nextValue;
                    break;
            }
        }
    }

    public enum Mode {
        Custom,
        Spin
    }
}