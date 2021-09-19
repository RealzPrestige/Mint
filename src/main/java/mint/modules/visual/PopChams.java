package mint.modules.visual;

import com.mojang.authlib.GameProfile;
import mint.clickgui.setting.Setting;
import mint.events.Render3DEvent;
import mint.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class PopChams extends Module {
    EntityPlayer player = null;
    ModelPlayer playerModel = null;
    long startTime;
    public static PopChams INSTANCE = new PopChams();

    /**
     * @author kambing
     * @since 19/9/2021 on 9:40am
     */

    public Setting<Boolean> self = register(new Setting<>("Self", true));
    public Setting<Integer> range = register(new Setting<>("Range", 30, 10, 100));
    public Setting<Boolean> outline = register(new Setting<>("Outline", true));
    public Setting<Integer> fadeStart = register(new Setting<>("FadeTime", 1000, 0, 5000));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 120, 0, 255));

    public PopChams() {
        super("PopChams" , Category.VISUAL, "ur mom chams");}

    /**
     *so this is how it works. on totem pop it will render a static entity from
     * minecraft render manager. its still in wip tho (TODO: ytravel)
     */

    public void k(EntityPlayer poppedPlayer) {
        if (self.getValue() || poppedPlayer != mc.player) {
                GameProfile profile = new GameProfile(mc.player.getUniqueID(), "");
                player = new EntityOtherPlayerMP(mc.world, profile);
                player.copyLocationAndAnglesFrom(poppedPlayer);
                startTime = System.currentTimeMillis();
            }
        }

    @Override
    public void onUpdate(){
        for (EntityPlayer entity : mc.world.playerEntities) {
            //debugging purposes
            if (entity.getName().equals("MintClient")) {
                k(entity);
            }

        }

    }

    @Override
    public void onRender3D(Render3DEvent event) {
            playerModel = new ModelPlayer(0, false);

            GL11.glLineWidth(1);

            int fillA = alpha.getValue();
            int lineA = 255;

            if (System.currentTimeMillis() - startTime > fadeStart.getValue()) {
                long time = System.currentTimeMillis() - startTime - fadeStart.getValue();
                double normal = normalize(((double) time), 0, fadeStart.getValue());
                normal = MathHelper.clamp(normal, 0, 1);
                normal = (-normal) + 1;
                lineA = (int) (normal * lineA);
                fillA = (int) (normal * fillA);
            }
            if (player != null && lineA > 0) {
                //TODO:add solid
                RenderTesselator.prepareGL();
                if (outline.getValue()) {
                    GlStateManager.pushMatrix();
                    GL11.glPushAttrib(1048575);
                    GL11.glPolygonMode(1032, 6913);
                    glDisable(3553);
                    glDisable(2896);
                    glDisable(2929);
                    glEnable(2848);
                    glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glColor4f(red.getValue() / 255.0f, green.getValue() / 255.0f, blue.getValue() / 255.0f, lineA / 255.0f);
                    mc.getRenderManager().renderEntityStatic(player, event.getPartialTicks(),false);
                    glEnable(2896);
                    GlStateManager.popAttrib();
                    GlStateManager.popMatrix();
                }
                RenderTesselator.releaseGL();
            }
        }
    

    double normalize(double value, double min, double max) {
        return ((value - min) / (max - min));

    }
    @Override
    public void onEnable(){
        //debugging purposes
        k(mc.player);
    }
    public static class RenderTesselator extends Tessellator {

        public static RenderTesselator INSTANCE = new RenderTesselator();

        public RenderTesselator() {
            super(0x200000);
        }

        public static void prepare(int mode) {
            prepareGL();
            begin(mode);
        }

        public static void prepareGL() {
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(1.5F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.color(1, 1, 1);
        }

        public static void begin(int mode) {
            INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
        }

        public static void release() {
            render();
            releaseGL();
        }

        public static void render() {
            INSTANCE.draw();
        }

        public static void releaseGL() {
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
        }
    }
}
