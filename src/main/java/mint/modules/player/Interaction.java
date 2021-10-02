package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Interaction extends Module {


    public Interaction(){
        super("Interaction", Module.Category.PLAYER, "Tweaks player's interactions.");
    }

    //todo add shit like echest canceller(when trying to open an echest it just doesnt open it)
    public Setting<Boolean> placementParent = register(new Setting("Placement", true, false));
    public Setting<Boolean> boatPlace = register(new Setting("Boat", true, v -> placementParent.getValue()));
    public Setting<Boolean> blockPlace = register(new Setting("Illegal Blocks", false, v -> placementParent.getValue()));
    boolean cancelled;

    @Override
    public void onToggle() {
        cancelled = false;
    }

    @Override
    public void onUpdate() {
        if (cancelled) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(mc.objectMouseOver.getBlockPos(), EnumFacing.SOUTH, EnumHand.MAIN_HAND, 1, 1, 1));
            cancelled = false;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (!isEnabled()) {
            return;
        }
        //tryuseitemonblock packet + held item check(boat) + boatPlace setting check
        if (e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItemMainhand().getItem() instanceof ItemBoat && boatPlace.getValue()) {
            e.setCanceled(true);
        }

        //tryuseitem packet + held item check(block) + blockPlace setting check
        if (e.getPacket() instanceof CPacketPlayerTryUseItem && mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && blockPlace.getValue()) {
            e.setCanceled(true);
            cancelled = true;
        }
    }
}