package mint.modules.visual;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.EntityUtil;
import mint.utils.PlayerUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class NameTags extends Module {
    private static NameTags INSTANCE = new NameTags();
    private final Setting<Integer> bottom = register(new Setting<>("Bottom", 0, 0, 20));
    private final Setting<Integer> y = register(new Setting<>("Y", 0, 0, 20));

    private final Setting<Boolean> boxParent = register(new Setting<>("Rect", true, false));
    private final Setting<Boolean> rect = register(new Setting("RectSetting", true, v-> boxParent.getValue()));
    private final Setting<Integer> rectRed = register(new Setting<>("RectRed", 0, 0, 255, v -> rect.getValue() && boxParent.getValue()));
    private final Setting<Integer> rectGreen = register(new Setting<>("RectGreen", 0, 0, 255, v -> rect.getValue() && boxParent.getValue()));
    private final Setting<Integer> rectBlue = register(new Setting<>("RectBlue", 0, 0, 255, v -> rect.getValue() && boxParent.getValue()));
    private final Setting<Integer> rectAlpha = register(new Setting<>("RectAlpha", 50, 0, 255, v -> rect.getValue() && boxParent.getValue()));
    private final Setting<Boolean> healthLine = register(new Setting("HealthLine", true));

    public NameTags() {
        super("Nametags", Category.VISUAL, "Draws info about an entity above their head.");
        this.setInstance();
    }

    public static NameTags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameTags();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (!NameTags.fullNullCheck()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (player == null || player.equals(mc.player) || !player.isEntityAlive() || player.isInvisible() && !EntityUtil.isInFov(player))
                    continue;
                double x = this.interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
                double y = this.interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
                double z = this.interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
                this.renderNameTag(player, x, y, z, event.getPartialTicks());
            }
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += player.isSneaking() ? 0.5 : 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = renderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + (double) 10 * (distance * 0.3)) / 1000.0;
        if (distance <= 8.0) {
            scale = 0.0245;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if(rect.getValue()) {
            RenderUtil.drawRect(-width - 1, -9, (float) width + 2.0f, 0.5f, ColorUtil.toRGBA(rectRed.getValue(), rectGreen.getValue(), rectBlue.getValue(), rectAlpha.getValue()));
        }
        if (healthLine.getValue()){
            final float healthAmount = player.getHealth() + player.getAbsorptionAmount();
            final int lineColor = (healthAmount >= 33) ? ColorUtil.toRGBA(0, 255, 0, 255) : (healthAmount >= 30) ? ColorUtil.toRGBA(150, 255, 0, 255) : ((healthAmount > 25) ? ColorUtil.toRGBA(75, 255, 0, 255) : ((healthAmount > 20) ? ColorUtil.toRGBA(255, 255, 0, 255) : ((healthAmount > 15) ? ColorUtil.toRGBA(255, 200, 0, 255) : ((healthAmount > 10) ? ColorUtil.toRGBA(255, 150, 0, 255) : ((healthAmount > 5) ? ColorUtil.toRGBA(255, 50, 0, 255) : ColorUtil.toRGBA(255, 0, 0, 255))))));
            RenderUtil.drawGradientRect(-width - 1, -(mc.fontRenderer.FONT_HEIGHT -8), (float) width + 2.0f + healthAmount * -2.0f, 0, lineColor, lineColor);
        }
        GlStateManager.disableBlend();
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
            renderMainHand.stackSize = 1;
        }
        GL11.glPushMatrix();
        GL11.glScalef(0.75f, 0.75f, 0.0f);
        GL11.glScalef(1.5f, 1.5f, 1.0f);
        GL11.glPopMatrix();
        GlStateManager.pushMatrix();
        int xOffset = -8;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null) continue;
            xOffset -= 8;
        }
        xOffset -= 8;
        ItemStack renderOffhand = player.getHeldItemOffhand().copy();
        if (renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
            renderOffhand.stackSize = 1;
        }
        this.renderItemStack(renderOffhand, xOffset);
        xOffset += 16;
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null) continue;
            ItemStack armourStack = stack.copy();
            if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
                armourStack.stackSize = 1;
            }
            this.renderItemStack(armourStack, xOffset);
            xOffset += 16;
        }
        this.renderItemStack(renderMainHand, xOffset);
        GlStateManager.popMatrix();
        this.renderer.drawStringWithShadow(displayTag, -width, -10, Mint.friendManager.isFriend(player) ? ColorUtil.toRGBA(0, 191, 255) : -1);
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -28);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, -28);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        this.renderEnchantmentText(stack, x);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x) {
        if (PlayerUtil.hasDurability(stack)) {
            int percent = PlayerUtil.getRoundedDamage(stack);
            String color = percent >= 60 ? "\u00a7a" : (percent >= 25 ? "\u00a7e" : "\u00a7c");
            this.renderer.drawStringWithShadow(color + percent + "%", x * 2, -28, -1);
        }
    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        float health = Math.round(EntityUtil.getHealth(player));
        String color = health > 18.0f ? "\u00a7a" : (health > 16.0f ? "\u00a72" : (health > 12.0f ? "\u00a7e" : (health > 8.0f ? "\u00a76" : (health > 5.0f ? "\u00a7c" : "\u00a74"))));
        String pingStr = "";
        try {
            int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
            pingStr = pingStr + responseTime + "ms ";
        } catch (Exception ignored) {
        }
        return name + " " +  pingStr + color + health;
    }


    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }
}