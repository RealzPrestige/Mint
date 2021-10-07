package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * so exploit whne public
 * i add it - kambing
 */
public class BowHack extends Module {
    public BowHack() {
        super("BowHack", Category.PLAYER, "Hack ur bows than instakill ur mom");

    }
    float yaw = mc.player.rotationYaw;
    Setting<Integer> power = register(new Setting<>("Power", 3, 1, 30));

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItem && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation( mc.player.posX - Math.sin(yaw) * power.getValue(), mc.player.posY + 5, mc.player.posZ + Math.cos(yaw) * power.getValue(), mc.player.rotationYaw, mc.player.rotationPitch, false));
        }
    }
}
