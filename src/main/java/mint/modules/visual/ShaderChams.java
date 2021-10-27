

package mint.modules.visual;

import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.EnumSetting;
import mint.utils.MathUtil;
import mint.utils.shader.FramebufferShader;
import mint.utils.shader.shaders.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

@ModuleInfo(name = "Shader Chams", category = Module.Category.Visual, description = "Makes shader on cham")
public class ShaderChams extends Module {

    static ShaderChams INSTANCE = new ShaderChams();
    public EnumSetting mode = new EnumSetting("Mode", modes.Aqua, this);

    public ShaderChams() {
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
        if (mode.getValue().equals(modes.Smoke))
            framebufferShader = SmokeShader.SMOKE_SHADER;
        else if (mode.getValue().equals(modes.Aqua))
            framebufferShader = AquaShader.AQUA_SHADER;
        else if (mode.getValue().equals(modes.Flow))
            framebufferShader = FlowShader.FLOW_SHADER;
        else if (mode.getValue().equals(modes.Red))
            framebufferShader = RedShader.RED_SHADER;
        else if (mode.getValue().equals(modes.Outline))
            framebufferShader = GlowShader.GLOW_SHADER;
        else if (mode.getValue().equals(modes.Rainbow))
            framebufferShader = RainbowShader.RAINBOW_SHADER;
        else if (mode.getValue().equals(modes.Star))
            framebufferShader = StarShader.STAR_SHADER;
        else if (mode.getValue().equals(modes.Galaxy))
            framebufferShader = GalaxyShader.GALAXY_SHADER;
        else if (mode.getValue().equals(modes.IIV))
            framebufferShader = IIVShader.IIV_SHADER;
        else if (mode.getValue().equals(modes.Cloud))
            framebufferShader = CloudShader.CLOUD_SHADER;
        else if (mode.getValue().equals(modes.BlueSpace))
            framebufferShader = BlueSpaceShader.BLUE_SPACE_SHADER;
        else if (mode.getValue().equals(modes.Aurora))
            framebufferShader = AuroraShader.AURORA_SHADER;
        else if (mode.getValue().equals(modes.Hamburger))
            framebufferShader = HamburgerShader.HAMBURGER_SHADER;

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
                    if (mc.getRenderManager().getEntityRenderObject(entity) == null)
                        continue;
                    final Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                    ((EntityPlayer) entity).hurtTime = 0;
                    Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());

                }
            }
        } catch (Exception ignored) {
        }
        final float radius = Float.intBitsToFloat(Float.floatToIntBits(1799.2811f) ^ 0x7BE0E8FF) + Float.intBitsToFloat(Float.floatToIntBits(0.9867451f) ^ 0x7F3C9B54);
        framebufferShader2.stopDraw(Float.intBitsToFloat(Float.floatToIntBits(0.010916991f) ^ 0x7F4DDD2E), Float.intBitsToFloat(Float.floatToIntBits(3.0171999E38f) ^ 0x7F62FD28), Float.intBitsToFloat(Float.floatToIntBits(0.00893931f) ^ 0x7F6D762F), 255.0f, radius, Float.intBitsToFloat(Float.floatToIntBits(4.801641f) ^ 0x7F19A70B));
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
        Hamburger;

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
            };
        }
    }
}