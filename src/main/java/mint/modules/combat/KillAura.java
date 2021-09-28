package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.InventoryUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", Category.COMBAT, "Automatically attacks players.");
    }

    public Setting<Boolean> switchToSword = register(new Setting<>("SwitchToSword", true));
    public Setting<Integer> range = register(new Setting<>("Range", 4, 1, 6));
    public Setting<Boolean> delayParent = register(new Setting<>("Delay", true, false));
    public Setting<Boolean> attackDelay = register(new Setting("AttackDelay", true, v -> delayParent.getValue()));
    public Setting<Integer> attackSpeed = register(new Setting("AttackSpeed", 10, 2, 18, v -> delayParent.getValue()));
    public Setting<Boolean> targetParent = register(new Setting("Targets", true, false));
    public Setting<Boolean> players = register(new Setting("Players", true, v -> targetParent.getValue()));
    public Setting<Boolean> mobs = register(new Setting("Mobs", true, v -> targetParent.getValue()));
    public Setting<Boolean> animals = register(new Setting("Animals", true, v -> targetParent.getValue()));
    public Setting<Boolean> renderParent = register(new Setting<>("Render", true, false));
    public Setting<Boolean> render = register(new Setting("Render", true, v -> renderParent.getValue()));
    public Setting<RenderMode> renderMode = register(new Setting("RenderMode", RenderMode.BOTH, v-> renderParent.getValue() && render.getValue()));
    public enum RenderMode{BOTH, OUTLINE, FILL}
    public Setting<Integer> r = register(new Setting("R", 255, 0, 255, v -> renderParent.getValue() && render.getValue()));
    public Setting<Integer> g = register(new Setting("G", 255, 0, 255, v -> renderParent.getValue() && render.getValue()));
    public Setting<Integer> b = register(new Setting("B", 255, 0, 255, v -> renderParent.getValue() && render.getValue()));
    public Setting<Integer> a = register(new Setting("A", 125, 0, 255, v -> renderParent.getValue() && render.getValue()));
    public Setting<Integer> lineWidth = register(new Setting("LineWidth", 1, 0, 3, v -> renderParent.getValue() && render.getValue()));
    public Setting<Boolean> rainbow = register(new Setting("Rainbow", true, v -> renderParent.getValue() && render.getValue()));

    public Entity target = null;

    public void onLogin() {
        disable();
    }

    @Override
    public void onUpdate() {
        for (Entity e : mc.world.loadedEntityList) {
            int swordSlot = InventoryUtil.getItemSlot(Items.DIAMOND_SWORD);
            if (shouldAttack(e)) {
                if (swordSlot != -1 && switchToSword.getValue() && mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD)
                    InventoryUtil.switchToSlot(swordSlot);
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
                    if (attackDelay.getValue()) {
                        if (mc.player.getCooledAttackStrength(0.0f) >= 1.0f) {
                            mc.playerController.attackEntity(mc.player, e);
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            target = e;
                        }
                    } else {
                        if (mc.player.ticksExisted % attackSpeed.getValue() == 0.0) {
                            mc.playerController.attackEntity(mc.player, e);
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            target = e;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        boolean fill = false;
        boolean outline = false;
        if(renderMode.getValue().equals(RenderMode.BOTH)) {
            fill = true;
            outline = true;
        }else if(renderMode.getValue().equals(RenderMode.FILL)) {
            fill = true;
            outline = false;
        }else if(renderMode.getValue().equals(RenderMode.OUTLINE)) {
            fill = false;
            outline = true;
        }
        if (render.getValue() && target != null) {
            double x = (target.posX - target.lastTickPosX) * event.getPartialTicks();
            double y = (target.posY - target.lastTickPosY) * event.getPartialTicks();
            double z = (target.posZ - target.lastTickPosZ) * event.getPartialTicks();
            AxisAlignedBB bb = target.getEntityBoundingBox().offset(-mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ);
            RenderUtil.prepare();
            if (fill)
                RenderGlobal.renderFilledBox(bb, (rainbow.getValue() ? ColorUtil.rainbow(6).getRed() : r.getValue()) / 255f, (rainbow.getValue() ? ColorUtil.rainbow(6).getGreen() : g.getValue()) / 255f, (rainbow.getValue() ? ColorUtil.rainbow(6).getBlue() : b.getValue()) / 255f, a.getValue() / 255f);
            if (outline) {
                GlStateManager.glLineWidth(lineWidth.getValue());
                RenderGlobal.drawSelectionBoundingBox(bb, (rainbow.getValue() ? ColorUtil.rainbow(6).getRed() : r.getValue()) / 255f, (rainbow.getValue() ? ColorUtil.rainbow(6).getGreen() : g.getValue()) / 255f, (rainbow.getValue() ? ColorUtil.rainbow(6).getBlue() : b.getValue()) / 255f, a.getValue() / 255f);
            }
            RenderUtil.release();
        }
    }

    @Override
    public void onDisable() {
        target = null;
    }

    public boolean shouldAttack(Entity entity) {
        if (entity.equals(mc.player)) return false;
        if (!(entity instanceof EntityLivingBase)) return false;
        if (entity.isDead || !entity.isEntityAlive() || ((EntityLivingBase) entity).getHealth() < 0) return false;
        if ((entity instanceof EntityPlayer) && !players.getValue()) return false;
        if ((entity instanceof EntityMob || entity instanceof EntitySlime) && !mobs.getValue()) return false;
        if ((entity instanceof EntityAnimal) && !animals.getValue()) return false;
        return entity.getDistance(mc.player) <= range.getValue();
    }
}