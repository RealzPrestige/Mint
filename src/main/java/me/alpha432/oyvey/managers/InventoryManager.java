package me.alpha432.oyvey.managers;

import me.alpha432.oyvey.OyVey;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryManager {
    public int currentPlayerItem;
    private int recoverySlot = -1;

    public void update() {
        if (this.recoverySlot != -1) {
            OyVey.INSTANCE.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.recoverySlot == 8 ? 7 : this.recoverySlot + 1));
            OyVey.INSTANCE.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.recoverySlot));
            OyVey.INSTANCE.mc.player.inventory.currentItem = this.recoverySlot;
            int i = OyVey.INSTANCE.mc.player.inventory.currentItem;
            if (i != this.currentPlayerItem) {
                this.currentPlayerItem = i;
                OyVey.INSTANCE.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.currentPlayerItem));
            }
            this.recoverySlot = -1;
        }
    }

    public void recoverSilent(int slot) {
        this.recoverySlot = slot;
    }
}

