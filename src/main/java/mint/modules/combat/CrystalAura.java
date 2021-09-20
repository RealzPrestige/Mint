package mint.modules.combat;

import mint.clickgui.setting.BindSetting;
import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystalAura extends Module {

    private static Minecraft mc = Minecraft.getMinecraft();

    public Setting<Boolean> parentBreak = register(new Setting("Break", true, false));
    public Setting<Boolean> breakIgnoreSelf = register(new Setting("BreakIgnoreSelf", false,  v-> parentBreak.getValue()));
    public Setting<Float> breakRange = register(new Setting("BreakRange", 5.0f, 0.1f, 6.0f, v -> parentBreak.getValue()));
    public Setting<Float> breakMinDmg = register(new Setting("BreakMinDamage", 6.0f, 0.1f, 36.0f, v -> parentBreak.getValue()));
    public Setting<Float> breakMaxSelf = register(new Setting("BreakMaxSelfDamage", 8.0f, 0.1f, 36.0f, v -> parentBreak.getValue()));
    public Setting<Boolean> packetBreak = register(new Setting("PacketBreak", true, v-> parentBreak.getValue()));
    public Setting<Boolean> predictBreak = register(new Setting("Predict", true, v-> parentBreak.getValue()));
    public Setting<Float> breakMinHp = register(new Setting("BreakMinHp", 8.0f, 0.1f, 36.0f, v -> parentBreak.getValue()));
    public Setting<Integer> breakDelay = register(new Setting("BreakDelay", 70, 0, 200, v -> parentBreak.getValue()));

    public Setting<Boolean> parentPlace = register(new Setting("Place", true, false));
    public Setting<Boolean> placeIgnoreSelf = register(new Setting("PlaceIgnoreSelf", false,  v-> parentPlace.getValue()));
    public Setting<Float> placeRange = register(new Setting("PlaceRange", 5.0f, 0.1f, 6.0f, v -> parentPlace.getValue()));
    public Setting<Float> placeMinDmg = register(new Setting("PlaceMinDamage", 5.0f, 0.1f, 36.0f, v -> parentPlace.getValue()));
    public Setting<Float> placeMaxSelf = register(new Setting("PlaceMaxSelfDamage", 8.0f, 0.1f, 36.0f, v -> parentPlace.getValue()));
    public Setting<Float> placeMinHp = register(new Setting("PlaceMinHp", 8.0f, 0.1f, 36.0f, v -> parentPlace.getValue()));
    public Setting<Integer> placeDelay = register(new Setting("PlaceDelay", 70, 0, 200, v -> parentPlace.getValue()));

    public Setting<Boolean> targetParent = register(new Setting("Target", true, false));
    public Setting<Float> targetRange = register(new Setting("TargetRange", 12.0f, 0.1f, 15.0f, v -> targetParent.getValue()));

    public Setting<Boolean> parentFacePlace = register(new Setting("FacePlace", true, false));
    public Setting<Boolean> health = register(new Setting("Health", false,  v-> parentFacePlace.getValue()));
    public Setting<Integer> healthAmount = register(new Setting("HealthAmount", 10, 1, 36, v -> parentFacePlace.getValue() && health.getValue()));
    public Setting<Boolean> armor = register(new Setting("Armor", false,  v-> parentFacePlace.getValue()));
    public Setting<Integer> armorPercent = register(new Setting("ArmorPercent", 30, 0, 100, v -> parentFacePlace.getValue() && armor.getValue()));
    public Setting<Boolean> bind = register(new Setting("Bind", false, v-> parentFacePlace.getValue()));
    public Setting<BindSetting> facePlaceBind = register(new Setting<>("FacePlaceBind:", new BindSetting(1), v-> parentFacePlace.getValue() && bind.getValue()));

    public Setting<Boolean> parentMisc = register(new Setting("Misc", true, false));
    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch", false,  v-> parentMisc.getValue()));
    public Setting<Integer> resetDelay = register(new Setting("ResetDelay", 100, 1, 250, v -> parentMisc.getValue()));
    public Setting<Integer> maxCrystals = register(new Setting("MaxCrystals", 3, 1, 10, v -> parentMisc.getValue()));
    public Setting<Integer> maxCrystalResetDelay = register(new Setting("MaxCrystalResetDelay", 2, 1, 10, v -> parentMisc.getValue()));

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
    HashMap<BlockPos, Integer> renderPosses = new HashMap();

    public CrystalAura(){
        super("Crystal Aura", Module.Category.COMBAT, "Automatically places and breaks crystals.");
    }
    @Override
    public void onToggle() {
        target = EntityUtil.getTarget(targetRange.getValue());
        target = null;
    }

    public void onTick(){
        ++ticks;
        if(ticks >= maxCrystalResetDelay.getValue()){
            ticks = 0;
            crystalAmount = 0;
        }
    }
    @Override
    public void onUpdate() {
        if (resetTimer.passedMs(resetDelay.getValue())) {
            finalPlacePos = null;
            finalBreakPos = null;
        }
        target = EntityUtil.getTarget(targetRange.getValue());
        if(target == null) {
            return;
        }
        if((bind.getValue() && facePlaceBind.getValue().getKey() != -1 && Keyboard.isKeyDown(facePlaceBind.getValue().getKey())) || (armor.getValue() && PlayerUtil.isArmorLow(target, armorPercent.getValue())) || (health.getValue() && EntityUtil.getHealth(target) <= healthAmount.getValue())){
            if(isPlayerSafe(target)) {
                doFacePlace(target, silentSwitch.getValue());
                forceFacePlace = true;
            } else {
                forceFacePlace = false;
            }
        } else {
            forceFacePlace = false;
        }
        if(!forceFacePlace) {
            if (placeTimer.passedMs(placeDelay.getValue()) && crystalAmount < maxCrystals.getValue()) {
                doPlace();
                placeTimer.reset();
            }
        }
        if(breakTimer.passedMs(breakDelay.getValue())) {
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
            int crystalSlot = getItemHotbar(Items.END_CRYSTAL);
            int oldSlot = mc.player.inventory.currentItem;
            if(mc.player.getHeldItemOffhand().getItem()!= Items.END_CRYSTAL){
                if(silentSwitch.getValue()){
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if(mc.player.getHeldItemOffhand().getItem()!= Items.END_CRYSTAL){
                if(silentSwitch.getValue()){
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }
            }
            if(renderMode.getValue() == RenderMode.FADE){
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
                BlockPos crystalPos = crystal.getPosition();
                float selfDamage = calculatePos(crystalPos, mc.player);
                float targetDamage = calculatePos(crystalPos, target);
                float minDamage = breakMinDmg.getValue();
                float selfHp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                if(selfDamage < (breakIgnoreSelf.getValue() ? 36 : selfHp) && minDamage < targetDamage && selfDamage < (placeIgnoreSelf.getValue() ? 36 : breakMaxSelf.getValue()) && breakMinHp.getValue() < selfHp) {
                    if(crystal.getDistance(mc.player) < MathUtil.square(breakRange.getValue())) {
                        if (packetBreak.getValue()) {
                            mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                            breakPos = crystalPos;
                            finalBreakPos = breakPos;
                        } else {
                            mc.playerController.attackEntity(mc.player, crystal);
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
            if (damageRender.getValue()) {
                //todo oml drawText,,,,,,,,,
            }
        }
    }

    private float calculatePos(final BlockPos pos, final EntityPlayer entity) {
        return EntityUtil.calculate(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, entity);
    }
    public static EntityPlayer getTarget(final float range) {
        EntityPlayer currentTarget = null;
        for (int size = mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = mc.world.playerEntities.get(i);
            if (!EntityUtil.isntValid(player, range)) {
                if (currentTarget == null) {
                    currentTarget = player;
                }
                else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) {
                    currentTarget = player;
                }
            }
        }
        return currentTarget;
    }

    public static int getItemHotbar(Item input) {
        for (int z = 0; z < 9; ++z) {
            Item item = mc.player.inventory.getStackInSlot(z).getItem();
            if (Item.getIdFromItem(item) != Item.getIdFromItem(input)) continue;
            return z;
        }
        return -1;
    }

    public void doFacePlace(EntityPlayer target, boolean silentSwitch){
        boolean canPlaceNorth = false;
        boolean canPlaceEast = false;
        boolean canPlaceSouth = false;
        boolean canPlaceWest = false;
        int crystalSlot = getItemHotbar(Items.END_CRYSTAL);
        int oldSlot = mc.player.inventory.currentItem;
        BlockPos playerPos = getPlayerPos(target);
        if(isPlayerSafe(target)){
            if(mc.world.getBlockState(playerPos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(playerPos.north().up()).getBlock() == Blocks.AIR){
                canPlaceNorth = true;
            }
            if(mc.world.getBlockState(playerPos.east().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(playerPos.east().up()).getBlock() == Blocks.AIR){
                canPlaceEast = true;
            }
            if(mc.world.getBlockState(playerPos.south().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(playerPos.south().up()).getBlock() == Blocks.AIR){
                canPlaceSouth = true;
            }
            if(mc.world.getBlockState(playerPos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(playerPos.west().up()).getBlock() == Blocks.AIR){
                canPlaceWest = true;
            }
        }
        if(canPlaceNorth){
            if(silentSwitch){
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.north(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if(silentSwitch){
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if(canPlaceEast){
            if(silentSwitch){
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.east(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if(silentSwitch){
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if(canPlaceSouth){
            if(silentSwitch){
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.south(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if(silentSwitch){
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if(canPlaceWest){
            if(silentSwitch){
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
            }
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.west(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if(silentSwitch){
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        }
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public boolean isPlayerSafe(EntityPlayer target){
        BlockPos playerPos = getPlayerPos(target);
        if((mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.BEDROCK)){
            return true;
        }
        return false;
    }

    public static boolean canBlockBeSeen(final BlockPos blockPos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true, false) == null;
    }
}