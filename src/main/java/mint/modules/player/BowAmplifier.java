package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * https://github.com/PotatOoOoOo0/BowMcBomb/blob/main/BowMcBomb.java
 **/

public class BowAmplifier extends Module {

    public BowAmplifier() {
        super("BowAmplifier", Category.PLAYER, "9b9t: Become hitman");
    }

    public Setting<Integer> spoofs = register(new Setting<>( "Spoofs", 300, 1, 300));
    public Setting<Y> y = register ( new Setting<>( "First Y", Y.Positive));
    public enum Y {Positive, Negative}

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if(!isEnabled())
            return;

        if (event.getStage() != 0) {
            return;
        }

        if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging p = event.getPacket();
            if (p.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && InventoryUtil.heldItem(Items.BOW, InventoryUtil.Hand.Both)) {

                mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                for (int s = 0; s < spoofs.getValue(); ++s) {
                    if (y.getValue() == Y.Positive) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                    }
                    if (y.getValue() == Y.Negative) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                    }
                }
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
        }
    }
}