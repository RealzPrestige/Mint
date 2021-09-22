package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderLivingEntityEvent;
import mint.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class PlayerChams extends Module {
    public Setting<Boolean> transparent = register(new Setting<>("Transparent", false));
    public Setting<Boolean> texture2D = register(new Setting<>("Texture2D", false));
    public Setting<Boolean> walls = register(new Setting<>("Walls", false));
    public Setting<Float> red = register(new Setting<>("Red", 0.0f, 0.0f, 255.0f));
    public Setting<Float> green = register(new Setting<>("Green", 255.0f, 0.0f, 255.0f));
    public Setting<Float> blue = register(new Setting<>("Blue", 0.0f, 0.0f, 255.0f));
    public Setting<Float> alpha = register(new Setting<>("Alpha", 0.0f, 0.0f, 255.0f));

    public PlayerChams() {
        super("Test", Category.VISUAL, "");
    }

    @SubscribeEvent
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (event.getEntityLivingBase() instanceof EntityPlayer) {
            if (transparent.getValue()) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }


            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);

            if (texture2D.getValue()) {
                glDisable(GL_TEXTURE_2D);
            }

            if (walls.getValue()) {
                glDisable(GL_DEPTH_TEST);
            }

            GL11.glColor4f(red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f, alpha.getValue() / 255f);
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

            if (walls.getValue()) {
                glEnable(GL_DEPTH_TEST);
            }

        }

        if (texture2D.getValue()) {
            glEnable(GL_TEXTURE_2D);
        }

        glPopAttrib();
        glPopMatrix();
    }
}
