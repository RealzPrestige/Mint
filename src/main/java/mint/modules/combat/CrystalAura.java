package mint.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystalAura extends Module {

    /**
     * @author zPrestige & kambing
     * base is custom, some predicts taken off ozark
     */

    private static Minecraft mc = Minecraft.getMinecraft();

    public Setting<Boolean> parentBreak = register(new Setting("Break", true, false));
    public Setting<Boolean> instantBreak = register(new Setting("InstantBreak", true, v-> parentBreak.getValue()));
    public Setting<Boolean> breakIgnoreSelf = register(new Setting("BreakIgnoreSelf", false,  v-> parentBreak.getValue() && !instantBreak.getValue()));
    public Setting<Float> breakRange = register(new Setting("BreakRange", 5.0f, 0.1f, 6.0f, v -> parentBreak.getValue() && !instantBreak.getValue()));
    public Setting<Float> breakMinDmg = register(new Setting("BreakMinDamage", 6.0f, 0.1f, 36.0f, v -> parentBreak.getValue() && !instantBreak.getValue()));
    public Setting<Float> breakMaxSelf = register(new Setting("BreakMaxSelfDamage", 8.0f, 0.1f, 36.0f, v -> parentBreak.getValue() && !instantBreak.getValue()));
    public Setting<Boolean> packetBreak = register(new Setting("PacketBreak", true, v-> parentBreak.getValue()));
    public Setting<Boolean> predictBreak = register(new Setting("Predict", true, v-> parentBreak.getValue()));
    public Setting<Float> breakMinHp = register(new Setting("BreakMinHp", 8.0f, 0.1f, 36.0f, v -> parentBreak.getValue() && !instantBreak.getValue()));
    public Setting<Integer> breakDelay = register(new Setting("BreakDelay", 70, 0, 200, v -> parentBreak.getValue()));
    public Setting<Boolean> soundPredict = register(new Setting("SoundPredict", true, v-> parentBreak.getValue()));

    public Setting<Boolean> parentPlace = register(new Setting("Place", true, false));
    public Setting<Boolean> placeIgnoreSelf = register(new Setting("PlaceIgnoreSelf", false,  v-> parentPlace.getValue()));
    public Setting<Float> placeRange = register(new Setting("PlaceRange", 5.0f, 0.1f, 6.0f, v -> parentPlace.getValue()));
    public Setting<Float> placeMinDmg = register(new Setting("PlaceMinDamage", 5.0f, 0.1f, 36.0f, v -> parentPlace.getValue()));
    public Setting<Float> placeMaxSelf = register(new Setting("PlaceMaxSelfDamage", 8.0f, 0.1f, 36.0f, v -> parentPlace.getValue()));
    public Setting<Float> placeMinHp = register(new Setting("PlaceMinHp", 8.0f, 0.1f, 36.0f, v -> parentPlace.getValue()));
    public Setting<Integer> placeDelay = register(new Setting("PlaceDelay", 70, 0, 200, v -> parentPlace.getValue()));
    public Setting<Boolean> uzimode = register(new Setting("UziMode", false, v-> parentPlace.getValue()));
    public Setting<Integer> uziSpeed = register(new Setting("UziSpeed", 1, 0, 3, v-> parentPlace.getValue() && uzimode.getValue()));
    public Setting<Boolean> uziSound = register(new Setting("UziSound", false, v-> parentPlace.getValue() && uzimode.getValue()));
    public Setting<Integer> uziRemoveDelay = register(new Setting("UziRemoveDelay", 10, 1, 50, v-> parentPlace.getValue() && uzimode.getValue()));

    public Setting<Boolean> targetParent = register(new Setting("Target", true, false));
    public Setting<Float> targetRange = register(new Setting("TargetRange", 12.0f, 0.1f, 15.0f, v -> targetParent.getValue()));

    public Setting<Boolean> parentFacePlace = register(new Setting("FacePlace", true, false));
    public Setting<Boolean> health = register(new Setting("Health", false,  v-> parentFacePlace.getValue()));
    public Setting<Integer> healthAmount = register(new Setting("HealthAmount", 10, 1, 36, v -> parentFacePlace.getValue() && health.getValue()));
    public Setting<Boolean> armor = register(new Setting("Armor", false,  v-> parentFacePlace.getValue()));
    public Setting<Integer> armorPercent = register(new Setting("ArmorPercent", 30, 0, 100, v -> parentFacePlace.getValue() && armor.getValue()));
    public Setting<Boolean> bind = register(new Setting("Bind", false, v-> parentFacePlace.getValue()));
    public Setting<Bind> facePlaceBind = register(new Setting<>("FacePlaceBind:", new Bind(1), v-> parentFacePlace.getValue() && bind.getValue()));

    public Setting<Boolean> parentMisc = register(new Setting("Misc", true, false));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch", false,  v-> parentMisc.getValue()));
    public Setting<Integer> resetDelay = register(new Setting("ResetDelay", 100, 1, 250, v -> parentMisc.getValue()));
    public Setting<Integer> maxCrystals = register(new Setting("MaxCrystals", 3, 1, 10, v -> parentMisc.getValue()));
    public Setting<Integer> maxCrystalResetDelay = register(new Setting("MaxCrystalResetDelay", 2, 1, 20, v -> parentMisc.getValue()));

    public Setting<Boolean> parentVisual = register(new Setting("Visual", true, false));
    public Setting<RenderMode> renderMode = register(new Setting("RenderMode", RenderMode.FADE, v-> parentVisual.getValue()));
    public enum RenderMode{STATIC, FADE}
    public Setting<Boolean> fadeParent = register(new Setting("Fade", false, true, v-> parentVisual.getValue()));
    public Setting<Integer> startAlpha = register(new Setting<>("StartAlpha", 255, 0, 255, v-> parentVisual.getValue() && fadeParent.getValue()));
    public Setting<Integer> endAlpha = register(new Setting<>("EndAlpha", 0, 0, 255, v-> parentVisual.getValue() && fadeParent.getValue()));
    public Setting<Integer> fadeStep = register(new Setting<>("FadeStep", 20, 10, 100, v-> parentVisual.getValue() && fadeParent.getValue()));
    public Setting<Boolean> boxParent = register(new Setting("Box", false, true, v-> parentVisual.getValue()));
    public Setting<Boolean> damageRender = register(new Setting("DamageText", false, v-> parentVisual.getValue() && boxParent.getValue()));
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

    public Timer resetTimer = new Timer();
    public EntityPlayer target;
    public BlockPos finalPlacePos;
    public BlockPos finalBreakPos;
    public Timer placeTimer = new Timer();
    public Timer breakTimer = new Timer();
    public int crystalAmount;
    public int ticks;
    boolean forceFacePlace;

    int entityIdg;

    int entityId1;
    int entityId2;

    int entityId11;
    int entityId22;
    int entityId33;

    HashMap<BlockPos, Integer> renderPosses = new HashMap();

    public CrystalAura(){
        super("CrystalAura", Module.Category.COMBAT, "Automatically places and breaks crystals.");
    }

    @Override
    public void onToggle() {
        target = EntityUtil.getTarget(targetRange.getValue());
        target = null;
        mc.world.removeEntityFromWorld(entityIdg);
        entityIdg = 0;
        mc.world.removeEntityFromWorld(entityId1);
        entityId1 = 0;
        mc.world.removeEntityFromWorld(entityId2);
        entityId2 = 0;
        mc.world.removeEntityFromWorld(entityId11);
        entityId11 = 0;
        mc.world.removeEntityFromWorld(entityId22);
        entityId22 = 0;
        mc.world.removeEntityFromWorld(entityId33);
        entityId33 = 0;
    }

    @Override
    public void onUpdate() {

        target = EntityUtil.getTarget(targetRange.getValue());

        if (ticks >= maxCrystalResetDelay.getValue()) {
            ticks = 0;
            crystalAmount = 0;
        }
        ++ticks;

        if (resetTimer.passedMs(resetDelay.getValue())) {
            finalPlacePos = null;
            finalBreakPos = null;
        }

        if (target == null) {
            return;
        }

        if ((bind.getValue() && facePlaceBind.getValue().getKey() != -1 && Keyboard.isKeyDown(facePlaceBind.getValue().getKey())) || (armor.getValue() && PlayerUtil.isArmorLow(target, armorPercent.getValue())) || (health.getValue() && EntityUtil.getHealth(target) <= healthAmount.getValue())){
            if (EntityUtil.isPlayerSafe(target)) {
                doFacePlace(target, silentSwitch.getValue());
                forceFacePlace = true;
            } else {
                forceFacePlace = false;
            }
        } else {
            forceFacePlace = false;
        }

        if (placeTimer.passedMs(placeDelay.getValue()) && crystalAmount < maxCrystals.getValue() && !forceFacePlace) {
               doPlace();
               placeTimer.reset();
        }
        if (breakTimer.passedMs(breakDelay.getValue())) {
            doBreak();
            breakTimer.reset();
        }
    }

    public void doPlace() {
        BlockPos placePos = null;
        target = EntityUtil.getTarget(targetRange.getValue());
        final List<BlockPos> sphere = BlockUtil.getSphere(placeRange.getValue(), true);
        for (int size = sphere.size(), i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);
            if (BlockUtil.canPlaceCrystal(pos, true)){
                float selfDamage = calculatePos(pos, mc.player);
                float oldDamage = finalPlacePos != null ? calculatePos(finalPlacePos, target) : 0;
                float targetDamage = calculatePos(pos, target);
                float minDamage;
                if ((EntityUtil.getHealth(target) < healthAmount.getValue()) || (bind.getValue() && Keyboard.isKeyDown(facePlaceBind.getValue().getKey())) || (PlayerUtil.isArmorLow(target, armorPercent.getValue()))){
                    minDamage = 2;
                } else {
                    minDamage = placeMinDmg.getValue();
                }
                float selfHp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                if (selfDamage < (placeIgnoreSelf.getValue() ? 36 : placeMaxSelf.getValue()) && selfDamage < selfHp && placeMinDmg.getValue() < targetDamage && selfDamage < placeMaxSelf.getValue() && oldDamage < targetDamage && placeMinHp.getValue() < selfHp){
                    if (targetDamage > minDamage) {
                        placePos = pos;
                        finalPlacePos = placePos;

                    }
                }
            }
        }
        if (placePos != null){
            int crystalSlot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
            int oldSlot = mc.player.inventory.currentItem;
            if (mc.player.getHeldItemOffhand().getItem()!= Items.END_CRYSTAL){
                if (silentSwitch.getValue()){
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            //ghost appeared
            if (uzimode.getValue()) {
                Timer ghostTimer = new Timer();
                if (uziSpeed.getValue() == 1) {
                    EntityEnderCrystal crystal = new EntityEnderCrystal(mc.world, (double) placePos.getX() + 0.5, (double) placePos.getY() + 1, (double) placePos.getZ() + 0.5);
                    mc.world.addEntityToWorld(entityIdg, crystal);
                    ghostTimer.reset();
                    if (ghostTimer.passedMs(uziRemoveDelay.getValue())) {
                        mc.world.removeEntityFromWorld(entityIdg);
                        if (uziSound.getValue()){
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f));
                        }
                        ++entityIdg;
                    }
                } else if (uziSpeed.getValue() == 2){
                    Timer firstTimer = new Timer();
                    EntityEnderCrystal crystal1 = new EntityEnderCrystal(mc.world, (double) placePos.getX() + 0.5, (double) placePos.getY() + 1, (double) placePos.getZ() + 0.5);
                    mc.world.addEntityToWorld(entityId1, crystal1);
                    firstTimer.reset();
                    if (firstTimer.passedMs(uziRemoveDelay.getValue())) {
                        mc.world.removeEntityFromWorld(entityId1);
                        if (uziSound.getValue()){
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f));
                        }
                        ++entityId1;
                    }
                    Timer secondTimer = new Timer();
                    EntityEnderCrystal crystal2 = new EntityEnderCrystal(mc.world, (double) placePos.getX() + 0.5, (double) placePos.getY() + 1, (double) placePos.getZ() + 0.5);
                    mc.world.addEntityToWorld(entityId2, crystal2);
                    secondTimer.reset();
                    if (secondTimer.passedMs(uziRemoveDelay.getValue())) {
                        mc.world.removeEntityFromWorld(entityId2);
                        if (uziSound.getValue()){
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f));
                        }
                        ++entityId2;
                    }
                } else if (uziSpeed.getValue() == 3){
                    Timer firstTimer = new Timer();
                    EntityEnderCrystal crystal1 = new EntityEnderCrystal(mc.world, (double) placePos.getX() + 0.5, (double) placePos.getY() + 1, (double) placePos.getZ() + 0.5);
                    mc.world.addEntityToWorld(entityId11, crystal1);
                    firstTimer.reset();
                    if (firstTimer.passedMs(uziRemoveDelay.getValue())) {
                        mc.world.removeEntityFromWorld(entityId11);
                        if (uziSound.getValue()){
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f));
                        }
                        ++entityId11;
                    }
                    Timer secondTimer = new Timer();
                    EntityEnderCrystal crystal2 = new EntityEnderCrystal(mc.world, (double) placePos.getX() + 0.5, (double) placePos.getY() + 1, (double) placePos.getZ() + 0.5);
                    mc.world.addEntityToWorld(entityId22, crystal2);
                    secondTimer.reset();
                    if (secondTimer.passedMs(uziRemoveDelay.getValue())) {
                        mc.world.removeEntityFromWorld(entityId22);
                        if (uziSound.getValue()){
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f));
                        }
                        ++entityId22;
                    }
                    Timer thirdTimer = new Timer();
                    EntityEnderCrystal crystal3 = new EntityEnderCrystal(mc.world, (double) placePos.getX() + 0.5, (double) placePos.getY() + 1, (double) placePos.getZ() + 0.5);
                    mc.world.addEntityToWorld(entityId33, crystal3);
                    thirdTimer.reset();
                    if (thirdTimer.passedMs(uziRemoveDelay.getValue())) {
                        mc.world.removeEntityFromWorld(entityId33);
                        if (uziSound.getValue()){
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f));
                        }
                        ++entityId33;
                    }
                }
            }
            if (mc.player.getHeldItemOffhand().getItem()!= Items.END_CRYSTAL){
                if (silentSwitch.getValue()){
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }
            }
            if (renderMode.getValue() == RenderMode.FADE){
                renderPosses.put(placePos, startAlpha.getValue());
            }
            crystalAmount++;
        }
    }

    public void doBreak() {
        BlockPos breakPos;
        target = EntityUtil.getTarget(targetRange.getValue());
        for (Entity crystal : mc.world.loadedEntityList) {
            if (crystal instanceof EntityEnderCrystal) {
                if (crystal.getEntityId() != entityId1 && crystal.getEntityId() != entityId2 && crystal.getEntityId() != entityId11 && crystal.getEntityId() != entityId22 && crystal.getEntityId() != entityIdg && crystal.getEntityId() != entityId33){
                    BlockPos crystalPos = crystal.getPosition();
                    float selfDamage = calculatePos(crystalPos, mc.player);
                    float targetDamage = calculatePos(crystalPos, target);
                    float minDamage = breakMinDmg.getValue();
                    float selfHp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                    if (instantBreak.getValue() || (selfDamage < (breakIgnoreSelf.getValue() ? 36 : selfHp) && minDamage < targetDamage && selfDamage < (placeIgnoreSelf.getValue() ? 36 : breakMaxSelf.getValue()) && breakMinHp.getValue() < selfHp)) {
                        if (crystal.getDistance(mc.player) < MathUtil.square(breakRange.getValue())) {
                            if (packetBreak.getValue()) {
                                mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                                breakPos = crystalPos;
                                finalBreakPos = breakPos;
                            } else {
                                mc.playerController.attackEntity(mc.player, crystal);
                                breakPos = crystalPos;
                                finalBreakPos = breakPos;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof SPacketSpawnObject && predictBreak.getValue()) {
            final SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51 && finalPlacePos != null && target != null) {
                final CPacketUseEntity predict = new CPacketUseEntity();
                predict.entityId = packet.getEntityID();
                predict.action = CPacketUseEntity.Action.ATTACK;
                mc.getConnection().sendPacket(predict);
                mc.player.swingArm(EnumHand.MAIN_HAND);
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
                        if (entityCrystal.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= breakRange.getValue()) {
                            if (soundPredict.getValue()) {
                                entityCrystal.setDead();
                            }
                        }
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
            if (finalBreakPos != null && finalPlacePos != null) {
                if (boxSetting.getValue() && finalBreakPos != finalPlacePos) {
                    RenderUtil.drawBoxESP(finalBreakPos.down(), new Color(255, 0, 0, boxAlpha.getValue()), true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), 0.1f, outlineSetting.getValue(), boxSetting.getValue(), boxAlpha.getValue(), false);
                }
            }
            if (finalPlacePos != null) {
                if (boxSetting.getValue()) {
                    RenderUtil.drawBoxESP(finalPlacePos, new Color(boxRed.getValue(), boxGreen.getValue(), boxBlue.getValue(), boxAlpha.getValue()), true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), 0.1f, outlineSetting.getValue(), boxSetting.getValue(), boxAlpha.getValue(), false);
                }
            }
        }
        if (damageRender.getValue() && finalPlacePos != null){
            RenderUtil.drawText(finalPlacePos, "" + ChatFormatting.RED + MathUtil.round(calculatePos(finalPlacePos, target), 0) + ChatFormatting.WHITE + " | " + ChatFormatting.GREEN + MathUtil.round(calculatePos(finalPlacePos, mc.player), 0)  , -1);
        }
    }

    private float calculatePos(final BlockPos pos, final EntityPlayer entity) {
        return EntityUtil.calculate(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, entity);
    }

    public void doFacePlace(EntityPlayer target, boolean silentSwitch) {
        boolean canPlaceNorth = false;
        boolean canPlaceEast = false;
        boolean canPlaceSouth = false;
        boolean canPlaceWest = false;
        int crystalSlot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int oldSlot = mc.player.inventory.currentItem;
        BlockPos playerPos = EntityUtil.getPlayerPos(target);
        if (EntityUtil.isPlayerSafe(target)){
            if (mc.world.getBlockState(playerPos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(playerPos.north().up()).getBlock() == Blocks.AIR){
                canPlaceNorth = true;
            }
            if (mc.world.getBlockState(playerPos.east().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(playerPos.east().up()).getBlock() == Blocks.AIR){
                canPlaceEast = true;
            }
            if (mc.world.getBlockState(playerPos.south().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(playerPos.south().up()).getBlock() == Blocks.AIR){
                canPlaceSouth = true;
            }
            if (mc.world.getBlockState(playerPos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(playerPos.west().up()).getBlock() == Blocks.AIR){
                canPlaceWest = true;
            }
        }
        if (canPlaceNorth){
            if (silentSwitch){
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.north(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if (silentSwitch){
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if (canPlaceEast){
            if (silentSwitch){
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.east(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if (silentSwitch){
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if (canPlaceSouth){
            if (silentSwitch){
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.south(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if (silentSwitch){
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if (canPlaceWest){
            if (silentSwitch){
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.west(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if (silentSwitch){
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        }
    }
}