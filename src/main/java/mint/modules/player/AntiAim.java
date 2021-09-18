package mint.modules.player;

import mint.Mint;
import mint.clickgui.setting.Setting;
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
    public Setting<Integer> spinSpeed = register(new Setting("SpinSpeed", 10, 0, 50));
    int nextValue;

    @Override
    public void onUpdate() {
        nextValue += spinSpeed.getValue();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && Mint.INSTANCE.mc.player.isHandActive()) {
            switch (mode.getValue()) {
                case Custom:
                ((CPacketPlayer) event.getPacket()).yaw = yaw.getValue();
                ((CPacketPlayer) event.getPacket()).pitch = pitch.getValue();
                break;

                case Spin:
                    ((CPacketPlayer) event.getPacket()).yaw = nextValue;
                    ((CPacketPlayer) event.getPacket()).pitch = nextValue;
                    break;
            }
        }
    }

    public String hudInfoString(){
        return "Y" +yaw.getValue() + " P" + pitch.getValue();
    }

    public enum Mode {
        Custom,
        Spin
    }
}
