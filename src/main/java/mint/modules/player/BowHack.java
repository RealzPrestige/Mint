package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 arrow damage depends on the velocity of a player,
 so if u were to tp 1k blocks away right b4 shooting the arrow the dmg would get supa big
 todo setVelocity mode
 */
public class BowHack extends Module {
    public BowHack() {
        super("BowHack", Category.PLAYER, "An exploit to insnatly kill your opponents.");
    }

    public Setting<Integer> power = register(new Setting("Power", 3, 1, 30));
    public Setting<Integer> yOffset = register(new Setting("Y Offset", 5, 1, 10));
    public Setting<Boolean> debug = register(new Setting("Debug", true));
    float yaw = mc.player.rotationYaw;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItem && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(
                    mc.player.posX - Math.sin(yaw) * power.getValue(),
                    mc.player.posY + yOffset.getValue(),
                    mc.player.posZ + Math.cos(yaw) * power.getValue(),
                    mc.player.rotationYaw, mc.player.rotationPitch, false));
            if (debug.getValue()) {
                MessageManager.sendMessage("X: " + (mc.player.posX - Math.sin(yaw) * power.getValue()));
                MessageManager.sendMessage("Y: " + (mc.player.posY + yOffset.getValue()));
                MessageManager.sendMessage("Z: " + (mc.player.posZ + Math.cos(yaw) * power.getValue()));
                MessageManager.sendMessage("Yaw: " + yaw);
            }
        }
    }
}