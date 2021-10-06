package mint.modules.miscellaneous;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketManipulator extends Module {

    public PacketManipulator() {
        super("PacketManipulator", Module.Category.MISCELLANEOUS, "Manipulates packets.");
    }

    /*
    a test thingy dont mind it for now
     */

    public Setting<Boolean> cancel = register(new Setting("Cancel", true, false));
    public Setting<Boolean> s = register(new Setting("Server", true, false, v -> cancel.getValue()));

    public Setting<Boolean> c = register(new Setting("Client", true, false, v -> cancel.getValue()));
    public Setting<Boolean> cplayer = register(new Setting("Player", true));

    public Setting<Boolean> send = register(new Setting("Send", true, false));
    public Setting<Integer> packet1 = register(new Setting("Packet1", 1, 1, 10, v -> send.getValue()));

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (isEnabled()) {
            if (e.getPacket() instanceof CPacketPlayer && cplayer.getValue()) {
                e.setCanceled(true);
            }
        } else {
            return;
        }
    }
}