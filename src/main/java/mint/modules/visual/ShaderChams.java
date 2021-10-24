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

public class ShaderChams extends Module {
    //TODO: make full alpha?
    static ShaderChams INSTANCE = new ShaderChams();
    public Setting<modes> mode = register(new Setting<>("Mode", modes.Aqua));

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
        }
        final FramebufferShader framebufferShader2 = framebufferShader;
        if (framebufferShader2 == null) {
            return;
        }
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
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
        framebufferShader2.stopDraw(Float.intBitsToFloat(Float.floatToIntBits(0.010916991f) ^ 0x7F4DDD2E), Float.intBitsToFloat(Float.floatToIntBits(3.0171999E38f) ^ 0x7F62FD28), Float.intBitsToFloat(Float.floatToIntBits(0.00893931f) ^ 0x7F6D762F), Float.intBitsToFloat(Float.floatToIntBits(0.096559145f) ^ 0x7EBAC0CD), radius, Float.intBitsToFloat(Float.floatToIntBits(4.801641f) ^ 0x7F19A70B));
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
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
                    modes.Custom
            };
        }
    }
}
