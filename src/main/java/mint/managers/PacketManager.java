package mint.managers;

import mint.Mint;
import mint.modules.Feature;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketManager
        extends Feature {
    private final List<Packet<?>> noEventPackets = new ArrayList();

    public void sendPacketNoEvent(Packet<?> packet) {
        if (packet != null && !PacketManager.nullCheck()) {
            this.noEventPackets.add(packet);
            Mint.INSTANCE.mc.player.connection.sendPacket(packet);
        }
    }

    public boolean shouldSendPacket(Packet<?> packet) {
        if (this.noEventPackets.contains(packet)) {
            this.noEventPackets.remove(packet);
            return false;
        }
        return true;
    }
}

