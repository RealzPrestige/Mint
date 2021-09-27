package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoCrystal extends Module {

    public static AutoCrystal INSTANCE;
    public Setting<Boolean> targetParent = register(new Setting("Target", true, false));
    public Setting<Float> targetRange = register(new Setting("Target Range", 10f, 0f, 15f, v-> targetParent.getValue()));

    public Setting<Boolean> rangesParent = register(new Setting("Ranges", true, false));
    public Setting<Float> placeRange = register(new Setting("Place Range", 5f, 0f, 6f, v-> rangesParent.getValue()));
    public Setting<Float> breakRange = register(new Setting("Break Range", 5f, 0f, 6f, v-> rangesParent.getValue()));

    public Setting<Boolean> swingParent = register(new Setting("Swing", true, false));
    public Setting<Boolean> placeSwing = register(new Setting("Place Swing", false, v-> swingParent.getValue()));
    public Setting<EnumHand> placeSwingMode = register(new Setting("Place Mode", EnumHand.MAIN_HAND, v-> placeSwing.getValue() && swingParent.getValue()));
    public Setting<Boolean> breakSwing = register(new Setting("Break Swing", false, v-> swingParent.getValue()));
    public Setting<EnumHand> breakSwingMode = register(new Setting("Break Mode", EnumHand.MAIN_HAND, v-> breakSwing.getValue() && swingParent.getValue()));

    public Setting<Boolean> packetParent = register(new Setting("Packet", true, false));
    public Setting<Boolean> packetBreak = register(new Setting("Packet Break", false, v-> packetParent.getValue()));

    public Setting<Boolean> rotateParent = register(new Setting("Rotations", true, false));
    public Setting<Boolean> rotate = register(new Setting("Rotate", false, v-> rotateParent.getValue()));
    public Setting<Boolean> placeRotate = register(new Setting("Place Rotate", false, v-> rotateParent.getValue() && rotate.getValue()));
    public Setting<Boolean> breakRotate = register(new Setting("Break Rotate", false, v-> rotateParent.getValue() && rotate.getValue()));

    public Setting<Boolean> switchParent = register(new Setting("Switch", true, false));
    public Setting<Boolean> silentSwitch = register(new Setting("Silent Switch", false, v-> switchParent.getValue()));

    public Setting<Boolean> predictParent = register(new Setting("Predict", true, false));
    public Setting<Boolean> breakPredict = register(new Setting("Break Predict", false, v-> predictParent.getValue()));
    public Setting<Boolean> soundPredict = register(new Setting("Sound Predict", false, v-> predictParent.getValue()));
    public Setting<Boolean> placePredict = register(new Setting("Place Predict", false, v-> predictParent.getValue()));

    public Setting<Boolean> damagesParent = register(new Setting("Damages", true, false));
    public Setting<Float> minDamage = register(new Setting("Min Damage", 6f, 0f, 12f, v-> damagesParent.getValue()));
    public Setting<Float> maxSelfDamage = register(new Setting("Max Self Damage", 8f, 0f, 12f, v-> damagesParent.getValue()));

    public Setting<Boolean> delayParent = register(new Setting("Delays", true, false));
    public Setting<Integer> placeDelay = register(new Setting("Place Delay", 0, 0, 200, v-> delayParent.getValue()));
    public Setting<Integer> breakDelay = register(new Setting("Break Delay", 65, 0, 200, v-> delayParent.getValue()));

    public Setting<Boolean> parentVisual = register(new Setting("Visual", true, false));
    public Setting<RenderMode> renderMode = register(new Setting("RenderMode", RenderMode.FADE, v-> parentVisual.getValue()));
    public enum RenderMode{STATIC, FADE}
    public Setting<Boolean> fadeParent = register(new Setting("Fade", false, true, v-> parentVisual.getValue()));
    public Setting<Integer> startAlpha = register(new Setting<>("StartAlpha", 255, 0, 255, v-> parentVisual.getValue() && fadeParent.getValue()));
    public Setting<Integer> endAlpha = register(new Setting<>("EndAlpha", 0, 0, 255, v-> parentVisual.getValue() && fadeParent.getValue()));
    public Setting<Integer> fadeStep = register(new Setting<>("FadeStep", 20, 10, 100, v-> parentVisual.getValue() && fadeParent.getValue()));
    public Setting<Boolean> boxParent = register(new Setting("Box", false, true, v-> parentVisual.getValue()));
    public Setting<Boolean> boxSetting = register(new Setting("BoxSetting", false, v-> boxParent.getValue() && parentVisual.getValue()));
    public Setting<Integer> boxRed = register(new Setting<>( "BoxRed", 255, 0, 255, v-> boxParent.getValue() && parentVisual.getValue()));
    public Setting<Integer> boxGreen = register(new Setting<>("BoxGreen", 255, 0, 255, v-> boxParent.getValue() && parentVisual.getValue()));
    public Setting<Integer> boxBlue = register(new Setting<>("BoxBlue", 255, 0, 255, v-> boxParent.getValue() && parentVisual.getValue()));
    public Setting<Integer> boxAlpha = register(new Setting<>("BoxAlpha", 120, 0, 255, v-> boxParent.getValue() && parentVisual.getValue()));
    public Setting<Boolean> outlineParent = register(new Setting("Outline", false, true, v-> parentVisual.getValue()));
    public Setting<Boolean> outlineSetting = register(new Setting("OutlineSetting", false, v-> outlineParent.getValue() && parentVisual.getValue()));
    public Setting<Integer> outlineRed = register(new Setting<>( "OutlineRed", 255, 0, 255, v-> outlineParent.getValue() && parentVisual.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("OutlineGreen", 255, 0, 255, v-> outlineParent.getValue() && parentVisual.getValue()));
    public Setting<Integer> outlineBlue = register(new Setting<>("OutlineBlue", 255, 0, 255, v-> outlineParent.getValue() && parentVisual.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("OutlineAlpha", 120, 0, 255, v-> outlineParent.getValue() && parentVisual.getValue()));

    Timer placeTimer = new Timer();
    Timer breakTimer = new Timer();
    BlockPos finalPos;
    BlockPos finalCrystalPos;
    BlockPos placePos;
    int crystals;
    EntityPlayer target;
    HashMap<BlockPos, Integer> renderPosses = new HashMap();
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private boolean rotating = false;

    public AutoCrystal(){
        super("Auto Crystal", Category.COMBAT, "");
    }

    public static AutoCrystal getInstance() {
        return AutoCrystal.INSTANCE;
    }

    public void onToggle(){
        mc.world.removeEntityFromWorld(crystals);
        crystals = 0;
    }

    @Override
    public void onUpdate() {
        target = EntityUtil.getTarget(targetRange.getValue());
        if (target != null) {
            if(placeTimer.passedMs(placeDelay.getValue())) {
                doPlace();
                placeTimer.reset();
            }
            if(breakTimer.passedMs(breakDelay.getValue()) && !breakPredict.getValue()) {
                doBreak();
                breakTimer.reset();
            }
        }
    }

    public void doPlace() {
        float maxDamage = 0.5f;
        final List<BlockPos> sphere = BlockUtil.getSphere(this.placeRange.getValue(), true);
        for (int size = sphere.size(), i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            final float self = EntityUtil.calculatePos(pos, mc.player);
            if (BlockUtil.canPlaceCrystal(pos, true)) {
                final float damage;
                if (EntityUtil.getHealth(mc.player) > self + 0.5f && this.maxSelfDamage.getValue() > self && (damage = EntityUtil.calculatePos(pos, EntityUtil.getTarget(targetRange.getValue()))) > maxDamage && damage > self && !EntityUtil.isPlayerSafe(target)) {
                    if (damage <= this.minDamage.getValue()) {
                        if (damage <= 2.0f) {
                            continue;
                        }
                    }
                    maxDamage = damage;
                    placePos = pos;
                    finalPos = placePos;
                }
            }
        }
        if (placePos != null) {
            int crystalSlot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
            int oldSlot = mc.player.inventory.currentItem;
            if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL || mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL){
                if (silentSwitch.getValue()){
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }else {
                    InventoryUtil.switchToSlot(crystalSlot);
                }
            }
            if(placeRotate.getValue()) {
                rotateToPos(placePos);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if (renderMode.getValue() == RenderMode.FADE){
                renderPosses.put(placePos, startAlpha.getValue());
            }
            if (mc.player.getHeldItemOffhand().getItem()!= Items.END_CRYSTAL){
                if (silentSwitch.getValue()){
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }
            }
            if(placeSwing.getValue()){
                mc.player.swingArm(placeSwingMode.getValue());
            }
            if(placePredict.getValue()) {
                Timer timers = new Timer();
                EntityEnderCrystal crystal = new EntityEnderCrystal(mc.world, (double) placePos.getX() + 0.5, (double) placePos.getY() + 1, (double) placePos.getZ() + 0.5);
                mc.world.addEntityToWorld(crystals, crystal);
                timers.reset();
                if (timers.passedMs(10)) {
                    mc.world.removeEntityFromWorld(crystals);
                    ++crystals;
                }
            }
        }
    }

    public void doBreak() {
        target = EntityUtil.getTarget(targetRange.getValue());
        for (Entity crystal : mc.world.loadedEntityList) {
            if (crystal instanceof EntityEnderCrystal) {

                if (crystal.getDistance(mc.player) > MathUtil.square(breakRange.getValue())) continue;

                if(breakRotate.getValue()) {
                    rotateTo(crystal);
                }

                if (packetBreak.getValue()) {
                    mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                } else {
                    mc.playerController.attackEntity(mc.player, crystal);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = event.getPacket();
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity entityCrystal : mc.world.loadedEntityList) {
                        if (entityCrystal instanceof EntityEnderCrystal) {
                            if (entityCrystal.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= breakRange.getValue() && soundPredict.getValue()) {
                                    entityCrystal.setDead();
                        }
                    }
                }
            }
        }
        if (event.getPacket() instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51 && finalPos != null && EntityUtil.getTarget(targetRange.getValue()) != null && breakPredict.getValue()) {
                final CPacketUseEntity predict = new CPacketUseEntity();
                predict.entityId = packet.getEntityID();
                predict.action = CPacketUseEntity.Action.ATTACK;
                finalCrystalPos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                if(predict.entityId != crystals) {
                    if(breakRotate.getValue()) {
                        rotateToPos(finalCrystalPos);
                    }
                        mc.getConnection().sendPacket(predict);
                        if (breakSwing.getValue()) {
                            mc.player.swingArm(breakSwingMode.getValue());
                    }
                }
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (renderMode.getValue() == RenderMode.FADE) {
            for (Map.Entry<BlockPos, Integer> entry : renderPosses.entrySet()) {
                renderPosses.put(entry.getKey(), entry.getValue() - (fadeStep.getValue() / 10));
                if (entry.getValue() <= endAlpha.getValue()) {
                    renderPosses.remove(entry.getKey());
                    return;
                }
                RenderUtil.drawBoxESP(entry.getKey(), new Color(boxRed.getValue(), boxGreen.getValue(), boxBlue.getValue(), entry.getValue()),true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), entry.getValue()), 0.1f, outlineSetting.getValue(), boxSetting.getValue(), entry.getValue(), true);
            }

        } else if (renderMode.getValue() == RenderMode.STATIC) {
            if (finalCrystalPos != null) {
                if (boxSetting.getValue() && finalCrystalPos != finalPos) {
                    RenderUtil.drawBoxESP(finalCrystalPos, new Color(255, 0, 0, boxAlpha.getValue()), true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), 0.1f, outlineSetting.getValue(), boxSetting.getValue(), boxAlpha.getValue(), false);
                }
            }
            if (finalPos != null) {
                if (boxSetting.getValue()) {
                    RenderUtil.drawBoxESP(finalPos, new Color(boxRed.getValue(), boxGreen.getValue(), boxBlue.getValue(), boxAlpha.getValue()), true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), 0.1f, outlineSetting.getValue(), boxSetting.getValue(), boxAlpha.getValue(), false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && rotate.getValue() && rotating && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = event.getPacket();
            packet.yaw = yaw;
            packet.pitch = pitch;
            rotating = false;
        }
    }
    private void rotateTo(Entity entity) {
        if (rotate.getValue()) {
            float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }

    private void rotateToPos(BlockPos pos) {
        if (rotate.getValue()) {
            float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() - 0.5f, (float) pos.getZ() + 0.5f));
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }
    }
}
