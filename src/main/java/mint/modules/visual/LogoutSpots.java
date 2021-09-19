package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.commands.Command;
import mint.events.ConnectionEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.MathUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public
class LogoutSpots extends Module {
    private final Setting<Model> model = this.register(new Setting("RenderModel", Model.Player));

    private enum Model {Player, Box}

    private final Setting<Integer> red = this.register(new Setting<>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<>("Green", 0, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<>("Blue", 0, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<>("Alpha", 255, 0, 255));
    private final Setting<Boolean> scaleing = this.register(new Setting<>("Scale", false));
    private final Setting<Float> scaling = this.register(new Setting<>("Size", 4.0f, 0.1f, 20.0f));
    private final Setting<Float> factor = this.register(new Setting<Object>("Factor", 0.3f, 0.1f, 1.0f, v -> this.scaleing.getValue()));
    private final Setting<Boolean> smartScale = this.register(new Setting<Object>("SmartScale", Boolean.FALSE, v -> this.scaleing.getValue()));
    private final Setting<Boolean> rect = this.register(new Setting<>("Rectangle", true));
    private final Setting<Boolean> coords = this.register(new Setting<>("Coords", true));
    private final List<LogoutPos> spots = new CopyOnWriteArrayList<>();
    public Setting<Float> range = this.register(new Setting<>("Range", 300.0f, 50.0f, 500.0f));
    public Setting<Boolean> message = this.register(new Setting<>("Message", false));

    public LogoutSpots() {
        super("LogoutSpots", Category.VISUAL, "render logout spots.");
    }

    @Override
    public void onLogout() {
        this.spots.clear();
    }

    @Override
    public void onDisable() {
        this.spots.clear();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!this.spots.isEmpty()) {
            synchronized (this.spots) {
                this.spots.forEach(spot -> {
                    if (spot.getEntity() != null) {
                        AxisAlignedBB bb = RenderUtil.interpolateAxis(spot.getEntity().getEntityBoundingBox());
                        if (model.getValue() == Model.Player) {
                            PopChams.RenderTesselator.prepareGL();
                            GlStateManager.pushMatrix();
                            GL11.glPushAttrib(1048575);
                            GL11.glPolygonMode(1032, 6913);
                            glDisable(3553);
                            glDisable(2896);
                            glDisable(2929);
                            glEnable(2848);
                            glEnable(3042);
                            GL11.glBlendFunc(770, 771);
                            GL11.glColor4f(red.getValue() / 255.0f, green.getValue() / 255.0f, blue.getValue() / 255.0f, alpha.getValue() / 255.0f);
                            mc.getRenderManager().renderEntityStatic(spot.getEntity(), event.getPartialTicks(), false);
                            glEnable(2896);
                            GlStateManager.popAttrib();
                            GlStateManager.popMatrix();
                            PopChams.RenderTesselator.releaseGL();
                        } else {
                            RenderUtil.drawBlockOutline(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                        }
                        double x = this.interpolate(spot.getEntity().lastTickPosX, spot.getEntity().posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
                        double y = this.interpolate(spot.getEntity().lastTickPosY, spot.getEntity().posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
                        double z = this.interpolate(spot.getEntity().lastTickPosZ, spot.getEntity().posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
                        this.renderNameTag(spot.getName(), x, y, z, event.getPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
                    }
                });
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!LogoutSpots.fullNullCheck()) {
            this.spots.removeIf(spot -> mc.player.getDistanceSq(spot.getEntity()) >= MathUtil.square(this.range.getValue()));
        }
    }

    @SubscribeEvent
    public void onConnection(ConnectionEvent event) {
        if (event.getStage() == 0) {
            UUID uuid = event.getUuid();
            EntityPlayer entity = mc.world.getPlayerEntityByUUID(uuid);
            if (entity != null && this.message.getValue()) {
                Command.sendMessage("\u00a7a" + entity.getName() + " just logged in" + (this.coords.getValue() ? " at (" + (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ + ")!" : "!"));
            }
            this.spots.removeIf(pos -> pos.getName().equalsIgnoreCase(event.getName()));
        } else if (event.getStage() == 1) {
            EntityPlayer entity = event.getEntity();
            UUID uuid = event.getUuid();
            String name = event.getName();
            if (this.message.getValue()) {
                Command.sendMessage("\u00a7c" + event.getName() + " just logged out" + (this.coords.getValue() ? " at (" + (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ + ")!" : "!"));
            }
            if (name != null && entity != null && uuid != null) {
                this.spots.add(new LogoutPos(name, uuid, entity));
            }
        }
    }

    private void renderNameTag(String name, double x, double yi, double z, float delta, double xPos, double yPos, double zPos) {
        double y = yi + 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = name + " XYZ: " + (int) xPos + ", " + (int) yPos + ", " + (int) zPos;
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = this.renderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + (double) this.scaling.getValue() * (distance * (double) this.factor.getValue())) / 1000.0;
        if (distance <= 8.0 && this.smartScale.getValue()) {
            scale = 0.0245;
        }
        if (!this.scaleing.getValue()) {
            scale = (double) this.scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (this.rect.getValue()) {
            RenderUtil.drawRect(-width - 2, -(this.renderer.getFontHeight() + 1), (float) width + 2.0f, 1.5f, 0x55000000);
        }
        GlStateManager.disableBlend();
        this.renderer.drawStringWithShadow(displayTag, -width, -(this.renderer.getFontHeight() - 1), ColorUtil.toRGBA(new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue())));
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }

    private static
    class LogoutPos {
        private final String name;
        private final UUID uuid;
        private final EntityPlayer entity;
        private final double x;
        private final double y;
        private final double z;

        public LogoutPos(String name, UUID uuid, EntityPlayer entity) {
            this.name = name;
            this.uuid = uuid;
            this.entity = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
        }

        public String getName() {
            return this.name;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public EntityPlayer getEntity() {
            return this.entity;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }
}

