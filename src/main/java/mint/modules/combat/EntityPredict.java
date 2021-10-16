package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityPredict extends Module {
    public Setting<Integer> attackAmount = register(new Setting("Attack Amount", 1, 1, 5));
    public Setting<Boolean> holdingCrystalOnly = register(new Setting("Holding Crystal Only", false));

    public EntityPredict() {
        super("Entity Predict", Category.COMBAT, "Predict crystal ids and attack m");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!isEnabled())
            return;

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            int entityId = 0;
            CPacketPlayerTryUseItemOnBlock packet = event.getPacket();
            if (!mc.world.getBlockState(packet.position).getBlock().equals(Blocks.OBSIDIAN))
                return;

            if (holdingCrystalOnly.getValue() && !(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) || mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)))
                return;

            for (Entity entity : mc.world.loadedEntityList)
                if (entity.entityId > entityId)
                    entityId = entity.entityId;

            switch (attackAmount.getValue()) {
                case 1:
                    attackEntity(entityId + 1);
                    break;
                case 2:
                    attackEntity(entityId + 1);
                    attackEntity(entityId + 2);
                    break;
                case 3:
                    attackEntity(entityId + 1);
                    attackEntity(entityId + 2);
                    attackEntity(entityId + 3);
                    break;
                case 4:
                    attackEntity(entityId + 1);
                    attackEntity(entityId + 2);
                    attackEntity(entityId + 3);
                    attackEntity(entityId + 4);
                    break;
                case 5:
                    attackEntity(entityId + 1);
                    attackEntity(entityId + 2);
                    attackEntity(entityId + 3);
                    attackEntity(entityId + 4);
                    attackEntity(entityId + 5);
                    break;

            }
        }
    }

    void attackEntity(int entityId) {
        CPacketUseEntity packet = new CPacketUseEntity();
        packet.entityId = entityId;
        packet.action = CPacketUseEntity.Action.ATTACK;
        mc.player.connection.sendPacket(packet);
    }
}
