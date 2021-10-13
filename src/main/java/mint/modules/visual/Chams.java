package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderCrystalEvent;
import mint.events.RenderLivingEntityEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class Chams extends Module {
    private static Chams INSTANCE = new Chams();
    public Setting<Targets> targets = register(new Setting<>("Targets", Targets.PLAYERS));

    public enum Targets {PLAYERS, CRYSTALS, BOTH}

    public Setting<Boolean> transparent = register(new Setting<>("Transparant", false));
    public Setting<Boolean> texture2D = register(new Setting<>("Texture 2D", false));
    public Setting<Boolean> blend = register(new Setting<>("Blend", false));
    public Setting<Boolean> depthMask = register(new Setting<>("Depth Mask", false));
    public Setting<Boolean> walls = register(new Setting<>("Walls", false));
    public Setting<Boolean> cancel = register(new Setting<>("Cancel", false));
    public Setting<RenderMode> renderMode = register(new Setting<>("Render Mode", RenderMode.FILL));
    public enum RenderMode {FILL, WIRE, BOTH}
    public Setting<Boolean> glint = register(new Setting<>("Galaxy Texture", false));
    public Setting<Boolean> glintBlend = register(new Setting<>("Glint Blend", false));
    public Setting<Integer> rotations = register(new Setting<>("Rotations", 30, 0, 200));
    public Setting<Float> lineWidth = register(new Setting<>("Line Width", 1.0f, 0.1f, 3.0f));
    public Setting<Double> scale = register(new Setting<>("Scale", 1.0, 0.0, 2.0));
    public Setting<Float> red = register(new Setting<>("Red", 0.0f, 0.0f, 255.0f));
    public Setting<Float> green = register(new Setting<>("Green", 255.0f, 0.0f, 255.0f));
    public Setting<Float> blue = register(new Setting<>("Blue", 0.0f, 0.0f, 255.0f));
    public Setting<Float> alpha = register(new Setting<>("Alpha", 0.0f, 0.0f, 255.0f));
    public Setting<Boolean> rainbow = register(new Setting<>("Rainbow", true));

    static final ResourceLocation RES_ITEM_GLINT;

    static {
        RES_ITEM_GLINT = new ResourceLocation("textures/galaxy.png");
    }

    public Chams() {
        super("Chams", Category.VISUAL, "Changes how other players look & make them visible through walls.");
        this.setInstance();
    }

    public static Chams getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Chams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if(!isEnabled())
            return;
        if (event.getEntityLivingBase() instanceof EntityPlayer && !event.getEntityLivingBase().equals(PopESP.getInstance().fakeEntity) && (targets.getValue() == Targets.PLAYERS || targets.getValue() == Targets.BOTH)) {
        event.getEntityLivingBase().hurtTime = 0;
        event.getEntityLivingBase().maxHurtTime = 0;
        if (cancel.getValue()) {
            event.setCanceled(true);
        }
            if (transparent.getValue()) {
                GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            }
            glPushMatrix();
            glPushAttrib(GL_ALL_ATTRIB_BITS);
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
                if (glintBlend.getValue()) {
                    GL11.glBlendFunc(770, 32772);
                } else {
                    GL11.glBlendFunc(770, 771);
                }
            }

            switch (renderMode.getValue()) {
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
                    event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
                    break;
                }
            }

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glLineWidth((float) ((double) lineWidth.getValue()));

            GL11.glColor4f(rainbow.getValue() ? ColorUtil.rainbow(6).getRed() : red.getValue() / 255f, rainbow.getValue() ? ColorUtil.rainbow(6).getGreen() : green.getValue() / 255f, rainbow.getValue() ? ColorUtil.rainbow(6).getBlue() : blue.getValue() / 255f, alpha.getValue() / 255f);

            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());

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

    @SubscribeEvent
    public void onRenderCrystalPost(RenderCrystalEvent.RenderCrystalPostEvent event) {
        if (targets.getValue() == Targets.CRYSTALS || targets.getValue() == Targets.BOTH) {
            if (cancel.getValue()) {
                event.setCanceled(true);
            }
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
                if (glintBlend.getValue()) {
                    GL11.glBlendFunc(770, 32772);
                } else {
                    GL11.glBlendFunc(770, 771);
                }
            }

            switch (renderMode.getValue()) {
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
                    event.getModelNoBase().render(event.getEntityEnderCrystal(), 0, rotation * (rotations.getValue() / 10), rotationMoved * 0.2F, 0, 0, 0.0625F);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    event.getModelNoBase().render(event.getEntityEnderCrystal(), 0, rotation * (rotations.getValue() / 10), rotationMoved * 0.2F, 0, 0, 0.0625F);

                    break;
                }
            }

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glLineWidth((float) ((double) lineWidth.getValue()));

            GL11.glColor4f(red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f, alpha.getValue() / 255f);

            event.getModelNoBase().render(event.getEntityEnderCrystal(), 0, rotation * (rotations.getValue() / 10), rotationMoved * 0.2F, 0, 0, 0.0625F);

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

    @Override
    public void onLogin() {
        if (PopESP.getInstance().isEnabled() && this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
}
