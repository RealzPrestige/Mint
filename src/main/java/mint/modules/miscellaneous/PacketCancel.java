package mint.modules.miscellaneous;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketCancel extends Module {

    public PacketCancel() {
        super("PacketCancel", Module.Category.MISCELLANEOUS, "Cancels chosen packets.");
    }

    public Setting<Boolean> clientParent = register(new Setting("Client", true, false));
    public Setting<Boolean> cpPlayer = register(new Setting("Player", false, v -> clientParent.getValue()));
    public Setting<Boolean> cpPlayerTryUseItemOnBlock = register(new Setting("TryUseItemOnBlock", false, v -> clientParent.getValue()));
    public Setting<Boolean> cpPlayerTryUseItem = register(new Setting("TryUseItem", false, v -> clientParent.getValue()));
    public Setting<Boolean> cpAnimation = register(new Setting("Animation", false, v -> clientParent.getValue()));
    //public Setting<Boolean> cp = register(new Setting(" ", false, v -> clientParent.getValue()));
    //public Setting<Boolean> cp = register(new Setting(" ", false, v -> clientParent.getValue()));
    //public Setting<Boolean> cp = register(new Setting(" ", false, v -> clientParent.getValue()));
    //public Setting<Boolean> cp = register(new Setting(" ", false, v -> clientParent.getValue()));
    //public Setting<Boolean> cpP = register(new Setting(" ", false, v -> clientParent.getValue()));



    public Setting<Boolean> serverParent = register(new Setting("Server", true, false));
    public Setting<Boolean> spChunkData = register(new Setting("ChunkData", false, v -> serverParent.getValue()));


    //todo add all server packets, add settings for client packets, add more parent settings(Client>Entity, Player, etc. Server>ChunkData, etc
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (!isEnabled()) {
            return;
        }
        //if (event.getStage() == EventStageable.EventStage.PRE) {
        if (e.getPacket() instanceof SPacketChunkData && spChunkData.getValue()) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (!isEnabled()) {
            return;
        }
        if (e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && cpPlayerTryUseItemOnBlock.getValue()) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketPlayerTryUseItem && cpPlayerTryUseItem.getValue()) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketPlayer && cpPlayer.getValue()) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketAnimation && cpAnimation.getValue()) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketChatMessage) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketClickWindow) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketClientSettings) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketClientStatus) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketCloseWindow) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketConfirmTeleport) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketConfirmTransaction) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketCreativeInventoryAction) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketCustomPayload) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketEnchantItem) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketEntityAction) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketHeldItemChange) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketInput) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketKeepAlive) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketPlaceRecipe) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketPlayerAbilities) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketPlayerDigging) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketRecipeInfo) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketResourcePackStatus) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketSeenAdvancements) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketSpectate) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketSteerBoat) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketTabComplete) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketUpdateSign) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketUseEntity) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketVehicleMove) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketEncryptionResponse) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketLoginStart) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketPing) {
            e.setCanceled(true);
        }

        if (e.getPacket() instanceof CPacketServerQuery) {
            e.setCanceled(true);
        }
    }
}