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
 its patched on practice servers forsho
 */
public class BowHack extends Module {
    public BowHack() {
        super("BowHack", Category.PLAYER, "An exploit to insnatly kill your opponents.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Calc));
    public enum Mode {Calc, Simple}

    public Setting<Integer> factor = register(new Setting("Factor", 3, 1, 30));
    public Setting<Float> yOffset = register(new Setting("Y Offset", 5.0f, 0.0f, 10.0f));
    public Setting<Boolean> debug = register(new Setting("Debug", true));
    float yaw = mc.player.rotationYaw;
    double x;
    double y;
    double z;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItem && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
            switch (mode.getValue()) {
                case Calc:
                    x = mc.player.posX - Math.sin(yaw) * factor.getValue();
                    y = mc.player.posY + yOffset.getValue();
                    z = mc.player.posZ + Math.cos(yaw) * factor.getValue();
                    mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(x, y, z, mc.player.rotationYaw, mc.player.rotationPitch, false));
                    break;

                case Simple:
                    x = mc.player.posX + factor.getValue();
                    y = mc.player.posY + yOffset.getValue();
                    z = mc.player.posZ + factor.getValue();
                    mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(x, y, z, mc.player.rotationYaw, mc.player.rotationPitch, false));
                    break;
            }

            if (debug.getValue()) {
                MessageManager.sendMessage("X: " + x);
                MessageManager.sendMessage("Y: " + y);
                MessageManager.sendMessage("Z: " + z);
                MessageManager.sendMessage("Yaw: " + (mode.getValue() == Mode.Calc ? yaw : "null"));
            }
        }
    }
}