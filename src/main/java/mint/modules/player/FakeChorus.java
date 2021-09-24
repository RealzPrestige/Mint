package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.Queue;

public class FakeChorus extends Module {

    //kami5
    private final Setting<Boolean> cancel = register(new Setting<>("Cancel", true));
    public FakeChorus() {
        super("ChorusSpoof", Module.Category.PLAYER, "Spoofs your chorus.");
    }
    Queue<CPacketPlayer> packets = new LinkedList<CPacketPlayer>();
    Queue<CPacketConfirmTeleport> tpPackets = new LinkedList<CPacketConfirmTeleport>();

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && this.cancel.getValue().booleanValue()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            this.packets.add((CPacketPlayer)event.getPacket());
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport) {
            this.tpPackets.add((CPacketConfirmTeleport)event.getPacket());
            event.setCanceled(true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        while (!this.packets.isEmpty()) {
            mc.getConnection().sendPacket((Packet)this.packets.poll());
        }
        while (!this.tpPackets.isEmpty()) {
            mc.getConnection().sendPacket((Packet)this.tpPackets.poll());
        }
    }
}
