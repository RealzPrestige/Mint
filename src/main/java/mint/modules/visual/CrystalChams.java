package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderCrystalEvent;
import mint.events.RenderEntityModelEvent;
import mint.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

public class CrystalChams extends Module {
    public Setting<Boolean> transparent = register(new Setting<>("Transparant", false));
    public Setting<Boolean> texture2D = register(new Setting<>("Texture 2D", false));
    public Setting<Boolean> blend = register(new Setting<>("Blend", false));
    public Setting<Boolean> depthMask = register(new Setting<>("Depth Mask", false));
    public Setting<Boolean> walls = register(new Setting<>("Walls", false));
    public Setting<RenderMode> renderMode = register(new Setting<>("Render Mode", RenderMode.FILL));
    public enum RenderMode {FILL, WIRE, BOTH}
    public Setting<Boolean> glint = register(new Setting<>("Galaxy Texture", false));
    public Setting<Float> lineWidth = register(new Setting<>("Line Width", 1.0f, 0.1f, 3.0f));
    public Setting<Double> scale = register(new Setting<>("Scale", 1.0, 0.0, 2.0));
    public Setting<Float> red = register(new Setting<>("Red", 0.0f, 0.0f, 255.0f));
    public Setting<Float> green = register(new Setting<>("Green", 255.0f, 0.0f, 255.0f));
    public Setting<Float> blue = register(new Setting<>("Blue", 0.0f, 0.0f, 255.0f));
    public Setting<Float> alpha = register(new Setting<>("Alpha", 0.0f, 0.0f, 255.0f));

    static final ResourceLocation RES_ITEM_GLINT;

    static {
        RES_ITEM_GLINT = new ResourceLocation("textures/galaxy.png");
    }

    public CrystalChams() {
        super("CrystalChams", Category.VISUAL, "");
    }

    @SubscribeEvent
    public void onRendereEntityModel(RenderEntityModelEvent event) {
        if(event.entity instanceof EntityEnderCrystal) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderCrystalPost(RenderCrystalEvent.RenderCrystalPostEvent event) {
        if (transparent.getValue()) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        glPushMatrix();
        glPushAttrib(GL_ALL_ATTRIB_BITS);

        float rotation = event.getEntityEnderCrystal().innerRotation + event.getPartialTicks();
        float rotationMoved = MathHelper.sin(rotation * 0.2F) / 2 + 0.5F;
        rotationMoved += Math.pow(rotationMoved, 2);

        glTranslated(event.getX(), event.getY(), event.getZ());
        glScaled(scale.getValue(), scale.getValue(), scale.getValue());
        if (blend.getValue()) {
            glEnable(GL_BLEND);
        }
        if (depthMask.getValue()) {
            glDepthMask(false);
        }
        if (texture2D.getValue()) {
            glEnable(GL_TEXTURE_2D);
        }

        if (walls.getValue()) {
            glDisable(GL_DEPTH_TEST);
        }

        if (glint.getValue()) {
            mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
            GL11.glTexCoord3d(1.0, 1.0, 1.0);
            GL11.glEnable(3553);
            GL11.glBlendFunc(768, 771);
            GL11.glBlendFunc(770, 32772);
        }

        switch (renderMode.getValue()){
            case FILL: {
                glPolygonMode(GL_FRONT_AND_BACK, GL_POLYGON_MODE);
                break;
            }
            case WIRE: {
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                break;
            }
            case BOTH: {
                glPolygonMode(GL_FRONT_AND_BACK, GL_POLYGON_MODE);
                event.getModelNoBase().render(event.getEntityEnderCrystal(), 0, rotation * 3, rotationMoved * 0.2F, 0, 0, 0.0625F);
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                event.getModelNoBase().render(event.getEntityEnderCrystal(), 0, rotation * 3, rotationMoved * 0.2F, 0, 0, 0.0625F);

                break;
            }
        }

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth((float) ((double) lineWidth.getValue()));

        GL11.glColor4f(red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f, alpha.getValue() / 255f);

        event.getModelNoBase().render(event.getEntityEnderCrystal(), 0, rotation * 3, rotationMoved * 0.2F, 0, 0, 0.0625F);

        if (walls.getValue()) {
            glEnable(GL_DEPTH_TEST);
        }
        if (texture2D.getValue()) {
            glDisable(GL_TEXTURE_2D);
        }
        if (depthMask.getValue()) {
            glDepthMask(true);
        }
        if (blend.getValue()) {
            glDisable(GL_BLEND);
        }
        glPopAttrib();
        glPopMatrix();
    }
}
