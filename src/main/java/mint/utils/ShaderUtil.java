package mint.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public abstract class ShaderUtil extends Shader {

    static Framebuffer framebuffer;
    static Minecraft mc = Minecraft.getMinecraft();
    float red, green, blue, alpha = 1F;
    float radius = 2F;
    float quality = 1F;

    static {
        framebuffer = setupFrameBuffer(framebuffer);
    }

    public ShaderUtil(String fragmentShader) {
        super(fragmentShader);
    }

    public void startDraw(float partialTicks) {
        GlStateManager.enableAlpha();

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        mc.gameSettings.entityShadows = false;
        mc.entityRenderer.orientCamera(partialTicks);
    }

    public void stopDraw(Color color, float radius, float quality) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(true);

        red = color.getRed() / 255F;
        green = color.getGreen() / 255F;
        blue = color.getBlue() / 255F;
        alpha = color.getAlpha() / 255F;
        this.radius = radius;
        this.quality = quality;

        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();

        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(framebuffer);

        mc.entityRenderer.disableLightmap();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public static Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if (frameBuffer != null)
            frameBuffer.deleteFramebuffer();

        frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);

        return frameBuffer;
    }

    public void drawFramebuffer(Framebuffer framebuffer) {
        startShader();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        glBegin(GL_QUADS);
        glTexCoord2d(0, 1);
        glVertex2d(0, 0);
        glTexCoord2d(0, 0);
        glVertex2d(0, scaledResolution.getScaledHeight());
        glTexCoord2d(1, 0);
        glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        glTexCoord2d(1, 1);
        glVertex2d(scaledResolution.getScaledWidth(), 0);
        glEnd();
        stopShader();
    }
}
