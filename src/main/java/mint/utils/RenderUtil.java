package mint.utils;

import mint.Mint;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    public static RenderItem itemRender;
    public static ICamera camera;
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Tessellator tessellator;
    public static BufferBuilder builder;

    static {
        Minecraft mc = Minecraft.getMinecraft();
        itemRender = mc.getRenderItem();
        camera = new Frustum();
        tessellator = Tessellator.getInstance();
        builder = RenderUtil.tessellator.getBuffer();
    }

    public static void drawGradientBlockOutline(BlockPos pos, Color startColor, Color endColor, float linewidth, double height) {
        IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        Vec3d interp = interpolateEntity(RenderUtil.mc.player, mc.getRenderPartialTicks());
        drawGradientBlockOutline(iblockstate.getSelectedBoundingBox(RenderUtil.mc.world, pos).grow(0.002f).offset(-interp.x, -interp.y, -interp.z).expand(0.0, height, 0.0), startColor, endColor, linewidth);
    }

    public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
        float red = (float) startColor.getRed() / 255.0f;
        float green = (float) startColor.getGreen() / 255.0f;
        float blue = (float) startColor.getBlue() / 255.0f;
        float alpha = (float) startColor.getAlpha() / 255.0f;
        float red1 = (float) endColor.getRed() / 255.0f;
        float green1 = (float) endColor.getGreen() / 255.0f;
        float blue1 = (float) endColor.getBlue() / 255.0f;
        float alpha1 = (float) endColor.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red1, green1, blue1, alpha1).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    public static void drawGlowBox(BlockPos pos, Color color, double height) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() + 1 - mc.getRenderManager().viewerPosX, pos.getY() + 1 - mc.getRenderManager().viewerPosY, pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
        glSetup();
        glPrepare();
        drawSelectionGlowFilledBox(axisAlignedBB, height, new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()), new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
        glRestore();
        glRelease();
    }

    public static void drawSelectionGlowFilledBox(AxisAlignedBB axisAlignedBB, double height, Color startColor, Color endColor) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder = Tessellator.getInstance().getBuffer();
        BufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        addChainedGlowBoxVertices(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY + height, axisAlignedBB.maxZ, startColor, endColor);
        tessellator.draw();
    }

    public static void addChainedGlowBoxVertices(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color startColor, Color endColor) {
        BufferBuilder BufferBuilder = Tessellator.getInstance().getBuffer();
        BufferBuilder.pos(minX, minY, minZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, minY, minZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, minY, maxZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, minY, maxZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, maxY, minZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, maxY, maxZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, maxY, maxZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, maxY, minZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, minY, minZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, maxY, minZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, maxY, minZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, minY, minZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, minY, minZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, maxY, minZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, maxY, maxZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, minY, maxZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, minY, maxZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, minY, maxZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(maxX, maxY, maxZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, maxY, maxZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, minY, minZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, minY, maxZ).color(startColor.getRed() / 255.0f, startColor.getGreen() / 255.0f, startColor.getBlue() / 255.0f, startColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, maxY, maxZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
        BufferBuilder.pos(minX, maxY, minZ).color(endColor.getRed() / 255.0f, endColor.getGreen() / 255.0f, endColor.getBlue() / 255.0f, endColor.getAlpha() / 255.0f).endVertex();
    }

    public static void glSetup() {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
    }

    public static void glRelease() {
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void glPrepare() {
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL_SMOOTH);
    }

    public static void glRestore() {
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(GL_FLAT);
    }

    public static Vec3d updateToCamera(final Vec3d vec) {
        return new Vec3d(vec.x - RenderUtil.mc.getRenderManager().viewerPosX, vec.y - RenderUtil.mc.getRenderManager().viewerPosY, vec.z - RenderUtil.mc.getRenderManager().viewerPosZ);
    }
    public static void addBuilderVertex(final BufferBuilder bufferBuilder, final double x, final double y, final double z, final Color color) {
        bufferBuilder.pos(x, y, z).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f).endVertex();
    }

    public static void prepare() {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        glEnable(2848);
        GL11.glBlendFunc(770, 771);
    }

    public static void release() {
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        glEnable(3553);
        GL11.glPolygonMode(1032, 6914);
    }

    public static void drawText(BlockPos pos, String text, int color) {
        GlStateManager.pushMatrix();
        RenderUtil.glBillboardDistanceScaled((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f, mc.player, 1.0f);
        GlStateManager.disableDepth();
        GlStateManager.translate(-((double) Mint.textManager.getStringWidth(text) / 2.0), 0.0, 0.0);
        Mint.textManager.drawStringWithShadow(text, 0.0f, 0.0f, color);
        GlStateManager.popMatrix();
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667f;
        GlStateManager.translate((double) x - mc.getRenderManager().renderPosX, (double) y - mc.getRenderManager().renderPosY, (double) z - mc.getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.player.rotationPitch, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }


    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        RenderUtil.glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = (float) distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }
    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air);
        }
    }

    public static void drawBlockOutline(final BlockPos pos, final Color color, final float linewidth, final boolean air) {
        final IBlockState iblockstate = RenderUtil.mc.world.getBlockState(pos);
        if ((air || iblockstate.getMaterial() != Material.AIR) && RenderUtil.mc.world.getWorldBorder().contains(pos)) {
            final Vec3d interp = interpolateEntity( RenderUtil.mc.player , RenderUtil.mc.getRenderPartialTicks());
            drawBlockOutline(iblockstate.getSelectedBoundingBox( RenderUtil.mc.world , pos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), color, linewidth);
        }
    }


    public static void drawBox(final BlockPos pos, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, pos.getX() + 1 - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() + 1 - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() + 1 - RenderUtil.mc.getRenderManager().viewerPosZ);
        RenderUtil.camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (RenderUtil.camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            glEnable(2848);
            glHint(3154, 4354);
            RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawBox(final BlockPos pos, final Color color, final double height) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, pos.getX() + 1 - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() + height - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() + 1 - RenderUtil.mc.getRenderManager().viewerPosZ);
        RenderUtil.camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (RenderUtil.camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.getRenderManager().viewerPosX, bb.minY + RenderUtil.mc.getRenderManager().viewerPosY, bb.minZ + RenderUtil.mc.getRenderManager().viewerPosZ, bb.maxX + RenderUtil.mc.getRenderManager().viewerPosX, bb.maxY + RenderUtil.mc.getRenderManager().viewerPosY, bb.maxZ + RenderUtil.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            glEnable(2848);
            glHint(3154, 4354);
            RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    // from client 2.0.0
    public static void drawBoxESPBB(AxisAlignedBB a, Color boxColor, Color outlineColor, float lineWidth, boolean outline, boolean box, float alpha, float scale, float slab) {
        double f = 0.5 * (1 - scale);
        AxisAlignedBB bb = RenderUtil.interpolateAxis(new AxisAlignedBB(
                a.minX + f,
                a.minY + f + (1 - slab),
                a.minZ + f,
                a.maxX - f,
                a.maxY - f,
                a.maxZ - f
        ));
        float rB = (float) boxColor.getRed() / 255.0f;
        float gB = (float) boxColor.getGreen() / 255.0f;
        float bB = (float) boxColor.getBlue() / 255.0f;
        float aB = (float) boxColor.getAlpha() / 255.0f;
        float rO = (float) outlineColor.getRed() / 255.0f;
        float gO = (float) outlineColor.getGreen() / 255.0f;
        float bO = (float) outlineColor.getBlue() / 255.0f;
        float aO = (float) outlineColor.getAlpha() / 255.0f;
        if ( alpha > 1 ) alpha = 1;
        aB *= alpha;
        aO *= alpha;
        if (box) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771 , 0 , 1 );
            GlStateManager.disableTexture2D ( );
            GlStateManager.depthMask ( false );
            glEnable ( 2848 );
            glHint ( 3154 , 4354 );
            RenderGlobal.renderFilledBox ( bb , rB , gB , bB , aB );
            GL11.glDisable ( 2848 );
            GlStateManager.depthMask ( true );
            GlStateManager.enableDepth ( );
            GlStateManager.enableTexture2D ( );
            GlStateManager.disableBlend ( );
            GlStateManager.popMatrix ( );
        }
        if ( outline ) {
            GlStateManager.pushMatrix ( );
            GlStateManager.enableBlend ( );
            GlStateManager.disableDepth ( );
            GlStateManager.tryBlendFuncSeparate ( 770 , 771 , 0 , 1 );
            GlStateManager.disableTexture2D ( );
            GlStateManager.depthMask ( false );
            glEnable ( 2848 );
            glHint ( 3154 , 4354 );
            GL11.glLineWidth ( lineWidth );
            Tessellator tessellator = Tessellator.getInstance ( );
            BufferBuilder bufferbuilder = tessellator.getBuffer ( );
            bufferbuilder.begin ( 3 , DefaultVertexFormats.POSITION_COLOR );
            bufferbuilder.pos ( bb.minX , bb.minY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.minY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.minY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.minY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.minY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.maxY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.maxY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.minY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.minY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.maxY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.maxY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.maxY , bb.maxZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.maxY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.minY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.maxX , bb.maxY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            bufferbuilder.pos ( bb.minX , bb.maxY , bb.minZ ).color ( rO , gO , bO , aO ).endVertex ( );
            tessellator.draw ( );
            GL11.glDisable ( 2848 );
            GlStateManager.depthMask ( true );
            GlStateManager.enableDepth ( );
            GlStateManager.enableTexture2D ( );
            GlStateManager.disableBlend ( );
            GlStateManager.popMatrix ( );
        }
    }


    public static void drawGradientRect(final double leftpos, final double top, final double right, final double bottom, final int col1, final int col2) {
        final float f = (col1 >> 24 & 0xFF) / 255.0f;
        final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f4 = (col1 & 0xFF) / 255.0f;
        final float f5 = (col2 >> 24 & 0xFF) / 255.0f;
        final float f6 = (col2 >> 16 & 0xFF) / 255.0f;
        final float f7 = (col2 >> 8 & 0xFF) / 255.0f;
        final float f8 = (col2 & 0xFF) / 255.0f;
        glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glVertex2d(leftpos, top);
        GL11.glVertex2d(leftpos, bottom);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawArrow(float x, float y, float size, float widthDiv, float heightDiv, float outlineWidth, boolean isOpen) {
        boolean blend = GL11.glIsEnabled(3042);
            if (isOpen) {
                GL11.glPushMatrix();
                GL11.glScaled(1.3, 1.3, 1.3);
                y -= 1.5f;
                x += 2;
                x /= 1.3;
                y /= 1.3;
                glEnable(GL_LINE_SMOOTH);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                glEnable(GL11.GL_BLEND);
                GL11.glLineWidth(1);
                GL11.glColor4f(255, 255, 255, 255);
                GL11.glBegin(GL11.GL_LINES);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x + 4, y + 3);
                GL11.glEnd();
                GL11.glBegin(GL11.GL_LINES);
                GL11.glVertex2d(x + 4, y + 3);
                GL11.glVertex2d(x, y + 6);
                GL11.glEnd();
                GL11.glDisable(GL11.GL_BLEND);
                glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL_LINE_SMOOTH);
                GL11.glPopMatrix();
            } else {
                glEnable(3042);
                GL11.glDisable(3553);
                GL11.glBlendFunc(770, 771);
                glEnable(2848);
                GL11.glPushMatrix();
                GL11.glLineWidth(outlineWidth);
                GL11.glBegin(2);
                GL11.glColor4f(255, 255, 255, 255);
                GL11.glVertex2d(x, y);
                GL11.glVertex2d(x - size / widthDiv, y - size);
                GL11.glVertex2d(x, y - size / heightDiv);
                GL11.glVertex2d(x + size / widthDiv, y - size);
                GL11.glVertex2d(x, y);
                GL11.glEnd();
                GL11.glPopMatrix();
                glEnable(3553);
                if (!blend) {
                    GL11.glDisable(3042);
                }
                GL11.glDisable(2848);
        }
    }

    public static void drawBoxESPFlat(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air) {
        if (box) {
            RenderUtil.drawBoxFlat(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
        }
        if (outline) {
            RenderUtil.drawBlockOutlineFlat(pos, secondC ? secondColor : color, lineWidth, air);
        }
    }
    public static void drawBlockOutlineFlat(final BlockPos pos, final Color color, final float linewidth, final boolean air) {
        final IBlockState iblockstate = Mint.INSTANCE.mc.world.getBlockState(pos);
        if ((air || iblockstate.getMaterial() != Material.AIR) && Mint.INSTANCE.mc.world.getWorldBorder().contains(pos)) {
            final Vec3d interp = interpolateEntity(Mint.INSTANCE.mc.player , Mint.INSTANCE.mc.getRenderPartialTicks());
            drawBlockOutlineFlat(iblockstate.getSelectedBoundingBox(Mint.INSTANCE.mc.world , pos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), color, linewidth);
        }
    }

    public static void drawBoxFlat(final BlockPos pos, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - Mint.INSTANCE.mc.getRenderManager().viewerPosX, pos.getY() - Mint.INSTANCE.mc.getRenderManager().viewerPosY, pos.getZ() - Mint.INSTANCE.mc.getRenderManager().viewerPosZ, pos.getX() + 1 - Mint.INSTANCE.mc.getRenderManager().viewerPosX, pos.getY() - Mint.INSTANCE.mc.getRenderManager().viewerPosY, pos.getZ() + 1 - Mint.INSTANCE.mc.getRenderManager().viewerPosZ);
        RenderUtil.camera.setPosition(Objects.requireNonNull(Mint.INSTANCE.mc.getRenderViewEntity()).posX, Mint.INSTANCE.mc.getRenderViewEntity().posY, Mint.INSTANCE.mc.getRenderViewEntity().posZ);
        if (RenderUtil.camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + Mint.INSTANCE.mc.getRenderManager().viewerPosX, bb.minY + Mint.INSTANCE.mc.getRenderManager().viewerPosY, bb.minZ + Mint.INSTANCE.mc.getRenderManager().viewerPosZ, bb.maxX + Mint.INSTANCE.mc.getRenderManager().viewerPosX, bb.maxY + Mint.INSTANCE.mc.getRenderManager().viewerPosY, bb.maxZ + Mint.INSTANCE.mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            glEnable(2848);
            glHint(3154, 4354);
            RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawBlockOutlineFlat(final AxisAlignedBB bb, final Color color, final float linewidth) {
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(2848);
        glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }
    public static void drawFilledBox(AxisAlignedBB bb, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7 , DefaultVertexFormats.POSITION_COLOR );
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true );
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - Mint.INSTANCE.mc.getRenderManager().viewerPosX ,bb.minY - Mint.INSTANCE.mc.getRenderManager().viewerPosY, bb.minZ - Mint.INSTANCE.mc.getRenderManager().viewerPosZ, bb.maxX - Mint.INSTANCE.mc.getRenderManager().viewerPosX, bb.maxY - Mint.INSTANCE.mc.getRenderManager().viewerPosY, bb.maxZ - Mint.INSTANCE.mc.getRenderManager().viewerPosZ);
    }

    public static void drawBlockOutline(final AxisAlignedBB bb, final Color color, final float linewidth) {
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(2848);
        glHint(3154, 4354);
        GL11.glLineWidth(linewidth);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawRectCol(final float x, final float y, final float width, final float height, final Color color) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y + height);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glEnd();
        glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    public static void drawBorder(float x, float y, float width, float height, Color color) {
        RenderUtil.drawRectCol(x - 1, y - 1, 1, height + 2, color);
        RenderUtil.drawRectCol(x + width, y - 1, 1, height + 2, color);
        RenderUtil.drawRectCol(x, y - 1, width, 1, color);
        RenderUtil.drawRectCol(x, y + height, width, 1, color);
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void glEnd() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        glEnable(2929);
        glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawColorPickerSquare(float x, float y, float w, float h, float hue, int alpha) {
        RenderUtil.prepare(1);
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        final Color starting = new Color(Color.HSBtoRGB(hue, 1, 1));

        builder.pos(w, y, 0).color(starting.getRed(), starting.getGreen(), starting.getBlue(), alpha).endVertex();
        builder.pos(x, y, 0).color(255, 255, 255, alpha).endVertex();
        builder.pos(x, h, 0).color(255, 255, 255, alpha).endVertex();
        builder.pos(w, h, 0).color(starting.getRed(), starting.getGreen(), starting.getBlue(), alpha).endVertex();

        builder.pos(w, y, 0).color(0, 0, 0, 0).endVertex();
        builder.pos(x, y, 0).color(0, 0, 0, 0).endVertex();
        builder.pos(x, h, 0).color(0, 0, 0, alpha).endVertex();
        builder.pos(w, h, 0).color(0, 0, 0, alpha).endVertex();

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.enableAlpha();
    }

    public static void prepare(float width) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.glLineWidth(width);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(GL_SMOOTH);
        }
    }

