package mint.modules.combat;

import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.events.CrystalAttackEvent;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author zPrestige_
 * @since 05/10/21
 * @author kambing for obiassist
 */
@SuppressWarnings("All")
public class AutoCrystal extends Module {

    public static AutoCrystal INSTANCE = new AutoCrystal();

    public Setting<Boolean> rangesParent = register(new Setting<>("Ranges", true, false));
    public Setting<Float> placeRange = register(new Setting<>("Place Range", 5f, 0f, 6f, v -> rangesParent.getValue()));
    public Setting<Float> breakRange = register(new Setting<>("Break Range", 5f, 0f, 6f, v -> rangesParent.getValue()));
    public Setting<Float> targetRange = register(new Setting<>("Target Range", 10f, 0f, 15f, v -> rangesParent.getValue()));

    public Setting<Boolean> damagesParent = register(new Setting<>("Damages", true, false));
    public Setting<Float> minimumDamage = register(new Setting<>("Minimum Damage", 6f, 0f, 12f, v -> damagesParent.getValue()));
    public Setting<Float> maximumSelfDamage = register(new Setting<>("Maximum Self Damage", 8f, 0f, 12f, v -> damagesParent.getValue()));
    public Setting<Boolean> antiSuicide = register(new Setting<>("Anti Suicide", false, false, v -> damagesParent.getValue()));

    public Setting<Boolean> predictParent = register(new Setting<>("Predicts", true, false));
    public Setting<Boolean> soundPredict = register(new Setting<>("Sound Predict", false, false, v -> predictParent.getValue()));
    public Setting<Boolean> placePredict = register(new Setting<>("Place Predict", false, false, v -> predictParent.getValue()));
    public Setting<Boolean> breakPredict = register(new Setting<>("Break Predict", false, false, v -> predictParent.getValue()));
    public Setting<Boolean> breakPredictCalc = register(new Setting<>("Break Predict Calc", false, false, v -> predictParent.getValue() && breakPredict.getValue()));

    public Setting<Boolean> delayParent = register(new Setting<>("Delays", true, false));
    public Setting<Integer> placeDelay = register(new Setting<>("Place Delay", 100, 0, 500, v -> delayParent.getValue()));
    public Setting<Integer> breakDelay = register(new Setting<>("Break Delay", 100, 0, 500, v -> delayParent.getValue()));

    public Setting<Boolean> miscParent = register(new Setting<>("Misc", true, false));
    public Setting<Boolean> updatedPlacements = register(new Setting<>("1.13+ Placements", false, false, v -> miscParent.getValue()));
    public Setting<Boolean> limitAttack = register(new Setting<>("Limit Attack", false, false, v -> miscParent.getValue()));
    public Setting<Boolean> packetBreak = register(new Setting<>("Packet Break", false, false, v -> miscParent.getValue()));
    public Setting<Boolean> allowCollision = register(new Setting<>("Allow Collision", false, false, v -> miscParent.getValue()));
    public Setting<Boolean> cancelVelocity = register(new Setting<>("Cancel Velocity", false, false, v -> miscParent.getValue()));
    public Setting<Boolean> cancelExplosion = register(new Setting<>("Cancel Explosion", false, false, v -> miscParent.getValue()));
    public Setting<Boolean> silentSwitch = register(new Setting<>("Silent Switch", false, false, v -> miscParent.getValue()));
    public Setting<Boolean> antiWeakness = register(new Setting<>("Anti Weakness", false, false, v -> silentSwitch.getValue() && miscParent.getValue()));
    public Setting<Boolean> swingParent = register(new Setting<>("Swings", true, false));
    public Setting<Boolean> placeSwing = register(new Setting<>("Place Swing", false, false, v -> swingParent.getValue()));
    public Setting<PlaceSwingHand> placeSwingHand = register(new Setting<>("PlaceSwingHand", PlaceSwingHand.MAINHAND, v -> placeSwing.getValue() && swingParent.getValue()));

    public enum PlaceSwingHand {MAINHAND, OFFHAND}

    public Setting<Boolean> breakSwing = register(new Setting<>("Break Swing", false, false, v -> swingParent.getValue()));
    public Setting<BreakSwingHand> breakSwingHand = register(new Setting<>("BreakSwingHand", BreakSwingHand.MAINHAND, v -> breakSwing.getValue() && swingParent.getValue()));

    public enum BreakSwingHand {MAINHAND, OFFHAND}

    public Setting<Boolean> facePlaceParent = register(new Setting<>("Face Placing", true, false));
    public Setting<FacePlaceMode> facePlaceMode = register(new Setting<>("FacePlaceMode", FacePlaceMode.Never, v -> facePlaceParent.getValue()));

    public enum FacePlaceMode {Never, Health, Bind, Always}

    public Setting<Float> facePlaceHp = register(new Setting<>("Face Place Delay", 15, 0, 36, v -> facePlaceMode.getValue().equals(FacePlaceMode.Health) && facePlaceParent.getValue()));
    public Setting<Bind> facePlaceBind = register(new Setting<>("Face Place Bind", new Bind(-1), v -> facePlaceMode.getValue().equals(FacePlaceMode.Bind) && facePlaceParent.getValue()));

    public Setting<Boolean> renderParent = register(new Setting<>("Renders", true, false));
    public Setting<Boolean> render = register(new Setting<>("Render", false, false, v -> renderParent.getValue()));
    public Setting<Boolean> fade = register(new Setting<>("Fade", false, false, v -> render.getValue() && renderParent.getValue()));
    public Setting<Integer> startAlpha = register(new Setting<>("Start Alpha", 255, 0, 255, v -> render.getValue() && fade.getValue() && renderParent.getValue()));
    public Setting<Integer> endAlpha = register(new Setting<>("End Alpha", 0, 0, 255, v -> render.getValue() && fade.getValue() && renderParent.getValue()));
    public Setting<Integer> fadeSpeed = register(new Setting<>("Fade Speed", 20, 0, 100, v -> render.getValue() && fade.getValue() && renderParent.getValue()));

    public Setting<Boolean> box = register(new Setting<>("Box", false, false, v -> render.getValue() && renderParent.getValue()));
    public Setting<Integer> boxRed = register(new Setting<>("Box Red", 255, 0, 255, v -> render.getValue() && box.getValue() && renderParent.getValue()));
    public Setting<Integer> boxGreen = register(new Setting<>("Box Green", 255, 0, 255, v -> render.getValue() && box.getValue() && renderParent.getValue()));
    public Setting<Integer> boxBlue = register(new Setting<>("Box Blue", 255, 0, 255, v -> render.getValue() && box.getValue() && renderParent.getValue()));
    public Setting<Integer> boxAlpha = register(new Setting<>("Box Alpha", 255, 0, 255, v -> render.getValue() && box.getValue() && renderParent.getValue()));

    public Setting<Boolean> outline = register(new Setting<>("Outline", false, false, v -> render.getValue() && renderParent.getValue()));
    public Setting<Integer> outlineRed = register(new Setting<>("Outline Red", 255, 0, 255, v -> render.getValue() && outline.getValue() && renderParent.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("Outline Green", 255, 0, 255, v -> render.getValue() && outline.getValue() && renderParent.getValue()));
    public Setting<Integer> outlineBlue = register(new Setting<>("Outline Blue", 255, 0, 255, v -> render.getValue() && outline.getValue() && renderParent.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("Outline Alpha", 255, 0, 255, v -> render.getValue() && outline.getValue() && renderParent.getValue()));
    public Setting<Float> lineWidth = register(new Setting<>("Line Width", 1f, 0f, 5f, v -> render.getValue() && outline.getValue() && renderParent.getValue()));

    public Setting<Boolean> obiAssistParent = register(new Setting<>("Assist", true, false));
    private final Setting<Boolean> packet = register(new Setting<>("PacketSwitch", true));
    private final Setting<Double> range = register(new Setting<>("TargetMaxRange", 10.0, 5.0, 15.0));
    private final Setting<Integer> delay = register(new Setting<>("MSDelay", 200, 0, 500));

    EntityPlayer targetPlayer;
    BlockPos finalPos;
    Timer delayTimer = new Timer();
    Timer placeTimer = new Timer();
    Timer breakTimer = new Timer();
    HashMap<BlockPos, Integer> possesToFade = new HashMap();
    bestPlacePos bestCrystalPos = new bestPlacePos(BlockPos.ORIGIN, 0);
    HashMap<Integer, Entity> attemptedEntityId = new HashMap();

    float mainTargetDamage;
    float mainTargetHealth;
    float mainMinimumDamageValue;
    float mainSelfHealth;
    float mainSelfDamage;
    int mainSlot;
    int mainOldSlot;

    public AutoCrystal() {
        super("Auto Crystal", Category.COMBAT, "Obliterates kids with end crystals ezz");
        this.setInstance();
    }

    public static AutoCrystal getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoCrystal();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onUpdate() {
        if (fullNullCheck())
            return;

        targetPlayer = EntityUtil.getTarget(targetRange.getValue());

        if (targetPlayer == null)
            return;

        if (placeTimer.passedMs((long) placeDelay.getValue())) {
            doPlace();
            placeTimer.reset();
        }
        if (breakTimer.passedMs((long) breakDelay.getValue())) {
            doBreak();
            breakTimer.reset();
        }
    }

    void doPlace() {

        int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int oldSlot = mc.player.inventory.currentItem;
        mainSlot = slot;
        mainOldSlot = oldSlot;

        bestCrystalPos = getBestPlacePos();

        if (bestCrystalPos == null)
            return;

        if (silentSwitch.getValue() && (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL))
            InventoryUtil.switchToSlot(slot);

        mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(bestCrystalPos.getBlockPos(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

        finalPos = bestCrystalPos.getBlockPos();

        if (render.getValue() && fade.getValue())
            possesToFade.put(bestCrystalPos.getBlockPos(), startAlpha.getValue());

        if (placeSwing.getValue())
            mc.player.swingArm(placeSwingHand.getValue().equals(PlaceSwingHand.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);

        if (silentSwitch.getValue() && (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL || mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL)) {
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        }
    }

    void doBreak() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal) {
                float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                float selfDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, mc.player);
                float targetDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, targetPlayer);
                float targetHealth = targetPlayer.getHealth() + targetPlayer.getAbsorptionAmount();
                float minimumDamageValue = minimumDamage.getValue();
                int sword = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                int oldSlot = mc.player.inventory.currentItem;

                if (mc.player.getDistance(entity.posX + 0.5f, entity.posY + 1, entity.posZ + 0.5f) > MathUtil.square(breakRange.getValue()))
                    continue;

                if (BlockUtil.isPlayerSafe(targetPlayer) && (facePlaceMode.getValue().equals(FacePlaceMode.Always) || (facePlaceMode.getValue().equals(FacePlaceMode.Health) && targetHealth < facePlaceHp.getValue()) || (facePlaceMode.getValue().equals(FacePlaceMode.Bind) && Keyboard.isKeyDown(facePlaceBind.getValue().getKey()))))
                    minimumDamageValue = 2;

                if (antiSuicide.getValue() && selfDamage > selfHealth)
                    continue;

                if (selfDamage > maximumSelfDamage.getValue())
                    continue;

                if (targetDamage < minimumDamageValue)
                    continue;

                if (limitAttack.getValue() && attemptedEntityId.containsValue(entity)) {
                    MessageManager.sendMessage("Limited Attack");
                    continue;
                }

                if (silentSwitch.getValue() && antiWeakness.getValue() && (mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD) && mc.player.getActivePotionEffects().equals(Potion.getPotionById(18)))
                    InventoryUtil.switchToSlot(sword);

                if (packetBreak.getValue()) {
                    mc.getConnection().sendPacket(new CPacketUseEntity(entity));
                } else {
                    mc.playerController.attackEntity(mc.player, entity);
                }

                CrystalAttackEvent event = new CrystalAttackEvent(entity.getEntityId(), entity);
                MinecraftForge.EVENT_BUS.post(event);

                if (breakSwing.getValue())
                    mc.player.swingArm(breakSwingHand.getValue().equals(BreakSwingHand.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);

                if (silentSwitch.getValue() && antiWeakness.getValue() && (mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD) && mc.player.getActivePotionEffects().equals(Potion.getPotionById(18))) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }
            }
        }
    }

    void doObiAssist() {
        EntityPlayer target = AutoCrystal.getInstance().targetPlayer;
        int slot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int old = mc.player.inventory.currentItem;
        EnumHand hand = null;

        if (target == null)
            return;

        if (AutoCrystal.getInstance().finalPos != null)
            return;


            if (slot != -1) {

                if (delayTimer.passedMs(delay.getValue())) {
                    if (mc.player.inventory.currentItem != slot) {
                        if (packet.getValue()) {
                            if (mc.player.isHandActive()) {
                                hand = mc.player.getActiveHand();
                            }
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                        }
                    }
                }
        }
        try {
            if (!(target.getDistance(mc.player) > range.getValue())) {

                AutoCrystal.bestPlacePos bestPlacePos;
                bestPlacePos = AutoCrystal.getInstance().getBestPlacePos();
                BlockPos pos = bestPlacePos.getBlockPos();


                if (!isValidToJoinDaGang(pos)) return;
                if (!canPlaceCrystalIfObbyWasAtPos(pos)) return;


                BlockUtil.placeBlock(pos);
                delayTimer.reset();
                if (packet.getValue()) {
                    if (slot != -1) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(old));
                        if (packet.getValue() && hand != null) {
                            mc.player.setActiveHand(hand);
                        }
                    }
                }
            }

        } catch (NullPointerException ignored) {
            //to avoid ticking entity crash
        }
    }


    public bestPlacePos getBestPlacePos() {
        TreeMap<Float, bestPlacePos> posList = new TreeMap<>();
        for (BlockPos pos : BlockUtil.getSphere(placeRange.getValue())) {
            float targetDamage = EntityUtil.calculatePosDamage(pos, targetPlayer);
            float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            float selfDamage = EntityUtil.calculatePosDamage(pos, mc.player);
            float targetHealth = targetPlayer.getHealth() + targetPlayer.getAbsorptionAmount();
            float minimumDamageValue = minimumDamage.getValue();
            mainTargetDamage = targetDamage;
            mainTargetHealth = targetHealth;
            mainSelfDamage = selfDamage;
            mainSelfHealth = selfHealth;
            mainMinimumDamageValue = minimumDamageValue;
            if (BlockUtil.isPosValidForCrystal(pos, updatedPlacements.getValue())) {
                if (mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f) > MathUtil.square(placeRange.getValue()))
                    continue;

                if (!allowCollision.getValue() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty())
                    continue;

                if (BlockUtil.isPlayerSafe(targetPlayer) && (facePlaceMode.getValue().equals(FacePlaceMode.Always) || (facePlaceMode.getValue().equals(FacePlaceMode.Health) && targetHealth < facePlaceHp.getValue()) || (facePlaceMode.getValue().equals(FacePlaceMode.Bind) && Keyboard.isKeyDown(facePlaceBind.getValue().getKey()))))
                    minimumDamageValue = 2;

                if (antiSuicide.getValue() && selfDamage > selfHealth)
                    continue;

                if (selfDamage > maximumSelfDamage.getValue())
                    continue;

                if (targetDamage < minimumDamageValue)
                    continue;

                posList.put(targetDamage, new bestPlacePos(pos, targetDamage));
            }
        }
        if (!posList.isEmpty()) {
            return posList.lastEntry().getValue();
        }
        return null;
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (cancelExplosion.getValue() && event.getPacket() instanceof SPacketExplosion)
            event.setCanceled(true);

        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velocity = event.getPacket();

            if (cancelVelocity.getValue() && velocity.getEntityID() == mc.player.getEntityId())
                event.setCanceled(true);
        }
        if (breakPredict.getValue() && event.getPacket() instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51 && finalPos != null && EntityUtil.getTarget(targetRange.getValue()) != null) {
                final CPacketUseEntity predict = new CPacketUseEntity();
                predict.entityId = packet.getEntityID();
                predict.action = CPacketUseEntity.Action.ATTACK;

                if (breakPredictCalc.getValue()) {
                    if (mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) > MathUtil.square(breakRange.getValue()))
                        return;

                    if (BlockUtil.isPlayerSafe(targetPlayer) && (facePlaceMode.getValue().equals(FacePlaceMode.Always) || (facePlaceMode.getValue().equals(FacePlaceMode.Health) && mainTargetHealth < facePlaceHp.getValue()) || (facePlaceMode.getValue().equals(FacePlaceMode.Bind) && Keyboard.isKeyDown(facePlaceBind.getValue().getKey()))))
                        mainMinimumDamageValue = 2;

                    if (antiSuicide.getValue() && mainSelfDamage > mainSelfHealth)
                        return;

                    if (mainSelfDamage > maximumSelfDamage.getValue())
                        return;

                    if (mainMinimumDamageValue > mainTargetDamage)
                        return;
                }

                mc.getConnection().sendPacket(predict);
                if (breakSwing.getValue()) {
                    mc.player.swingArm(breakSwingHand.getValue().equals(BreakSwingHand.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                }
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal) {

                        if (limitAttack.getValue() && attemptedEntityId.containsValue(entity.getEntityId()))
                            attemptedEntityId.remove(entity, entity.getEntityId());

                        BlockPos predictedCrystalPos = new BlockPos(entity.posX, entity.posY - 1, entity.posZ);

                        if (soundPredict.getValue() && entity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= breakRange.getValue())
                            entity.setDead();

                        if (placePredict.getValue() && predictedCrystalPos.equals(bestCrystalPos)) {

                            if (entity.getDistance(mc.player) > MathUtil.square(placeRange.getValue()))
                                continue;

                            if (BlockUtil.isPlayerSafe(targetPlayer) && (facePlaceMode.getValue().equals(FacePlaceMode.Always) || (facePlaceMode.getValue().equals(FacePlaceMode.Health) && mainTargetHealth < facePlaceHp.getValue()) || (facePlaceMode.getValue().equals(FacePlaceMode.Bind) && Keyboard.isKeyDown(facePlaceBind.getValue().getKey()))))
                                mainMinimumDamageValue = 2;

                            if (antiSuicide.getValue() && mainSelfDamage > mainSelfHealth)
                                continue;

                            if (mainSelfDamage > maximumSelfDamage.getValue())
                                continue;

                            if (mainMinimumDamageValue > mainTargetDamage)
                                continue;

                            if (limitAttack.getValue() && attemptedEntityId.containsValue(entity)) {
                                MessageManager.sendMessage("Limited Attack");
                                continue;
                            }

                            if (silentSwitch.getValue() && (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL || mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL))
                                InventoryUtil.switchToSlot(mainSlot);

                            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(predictedCrystalPos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                            if (render.getValue() && fade.getValue())
                                possesToFade.put(predictedCrystalPos, startAlpha.getValue());

                            if (placeSwing.getValue())
                                mc.player.swingArm(placeSwingHand.getValue().equals(PlaceSwingHand.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);

                            if (silentSwitch.getValue() && (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL || mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL)) {
                                mc.player.inventory.currentItem = mainOldSlot;
                                mc.playerController.updateController();
                            }
                        }
                    }
                }
            }
        }
    }

    public void onEnable() {
        targetPlayer = null;
        finalPos = null;
    }

    public void onDisable() {
        delayTimer.reset();
        targetPlayer = null;
        finalPos = null;
    }

    @SubscribeEvent
    public void onCrystalAttacked(CrystalAttackEvent event) {
        if (limitAttack.getValue())
            attemptedEntityId.put(event.getEntityId(), event.getEntity());
    }

    public void onRender3D(Render3DEvent event) {
        if (render.getValue()) {
            if (fade.getValue()) {
                for (Map.Entry<BlockPos, Integer> entry : possesToFade.entrySet()) {
                    possesToFade.put(entry.getKey(), entry.getValue() - (fadeSpeed.getValue() / 10));
                    if (entry.getValue() <= endAlpha.getValue()) {
                        possesToFade.remove(entry.getKey());
                        return;
                    }
                    RenderUtil.drawBoxESP(entry.getKey(), new Color(boxRed.getValue() / 255f, boxGreen.getValue() / 255f, boxBlue.getValue() / 255f, entry.getValue() / 255f), true, new Color(outlineRed.getValue() / 255f, outlineGreen.getValue() / 255f, outlineBlue.getValue() / 255f, entry.getValue() / 255f), lineWidth.getValue(), outline.getValue(), box.getValue(), entry.getValue(), true);
                }
            } else if (finalPos != null) {
                RenderUtil.drawBoxESP(finalPos, new Color(boxRed.getValue() / 255f, boxGreen.getValue() / 255f, boxBlue.getValue() / 255f, boxAlpha.getValue() / 255f), true, new Color(outlineRed.getValue() / 255f, outlineGreen.getValue() / 255f, outlineBlue.getValue() / 255f, outlineAlpha.getValue() / 255f), lineWidth.getValue(), outline.getValue(), box.getValue(), (int) boxAlpha.getValue(), true);
            }
        }
    }

    static class bestPlacePos {
        BlockPos blockPos;
        float targetDamage;

        public bestPlacePos(BlockPos blockPos, float targetDamage) {
            this.blockPos = blockPos;
            this.targetDamage = targetDamage;
        }

        public float getTargetDamage() {
            return targetDamage;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }
    }
    public static boolean canPlaceCrystalIfObbyWasAtPos(final BlockPos pos) {

        final Block floor = mc.world.getBlockState(pos.add(0, 1, 0)).getBlock();
        final Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

        if (floor == Blocks.AIR && ceil == Blocks.AIR) {
            return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.add(0, 1, 0))).isEmpty();
        }

        return false;
    }

    public boolean isValidToJoinDaGang(BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);

            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                return true;
            }
        }
        return false;
    }
}

