package mint.modules.combat;

import mint.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Crits extends Module {

    public Crits() {
        super("Crits", Module.Category.Combat, "Scores criticals for you.");
    }

    public Setting<Boolean> targetParent = register(new Setting("Target", true, false));
    public Setting<Boolean> crystal = register(new Setting("End Crystals", false, v -> targetParent.getValue()));

    public Setting<Boolean> pauseInLiquids = register(new Setting("Pause In Liquids", true));
    public Setting<Boolean> confirmPos = register(new Setting("Confirm Position", true));

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity)e.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
            if (!(e.getPacket() instanceof EntityEnderCrystal && crystal.getValue()) || ((mc.player.isInLava() || mc.player.isInWater()) && pauseInLiquids.getValue()) && !mc.player.onGround) {
                return;
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.10000000149011612, mc.player.posZ, false));
            if (confirmPos.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
    }
}