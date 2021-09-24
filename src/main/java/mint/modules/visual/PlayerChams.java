package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderLivingEntityEvent;
import mint.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class PlayerChams extends Module {
    public Setting<Boolean> transparent = register(new Setting<>("Transparent", false));
    public Setting<Boolean> texture2D = register(new Setting<>("Texture2D", false));
    public Setting<Boolean> walls = register(new Setting<>("Walls", false));
    public Setting<Boolean> glint = register(new Setting<>("Glint", false));
    public Setting<Boolean> customBlendFunc = register(new Setting("CustomBlendFunc", false, v-> glint.getValue()));
    public Setting<Float> red = register(new Setting<>("Red", 0.0f, 0.0f, 255.0f));
    public Setting<Float> green = register(new Setting<>("Green", 255.0f, 0.0f, 255.0f));
    public Setting<Float> blue = register(new Setting<>("Blue", 0.0f, 0.0f, 255.0f));
    public Setting<Float> alpha = register(new Setting<>("Alpha", 0.0f, 0.0f, 255.0f));

    static final ResourceLocation RES_ITEM_GLINT;

    public PlayerChams() {
        super("PlayerChams", Category.VISUAL, "Renders stuff on players.");
    }

    @SubscribeEvent
    public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
        event.getEntityPlayer().isImmuneToFire = true;
    }

    @SubscribeEvent
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (event.getEntityLivingBase() instanceof EntityPlayer && event.getEntityLivingBase().equals(PopESP.getInstance().fakeEntity)) {
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
            if(glint.getValue()){
                mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
                GL11.glTexCoord3d(1.0, 1.0, 1.0);
                GL11.glEnable(3553);
                GL11.glBlendFunc(768, 771);
                 if (customBlendFunc.getValue()) {
                    GL11.glBlendFunc(770, 32772);
                }
                else {
                    GL11.glBlendFunc(770, 771);
                }
            }
            GL11.glColor4f(red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f, alpha.getValue() / 255f);
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
            if (walls.getValue()) {
                glEnable(GL_DEPTH_TEST);
            }
            if (texture2D.getValue()) {
                glEnable(GL_TEXTURE_2D);
            }
            glPopAttrib();
            glPopMatrix();
        }
    }

    static {
        RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    }
}
