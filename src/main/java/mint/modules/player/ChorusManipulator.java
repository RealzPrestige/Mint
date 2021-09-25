package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;


/**
 *
 * @Author zPrestige_
 * Inspired by KamiV
 * @Since 24/09/2021
 *
 */
public class ChorusManipulator extends Module {
    public static ChorusManipulator INSTANCE = new ChorusManipulator();
    public Setting<Boolean> cancel = register(new Setting<>("Cancel",false));
    public Setting<Boolean> solidParent = register(new Setting<>("Solid", true, false));
    public Setting<Boolean> solidSetting = register(new Setting("Render Solid", true, v -> solidParent.getValue()));
    public Setting<Float> red = register(new Setting<>("Solid Red", 140.0f, 0.0f, 255.0f, v -> solidParent.getValue() && solidSetting.getValue()));
    public Setting<Float> green = register(new Setting<>("Solid Green", 100.0f, 0.0f, 255.0f, v -> solidParent.getValue() && solidSetting.getValue()));
    public Setting<Float> blue = register(new Setting<>("Solid Blue", 140.0f, 0.0f, 255.0f, v -> solidParent.getValue() && solidSetting.getValue()));
    public Setting<Float> alpha = register(new Setting<>("Solid Alpha", 50.0f, 0.0f, 255.0f, v -> solidParent.getValue() && solidSetting.getValue()));
    public Setting<Boolean> wireFrameParent = register(new Setting<>("Wire Frame", true, false));
    public Setting<Boolean> wireFrameSetting = register(new Setting("Render Wire", true, v -> wireFrameParent.getValue()));
    public Setting<Float> wireRed = register(new Setting<>("Wire Red", 140.0f, 0.0f, 255.0f, v -> wireFrameParent.getValue() && wireFrameSetting.getValue()));
    public Setting<Float> wireGreen = register(new Setting<>("Wire Green", 100.0f, 0.0f, 255.0f, v -> wireFrameParent.getValue() && wireFrameSetting.getValue()));
    public Setting<Float> wireBlue = register(new Setting<>("WireBlue", 140.0f, 0.0f, 255.0f, v -> wireFrameParent.getValue() && wireFrameSetting.getValue()));
    public Setting<Float> wireAlpha = register(new Setting<>("WireBlue", 255.0f, 0.0f, 255.0f, v -> wireFrameParent.getValue() && wireFrameSetting.getValue()));

    Queue<CPacketPlayer> packets;
    Queue<CPacketConfirmTeleport> tpPackets;
    public EntityOtherPlayerMP fakeEntity;

    double xPos;
    double yPos;
    double zPos;

    public HashMap<EntityPlayer, Integer> playerCham = new HashMap<>();

    public ChorusManipulator() {
        super("ChorusManipulator", Category.PLAYER, "Manipulates your Chorus Fruits.");
        packets = new LinkedList<>();
        tpPackets = new LinkedList<>();
    }

    public void onLogin() {
        disable();
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if(fullNullCheck()){
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && cancel.getValue()) {
            xPos = ((SPacketPlayerPosLook) event.getPacket()).getX();
            yPos = ((SPacketPlayerPosLook) event.getPacket()).getY();
            zPos = ((SPacketPlayerPosLook) event.getPacket()).getZ();
            playerCham.clear();
            onChorus();
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            packets.add(event.getPacket());
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport) {
            tpPackets.add(event.getPacket());
            event.setCanceled(true);
        }
    }

    @Override
    public void onDisable() {
        while (!packets.isEmpty()) {
           mc.getConnection().sendPacket(packets.poll());
        }
        while (!tpPackets.isEmpty()) {
            mc.getConnection().sendPacket(tpPackets.poll());
        }
        playerCham.clear();
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        for (Map.Entry<EntityPlayer, Integer> pop : playerCham.entrySet()) {
            if (wireFrameSetting.getValue()) {
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                glDisable(3553);
                glDisable(2896);
                glDisable(2929);
                glEnable(2848);
                glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(wireRed.getValue() / 255f, wireGreen.getValue() / 255f, wireBlue.getValue() / 255f, wireAlpha.getValue() / 255f);
                renderEntityStatic(pop.getKey(), event.getPartialTicks(), false);
                GL11.glLineWidth(1f);
                glEnable(2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
            if (solidSetting.getValue()) {
                GL11.glPushMatrix();
                GL11.glDepthRange(0.01, 1.0f);
                GL11.glPushAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(false);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GL11.glLineWidth(1f);
                GL11.glColor4f(red.getValue() / 255f, green.getValue() / 255f, blue.getValue() / 255f, alpha.getValue() / 255f);
                renderEntityStatic(pop.getKey(), event.getPartialTicks(), false);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(true);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                GL11.glPopAttrib();
                GL11.glDepthRange(0.0, 1.0f);
                GL11.glPopMatrix();
            }
        }
    }


    public void onChorus() {
        if (mc.world.getEntityByID(mc.player.getEntityId()) != null) {
            final EntityPlayer entity = mc.player;
            if (entity != null) {
                fakeEntity = new EntityOtherPlayerMP(mc.world, entity.getGameProfile());
                fakeEntity.posX = xPos;
                fakeEntity.posY = yPos;
                fakeEntity.posZ = zPos;
                fakeEntity.rotationYawHead = entity.rotationYawHead;
                fakeEntity.prevRotationYawHead = entity.rotationYawHead;
                fakeEntity.rotationYaw = entity.rotationYaw;
                fakeEntity.prevRotationYaw = entity.rotationYaw;
                fakeEntity.rotationPitch = entity.rotationPitch;
                fakeEntity.prevRotationPitch = entity.rotationPitch;
                fakeEntity.cameraYaw = fakeEntity.rotationYaw;
                fakeEntity.cameraPitch = fakeEntity.rotationPitch;
                playerCham.put(fakeEntity, 255);
            }
        }
    }

    public void renderEntityStatic(Entity entityIn, float partialTicks, boolean p_188388_3_) {
        if (entityIn.ticksExisted == 0) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
        }
        double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
        double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
        double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
        float f = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
        int i = entityIn.getBrightnessForRender();
        if (entityIn.isBurning()) {
            i = 15728880;
        }
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        mc.getRenderManager().renderEntity(entityIn, d0 - mc.getRenderManager().viewerPosX, d1 - mc.getRenderManager().viewerPosY, d2 - mc.getRenderManager().viewerPosZ, f, partialTicks, p_188388_3_);
    }
}
