package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderLivingEntityEvent;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NoCluster extends Module {
    public NoCluster() {
        super("No Cluster", Category.VISUAL, "Lowers players opacity when they are nearby.");
    }

    public Setting<Float> range = register(new Setting<>("Range", 4.0f, 1.0f, 6.0f));
    public Setting<Float> alpha = register(new Setting<>("Alpha", 100.0f, 1.0f, 255.0f));

    @SubscribeEvent
    public void onRenderLivingEntityEvent(RenderLivingEntityEvent event) {
        if(NullUtil.fullNullCheck() || !isEnabled())
            return;

        for(Entity entity : mc.world.loadedEntityList) {
            if(shouldRender(entity)) {
                GL11.glPushMatrix();
                GL11.glColor4f(1f, 1f, 1f, alpha.getValue() / 255f);
                event.getModelBase().render(entity, event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
                GL11.glColor4f(1f, 1f, 1f, 1f);
                GL11.glPopMatrix();
            }
        }
    }

    public boolean shouldRender(Entity entity) {
        if(!(entity instanceof EntityPlayer)) return false;
        if(entity.equals(mc.player)) return false;
        return mc.player.getDistance(mc.player) > range.getValue();
    }
}
