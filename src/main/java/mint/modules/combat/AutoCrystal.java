package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
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
import java.awt.*;
import java.util.List;

public class AutoCrystal extends Module {

    public Setting<Float> targetRange = register(new Setting("Target Range", 10f, 0f, 15f));

    public Setting<Float> placeRange = register(new Setting("Place Range", 5f, 0f, 6f));
    public Setting<Float> breakRange = register(new Setting("Break Range", 5f, 0f, 6f));

    public Setting<Float> minDamage = register(new Setting("Min Damage", 6f, 0f, 12f));
    public Setting<Float> maxSelfDamage = register(new Setting("Max Self Damage", 8f, 0f, 12f));

    public Setting<Integer> placeDelay = register(new Setting("Place Delay", 70, 0, 200));

    Timer placeTimer = new Timer();
    BlockPos finalPos;
    BlockPos finalCrystalPos;
    int crystals;

    public AutoCrystal(){
        super("AutoCrystal", Category.COMBAT, "");
    }

    public void onToggle(){
        mc.world.removeEntityFromWorld(crystals);
        crystals = 0;
    }
    @Override
    public void onUpdate() {
        if (EntityUtil.getTarget(targetRange.getValue()) != null) {
            if(placeTimer.passedMs(placeDelay.getValue())) {
                doPlace();
                placeTimer.reset();
            }
        }
    }

    private void doPlace() {
        BlockPos placePos = null;
        float maxDamage = 0.5f;
        final List<BlockPos> sphere = BlockUtil.getSphere(this.placeRange.getValue(), true);
        for (int size = sphere.size(), i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            final float self = EntityUtil.calculatePos(pos, mc.player);
            if (BlockUtil.canPlaceCrystal(pos, true)) {
                final float damage;
                if (EntityUtil.getHealth(mc.player) > self + 0.5f && this.maxSelfDamage.getValue() > self && (damage = EntityUtil.calculatePos(pos, EntityUtil.getTarget(targetRange.getValue()))) > maxDamage && damage > self) {
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
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem()== Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

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

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
            if (event.getPacket() instanceof SPacketSoundEffect) {
                final SPacketSoundEffect packet = event.getPacket();
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity entityCrystal : mc.world.loadedEntityList) {
                        if (entityCrystal instanceof EntityEnderCrystal) {
                            if (entityCrystal.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= breakRange.getValue()) {
                                    entityCrystal.setDead();
                        }
                    }
                }
            }
        }
        if (event.getPacket() instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51 && finalPos != null && EntityUtil.getTarget(targetRange.getValue()) != null) {
                final CPacketUseEntity predict = new CPacketUseEntity();
                predict.entityId = packet.getEntityID();
                predict.action = CPacketUseEntity.Action.ATTACK;
                if(predict.entityId != crystals) {
                    mc.getConnection().sendPacket(predict);
                }
            }
        }
    }

    public void onRender3D(Render3DEvent event){
        if(finalPos != null){
            RenderUtil.drawBox(finalPos, new Color(0, 255, 0, 100));
        }
        if(finalCrystalPos != null){
            RenderUtil.drawBox(finalCrystalPos, new Color(255, 0, 0, 100));
        }

    }
}
