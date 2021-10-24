package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.utils.MathUtil;
import mint.utils.shader.FramebufferShader;
import mint.utils.shader.shaders.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class ShaderChams extends Module {
    static ShaderChams INSTANCE = new ShaderChams();
    public Setting<modes> mode = register(new Setting<>("Mode", modes.Aqua));
    Setting<Boolean> kambingChadMode = register(new Setting("GL_ENABLE_KAMBING", true));

    public ShaderChams() {
        super("Shader Chams", Category.VISUAL, "Makes shader on cham");
        setInstance();
    }

    public static ShaderChams getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ShaderChams();
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }

    public static void beginSolid() {
        GL11.glPushAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(1f / 255f, 1f / 255f, 1f / 255f, 255);
    }

    public static void endSolid() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glPopAttrib();
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        FramebufferShader framebufferShader = null;
        switch (mode.getValue()) {
            case Smoke:
                framebufferShader = SmokeShader.SMOKE_SHADER;
                break;
            case Aqua:
                framebufferShader = AquaShader.AQUA_SHADER;
                break;
            case Flow:
                framebufferShader = FlowShader.FLOW_SHADER;
                break;
            case Red:
                framebufferShader = RedShader.RED_SHADER;
                break;
            case Outline:
                framebufferShader = GlowShader.GLOW_SHADER;
                break;
            case Rainbow:
                framebufferShader = RainbowShader.RAINBOW_SHADER;
                break;
            case Star:
                framebufferShader = StarShader.STAR_SHADER;
                break;
            case Galaxy:
                framebufferShader = GalaxyShader.GALAXY_SHADER;
                break;
            case IIV:
                framebufferShader = IIVShader.IIV_SHADER;
                break;
            case Cloud:
                framebufferShader = CloudShader.CLOUD_SHADER;
                break;
            case BlueSpace:
                framebufferShader = BlueSpaceShader.BLUE_SPACE_SHADER;
                break;
            case Aurora:
                framebufferShader = AuroraShader.AURORA_SHADER;
                break;
            case Hamburger:
                framebufferShader = HamburgerShader.HAMBURGER_SHADER;
                break;
            case Custom:
                framebufferShader = CustomShader.CUSTOM_SHADER;
                break;
            case Lava:
                framebufferShader = LavaShader.LAVA_SHADER;
                break;
            case Black:
                framebufferShader = BlackShader.BLACK_SHADER;
                break;
            case GreenGalaxy:
                framebufferShader = GreenGalaxyShader.GREENGALAXY_SHADER;
                break;

        }
        final FramebufferShader framebufferShader2 = framebufferShader;
        if (framebufferShader2 == null) {
            return;
        }

        if (kambingChadMode.getValue()) {
            beginSolid();
        } else {
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
        }

        framebufferShader2.startDraw(event.getPartialTicks());
        try {
            for (final Entity entity : mc.world.loadedEntityList) {
                if (entity != mc.player && entity != mc.getRenderViewEntity()) {
                    if (!(entity instanceof EntityPlayer))
                        continue;
                    final Render getEntityRenderObject = mc.getRenderManager().getEntityRenderObject(entity);
                    if (getEntityRenderObject == null)
                        continue;
                    final Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                    ((EntityPlayer) entity).hurtTime = 0;
                    getEntityRenderObject.doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final float radius = Float.intBitsToFloat(Float.floatToIntBits(1799.2811f) ^ 0x7BE0E8FF) + Float.intBitsToFloat(Float.floatToIntBits(0.9867451f) ^ 0x7F3C9B54);
        framebufferShader2.stopDraw(Float.intBitsToFloat(Float.floatToIntBits(0.010916991f) ^ 0x7F4DDD2E), Float.intBitsToFloat(Float.floatToIntBits(3.0171999E38f) ^ 0x7F62FD28), Float.intBitsToFloat(Float.floatToIntBits(0.00893931f) ^ 0x7F6D762F), 255, radius, Float.intBitsToFloat(Float.floatToIntBits(4.801641f) ^ 0x7F19A70B));
        if (kambingChadMode.getValue()) {
            endSolid();
        } else {
            GlStateManager.color(1f, 1f, 1f, 255.0f);
            GlStateManager.matrixMode(5889);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
        }

    }

    public enum modes {
        Smoke,
        Aqua,
        Flow,
        Red,
        Outline,
        Rainbow,
        Star,
        Galaxy,
        IIV,
        Cloud,
        BlueSpace,
        Aurora,
        Hamburger,
        Black,
        GreenGalaxy,
        Lava,
        Custom;

        public static modes[] $VALUES;

        static {
            modes.$VALUES = new modes[]{
                    modes.Smoke,
                    modes.Aqua,
                    modes.Flow,
                    modes.Red,
                    modes.Outline,
                    modes.Rainbow,
                    modes.Star,
                    modes.Galaxy,
                    modes.IIV,
                    modes.Cloud,
                    modes.BlueSpace,
                    modes.Aurora,
                    modes.Hamburger,
                    modes.Black,
                    modes.GreenGalaxy,
                    modes.Lava,
                    modes.Custom
            };
        }

    }
}
