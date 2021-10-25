package mint.modules.player;

import com.mojang.authlib.GameProfile;
import mint.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blink extends Module {
    public Setting<Boolean> renderPlayer = register(new Setting("Render Player", false));
    public Setting<Mode> mode = register(new Setting("Mode", Mode.FULL));

    enum Mode {FULL, RECEIVE, SEND}

    EntityOtherPlayerMP fake_player;

    public Blink() {
        super("Blink", Category.PLAYER, "Lets you teleport yes.");
    }

    public void onEnable() {
        if (NullUtil.fullNullCheck())
            return;
        if (renderPlayer.getValue()) {
            fake_player = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), mc.session.getUsername()));
            fake_player.copyLocationAndAnglesFrom(mc.player);
            fake_player.rotationYawHead = mc.player.rotationYawHead;
            fake_player.inventory = mc.player.inventory;
            fake_player.setHealth(36);
            mc.world.addEntityToWorld(-100, fake_player);
        }
    }

    public void onDisable() {
        if (renderPlayer.getValue()) {
            try {
                mc.world.removeEntity(fake_player);
            } catch (Exception ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!isEnabled())
            return;
        event.setCanceled(mode.getValue().equals(Mode.SEND));
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!isEnabled())
            return;
        event.setCanceled(mode.getValue().equals(Mode.RECEIVE));
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!isEnabled())
            return;
        event.setCanceled(mode.getValue().equals(Mode.FULL));
    }
}
