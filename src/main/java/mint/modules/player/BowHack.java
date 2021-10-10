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
 */
public class BowHack extends Module {
    public BowHack() {
        super("BowHack", Category.PLAYER, "An exploit to insnatly kill your opponents.");
    }

    //which dumb nigger deleted my mode nigguh u dum af
    public Setting<Mode> mode = register(new Setting("Mode", Mode.Calc));
    public enum Mode {Calc, Spoof}
    public Setting<Type> type = register(new Setting("Type", Type.X));
    public enum Type {X, Y, Z}

    public Setting<Integer> power = register(new Setting("Power", 3, 1, 30, v -> mode.getValue() == Mode.Calc));
    public Setting<Integer> yOffset = register(new Setting("Y Offset", 5, 1, 10, v -> mode.getValue() == Mode.Calc));
    public Setting<Boolean> debug = register(new Setting("Debug", true));
    float yaw;
    double x;
    double y;
    double z;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItem && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
            yaw = mc.player.rotationYaw;
            x = Math.sin(yaw) * power.getValue();
            y = yOffset.getValue();
            z = Math.cos(yaw) * power.getValue();

            if (mode.getValue() == Mode.Calc) {
                mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(
                        mc.player.posX - x,
                        mc.player.posY + y,
                        mc.player.posZ + z,
                        mc.player.rotationYaw,
                        mc.player.rotationPitch,
                        false));
            }
            if (mode.getValue() == Mode.Spoof) {
                mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(
                        mc.player.posX + (type.getValue() == Type.X ? 1000 : 0),
                        mc.player.posY + (type.getValue() == Type.Y ? 1000 : 0),
                        mc.player.posZ + (type.getValue() == Type.Z ? 1000 : 0),
                        mc.player.rotationYaw,
                        mc.player.rotationPitch,
                        false));
            }
            if (debug.getValue()) {
                MessageManager.sendMessage("X: " + x);
                MessageManager.sendMessage("Y: " + y);
                MessageManager.sendMessage("Z: " + z);
                MessageManager.sendMessage("Yaw: " + yaw);
            }
        }
    }
}