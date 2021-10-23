package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.utils.MathUtil;
import mint.utils.shader.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class ShaderChams extends Module {

    public Setting<modes> mode = register(new Setting<>("Mode", modes.Aqua));

    public ShaderChams() {
        super("Shader Chams", Category.VISUAL, "Makes shader on cham");
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        FramebufferShader framebufferShader = null;
        switch ((modes) mode.getValue()) {
            case Smoke: {
                framebufferShader = SmokeShader.SMOKE_SHADER;
                break;
            }
            case Aqua: {
                framebufferShader = AquaShader.AQUA_SHADER;
                break;
            }
            case Flow: {
                framebufferShader = FlowShader.FLOW_SHADER;
                break;
            }
            case Red: {
                framebufferShader = RedShader.RED_SHADER;
                break;
            }
            case Outline: {
                framebufferShader = GlowShader.GLOW_SHADER;
                break;
            }
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
                    if (!(entity instanceof EntityPlayer)) {
                        continue;
                    }
                    final Render getEntityRenderObject = mc.getRenderManager().getEntityRenderObject(entity);
                    if (getEntityRenderObject == null) {
                        continue;
                    }
                    final Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                    getEntityRenderObject.doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final float n2 = Float.intBitsToFloat(Float.floatToIntBits(3.651715f) ^ 0x7F69B5B3);
        final Float value3 = Float.intBitsToFloat(Float.floatToIntBits(0.9867451f) ^ 0x7F3C9B54);
        final Float radius;
        final Float n3 = radius = Float.intBitsToFloat(Float.floatToIntBits(1799.2811f) ^ 0x7BE0E8FF) + value3;
        final float red = Float.intBitsToFloat(Float.floatToIntBits(1.4528846f) ^ 0x7CC6F81F);
        final float green = Float.intBitsToFloat(Float.floatToIntBits(8.874367E37f) ^ 0x7E8586D1);
        final float blue = Float.intBitsToFloat(Float.floatToIntBits(0.01116983f) ^ 0x7F4801AA);
        final float alpha = Float.intBitsToFloat(Float.floatToIntBits(0.008144599f) ^ 0x7F7A70ED);
        framebufferShader2.stopDraw(Float.intBitsToFloat(Float.floatToIntBits(0.010916991f) ^ 0x7F4DDD2E), Float.intBitsToFloat(Float.floatToIntBits(3.0171999E38f) ^ 0x7F62FD28), Float.intBitsToFloat(Float.floatToIntBits(0.00893931f) ^ 0x7F6D762F), Float.intBitsToFloat(Float.floatToIntBits(0.096559145f) ^ 0x7EBAC0CD), radius, Float.intBitsToFloat(Float.floatToIntBits(4.801641f) ^ 0x7F19A70B));
        GlStateManager.color(Float.intBitsToFloat(Float.floatToIntBits(4.0344067f) ^ 0x7F0119DC), Float.intBitsToFloat(Float.floatToIntBits(10.789216f) ^ 0x7EACA0A1), Float.intBitsToFloat(Float.floatToIntBits(5.1625485f) ^ 0x7F253399));
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
        Outline;

        public static modes[] $VALUES;

        static {
            modes.$VALUES = new modes[]{modes.Smoke, modes.Aqua, modes.Flow, modes.Red, modes.Outline};
        }
    }
}
