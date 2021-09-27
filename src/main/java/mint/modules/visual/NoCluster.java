package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderEntityModelEvent;
import mint.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NoCluster extends Module {
    public NoCluster() {
        super("NoCluster", Category.VISUAL, "Once a player is in range they become clear.");
    }

    public Setting<Float> range = register(new Setting<>("Range", 4, 1, 6));
    public Setting<Float> alpha = register(new Setting<>("Alpha", 100, 1, 255));

    @SubscribeEvent
    public void onRender(RenderEntityModelEvent event) {
        for(Entity entity : mc.world.loadedEntityList) {
            if(shouldRender(entity)) {
                GL11.glPushMatrix();
                GL11.glColor4f(1f, 1f, 1f, alpha.value / 255f);
                GL11.glPopMatrix();
            }
        }
    }

    public boolean shouldRender(Entity entity) {
        if(!(entity instanceof EntityPlayer)) return false;
        if(entity.equals(mc.player)) return false;
        if(mc.player.getDistance(mc.player) > range.getValue()) return true;
        return false;
    }
}
