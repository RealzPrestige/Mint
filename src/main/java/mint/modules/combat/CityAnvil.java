package mint.modules.combat;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Objects;

public class CityAnvil extends Module {

    public Setting<Float> targetRange = register(new Setting<>("Target Range", 5.0f, 0.0f, 6.0f));
    public Setting<Boolean> silentSwitch = register(new Setting<>("Silent Switch", false));
    public Setting<Boolean> packet = register(new Setting<>("Packet", false));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", false));
    public Setting<Boolean> swing = register(new Setting<>("Swing", false));
    public Setting<EnumHand> swingMode = register(new Setting<>("Swing Mode", EnumHand.MAIN_HAND, v -> swing.getValue()));
    public Setting<Boolean> autoMine = register(new Setting<>("Auto Mine", false));
    public Setting<Integer> mineDelay  = register(new Setting<>( "Mine Delay", 700, 0, 1000));
    public Setting<Boolean> render = register(new Setting<>("Render", false));
    public Setting<Integer> outlineRed = register(new Setting<>( "Red", 255, 0, 255, v-> render.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("Green", 255, 0, 255, v-> render.getValue()));
    public Setting<Integer> outlineBlue = register(new Setting<>("Blue", 255, 0, 255, v-> render.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("Alpha", 120, 0, 255, v-> render.getValue()));
    Timer timer = new Timer();
    BlockPos currentPos;
    BlockPos currentPos2;
    public CityAnvil() {
        super("City Anvil", Category.COMBAT, "no idea how to describe it");
    }

    public void onUpdate() {
        EntityPlayer target = EntityUtil.getTarget(targetRange.getValue());
        if (target != null && !Mint.friendManager.isFriend(target)) {
            BlockPos pos = PlayerUtil.getPlayerPos(target);
            int anvilSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ANVIL));
            int crystalSlot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
            int oldSlot = mc.player.inventory.currentItem;

            //North
            if (mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.north().north().up()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.down()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.south()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR) {
                if (anvilSlot != -1) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(anvilSlot);
                    }
                    BlockUtil.placeBlock(pos.north(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), swingMode.getValue());
                    currentPos = pos.north();
                    currentPos2 = pos.north().north();
                    if (silentSwitch.getValue()) {
                        mc.player.inventory.currentItem = oldSlot;
                        mc.playerController.updateController();
                    }
                }
                if (silentSwitch.getValue() && crystalSlot != -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }

                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.north().north().down(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                if (silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }

                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal) {
                        if (crystal.getDistance(mc.player) > MathUtil.square(5)) continue;

                        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                    }
                }
            }

            //East
            if (mc.world.getBlockState(pos.east()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.east().east()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.east().east().up()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.down()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.south()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR) {
                if (anvilSlot != -1) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(anvilSlot);
                    }
                    BlockUtil.placeBlock(pos.east(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), swingMode.getValue());
                    currentPos = pos.east();
                    currentPos2 = pos.east().east();
                    if (silentSwitch.getValue()) {
                        mc.player.inventory.currentItem = oldSlot;
                        mc.playerController.updateController();
                    }
                }

                if (silentSwitch.getValue() && crystalSlot != -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }

                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.east().east().down(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                if (silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }

                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal) {
                        if (crystal.getDistance(mc.player) > MathUtil.square(5)) continue;

                        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                    }
                }
            }

            //South
            if (mc.world.getBlockState(pos.south()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.south().south()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.south().south().up()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.down()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR) {
                if (anvilSlot != -1) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(anvilSlot);
                    }
                    BlockUtil.placeBlock(pos.south(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), swingMode.getValue());
                    currentPos = pos.south();
                    currentPos2 = pos.south().south();
                    if (silentSwitch.getValue()) {
                        mc.player.inventory.currentItem = oldSlot;
                        mc.playerController.updateController();
                    }
                }

                if (silentSwitch.getValue() && crystalSlot != -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }

                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.south().south().down(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                if (silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }

                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal) {

                        if (crystal.getDistance(mc.player) > MathUtil.square(5)) continue;

                        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));

                    }
                }

            }

            //West
            if (mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.west().west().up()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.down()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.south()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR) {
                if (anvilSlot != -1) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(anvilSlot);
                    }
                    BlockUtil.placeBlock(pos.west(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), swingMode.getValue());
                    currentPos = pos.west();
                    currentPos2 = pos.west().west();
                    if (silentSwitch.getValue()) {
                        mc.player.inventory.currentItem = oldSlot;
                        mc.playerController.updateController();
                    }
                }

                if (silentSwitch.getValue() && crystalSlot != -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }

                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.west().west().down(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                if (silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }

                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal) {

                        if (crystal.getDistance(mc.player) > MathUtil.square(5)) continue;

                        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                    }
                }
            }
        }
        if(currentPos != null && mc.world.getBlockState(currentPos).getBlock() == Blocks.ANVIL){
            if(autoMine.getValue()) {
                if (timer.passedMs(mineDelay.getValue())) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, EnumFacing.UP));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.UP));
                    timer.reset();
                }
            }

        }
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (currentPos != null && render.getValue()) {
            RenderUtil.drawBlockOutline(currentPos, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), 1, true);
        }
        if(currentPos2 != null && render.getValue()){
            RenderUtil.drawBlockOutline(currentPos2, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), 1, true);
        }
    }
}
