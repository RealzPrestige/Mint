package mint.modules.visual;

import mint.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {
    static NoRender INSTANCE = new NoRender();

    public Setting<Boolean> fire = register(new Setting<>("Fire Overlay", false));
    public Setting<Boolean> hurtCam = register(new Setting<>("Hurt Camera Effect", false));
    public Setting<Boolean> insideBlocks = register(new Setting<>("Inside Blocks Overlay", false));
    public Setting<Boolean> explosions = register(new Setting<>("Explosions Effect", false));
    public Setting<Boolean> armorRemover = register(new Setting<>("Armor Remover", false));

    public NoRender() {
        super("No Render", Category.VISUAL, "Renders No");
        setInstance();
    }

    public void onLogin() {
        if (!isEnabled())
            return;

        disable();
        enable();
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;

        if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.FIRE))
            event.setCanceled(fire.getValue());

        if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.BLOCK))
            event.setCanceled(insideBlocks.getValue());
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;

        if (event.getPacket() instanceof SPacketExplosion)
            event.setCanceled(explosions.getValue());
    }

    public static NoRender getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NoRender();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
