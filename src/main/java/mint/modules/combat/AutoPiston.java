package mint.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.clickgui.setting.Setting;
import mint.events.Render3DEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class AutoPiston extends Module {
    public Setting<Float> targetRange = register(new Setting("Target Range", 5.0f, 0, 10.0f));
    public Setting<Float> placeRange = register(new Setting("Place Range", 5.0f, 0, 6.0f));
    public Setting<Float> breakRange = register(new Setting("Break Range", 5.0f, 0.0f, 6.0f));
    public Setting<Integer> startDelay = register(new Setting("Start Delay", 100, 0, 1000));
    public Setting<Integer> placeDelay = register(new Setting("Place Delay", 100, 10, 1000));
    public Setting<Integer> redstoneDelay = register(new Setting("Redstone Delay", 150, 10, 1000));
    public Setting<Integer> breakDelay = register(new Setting("Break Delay", 100, 10, 1000));
    public Setting<Integer> trapDelay = register(new Setting("Trap Delay", 100, 10, 1000));
    public Setting<Boolean> completeOnDisable = register(new Setting<>("Complete On Disable", false));
    public Setting<Boolean> autoTrap = register(new Setting<>("Trap", false));
    public Setting<Boolean> packet = register(new Setting<>("Packet", false));
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", false));
    public Setting<Boolean> blockSwing = register(new Setting<>("Block Swing", false));
    public Setting<BlockSwingHand> blockSwingMode = register(new Setting<>("Block Swing Mode", BlockSwingHand.MAINHAND, v -> blockSwing.getValue()));

    public enum BlockSwingHand {MAINHAND, OFFHAND, PACKET}

    public Setting<Boolean> packetBreak = register(new Setting<>("Packet Break", false, false));
    public Setting<Boolean> crystalPlaceSwing = register(new Setting<>("Crystal Place Swing", false));
    public Setting<PlaceSwingHand> placeSwingHand = register(new Setting<>("Place Swing Hand", PlaceSwingHand.MAINHAND, v -> crystalPlaceSwing.getValue()));

    public enum PlaceSwingHand {MAINHAND, OFFHAND, PACKET}

    public Setting<Boolean> crystalBreakSwing = register(new Setting<>("Crystal Break Swing", false));
    public Setting<BreakSwingHand> breakSwingHand = register(new Setting<>("Break Swing Hand", BreakSwingHand.MAINHAND, v -> crystalBreakSwing.getValue()));

    public enum BreakSwingHand {MAINHAND, OFFHAND, PACKET}


    Timer startTimer = new Timer();
    EntityPlayer target;
    Timer timer = new Timer();
    Timer breakTimer = new Timer();
    Timer trapTimer = new Timer();
    int i;

    public AutoPiston() {
        super("Auto Piston", Category.COMBAT, "Pushes crystals into holes using pistons.");
    }

    public void onLogin() {
        disable();
    }

    public void onEnable() {
        timer.reset();
        startTimer.reset();
        breakTimer.reset();
        trapTimer.reset();
        i = 0;
        target = null;
    }

    public void onDisable() {
        if (completeOnDisable.getValue()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (mc.player.getDistance(entity) > breakRange.getValue())
                        continue;

                    if (packetBreak.getValue())
                        mc.getConnection().sendPacket(new CPacketUseEntity(entity));
                    else
                        mc.playerController.attackEntity(mc.player, entity);

                    if (crystalBreakSwing.getValue())
                        swingArm(false, false);
                    breakTimer.reset();
                    timer.reset();
                    i = 0;
                }
            }
        }
    }


    public void onUpdate() {
        if (fullNullCheck())
            return;

        target = EntityUtil.getTarget(targetRange.getValue());
        boolean occupiedNorth = false;
        boolean occupiedEast = false;
        boolean occupiedSouth = false;
        boolean occupiedWest = false;
        if (target == null)
            return;

        if (!EntityUtil.isPlayerSafe(target))
            return;

        if (breakTimer.passedMs((placeDelay.getValue() * 3) + redstoneDelay.getValue() + breakDelay.getValue())) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (mc.player.getDistance(entity) > breakRange.getValue())
                        continue;

                    if (packetBreak.getValue())
                        mc.getConnection().sendPacket(new CPacketUseEntity(entity));
                    else
                        mc.playerController.attackEntity(mc.player, entity);

                    if (crystalBreakSwing.getValue())
                        swingArm(false, false);
                    breakTimer.reset();
                    timer.reset();
                    i = 0;
                }
            }
        }
        BlockPos playerPos = PlayerUtil.getPlayerPos(target);

        if (!mc.world.getBlockState(playerPos.north().up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.north().up().up()).getBlock().equals(Blocks.AIR) || !(mc.world.getBlockState(playerPos.north().north().up()).getBlock().equals(Blocks.PISTON) || mc.world.getBlockState(playerPos.north().north().up()).getBlock().equals(Blocks.AIR)) || !(mc.world.getBlockState(playerPos.north().north().north().up()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(playerPos.north().north().north().up()).getBlock().equals(Blocks.AIR)))
            occupiedNorth = true;

        if (!mc.world.getBlockState(playerPos.east().up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.east().up().up()).getBlock().equals(Blocks.AIR) || !(mc.world.getBlockState(playerPos.east().east().up()).getBlock().equals(Blocks.PISTON) || mc.world.getBlockState(playerPos.east().east().up()).getBlock().equals(Blocks.AIR)) || !(mc.world.getBlockState(playerPos.east().east().east().up()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(playerPos.east().east().east().up()).getBlock().equals(Blocks.AIR)))
            occupiedEast = true;

        if (!mc.world.getBlockState(playerPos.south().up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.south().up().up()).getBlock().equals(Blocks.AIR) || !(mc.world.getBlockState(playerPos.south().south().up()).getBlock().equals(Blocks.PISTON) || mc.world.getBlockState(playerPos.south().south().up()).getBlock().equals(Blocks.AIR)) || !(mc.world.getBlockState(playerPos.south().south().south().up()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(playerPos.south().south().south().up()).getBlock().equals(Blocks.AIR)))
            occupiedSouth = true;

        if (!mc.world.getBlockState(playerPos.west().up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.west().up().up()).getBlock().equals(Blocks.AIR) || !(mc.world.getBlockState(playerPos.west().west().up()).getBlock().equals(Blocks.PISTON) || mc.world.getBlockState(playerPos.west().west().up()).getBlock().equals(Blocks.AIR)) || !(mc.world.getBlockState(playerPos.west().west().west().up()).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(playerPos.west().west().west().up()).getBlock().equals(Blocks.AIR)))
            occupiedWest = true;

        if (autoTrap.getValue() && !mc.world.getBlockState(playerPos.up().up()).getBlock().equals(Blocks.OBSIDIAN)) {
            if (!trapTimer.passedMs(trapDelay.getValue()))
                return;
            int currentSlot = mc.player.inventory.currentItem;
            if (!mc.world.getBlockState(playerPos.up().up().north()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.up().up().east()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.up().up().south()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(playerPos.up().up().west()).getBlock().equals(Blocks.AIR)) {
                if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                    disableModule();
                InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                BlockUtil.placeBlock(playerPos.up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                if (blockSwing.getValue())
                    swingArm(true, true);
                mc.player.inventory.currentItem = currentSlot;
                mc.playerController.updateController();
                trapTimer.reset();
            } else {
                if (!occupiedNorth) {
                    if (mc.world.getBlockState(playerPos.south().up()).getBlock().equals(Blocks.AIR)) {
                        if (!trapTimer.passedMs(trapDelay.getValue()))
                            return;
                        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                            disableModule();
                        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        BlockUtil.placeBlock(playerPos.south().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                        if (blockSwing.getValue())
                            swingArm(true, true);
                        mc.player.inventory.currentItem = currentSlot;
                        mc.playerController.updateController();
                        trapTimer.reset();
                    } else if (mc.world.getBlockState(playerPos.south().up().up()).getBlock().equals(Blocks.AIR)) {
                        if (!trapTimer.passedMs(trapDelay.getValue()))
                            return;
                        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                            disableModule();
                        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        BlockUtil.placeBlock(playerPos.south().up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                        if (blockSwing.getValue())
                            swingArm(true, true);
                        mc.player.inventory.currentItem = currentSlot;
                        mc.playerController.updateController();
                        trapTimer.reset();
                    }
                } else if (!occupiedEast) {
                    if (mc.world.getBlockState(playerPos.west().up()).getBlock().equals(Blocks.AIR)) {
                        if (!trapTimer.passedMs(trapDelay.getValue()))
                            return;
                        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                            disableModule();
                        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        BlockUtil.placeBlock(playerPos.west().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                        if (blockSwing.getValue())
                            swingArm(true, true);
                        mc.player.inventory.currentItem = currentSlot;
                        mc.playerController.updateController();
                        trapTimer.reset();
                    } else if (mc.world.getBlockState(playerPos.west().up().up()).getBlock().equals(Blocks.AIR)) {
                        if (!trapTimer.passedMs(trapDelay.getValue()))
                            return;
                        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                            disableModule();
                        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        BlockUtil.placeBlock(playerPos.west().up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                        if (blockSwing.getValue())
                            swingArm(true, true);
                        mc.player.inventory.currentItem = currentSlot;
                        mc.playerController.updateController();
                        trapTimer.reset();
                    }
                } else if (!occupiedSouth) {
                    if (mc.world.getBlockState(playerPos.north().up()).getBlock().equals(Blocks.AIR)) {
                        if (!trapTimer.passedMs(trapDelay.getValue()))
                            return;
                        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                            disableModule();
                        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        BlockUtil.placeBlock(playerPos.north().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                        if (blockSwing.getValue())
                            swingArm(true, true);
                        mc.player.inventory.currentItem = currentSlot;
                        mc.playerController.updateController();
                        trapTimer.reset();
                    } else if (mc.world.getBlockState(playerPos.north().up().up()).getBlock().equals(Blocks.AIR)) {
                        if (!trapTimer.passedMs(trapDelay.getValue()))
                            return;
                        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                            disableModule();
                        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        BlockUtil.placeBlock(playerPos.north().up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                        if (blockSwing.getValue())
                            swingArm(true, true);
                        mc.player.inventory.currentItem = currentSlot;
                        mc.playerController.updateController();
                        trapTimer.reset();
                    }
                } else if (!occupiedWest) {
                    if (mc.world.getBlockState(playerPos.east().up()).getBlock().equals(Blocks.AIR)) {
                        if (!trapTimer.passedMs(trapDelay.getValue()))
                            return;
                        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                            disableModule();
                        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        BlockUtil.placeBlock(playerPos.east().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                        if (blockSwing.getValue())
                            swingArm(true, true);
                        mc.player.inventory.currentItem = currentSlot;
                        mc.playerController.updateController();
                        trapTimer.reset();
                    } else if (mc.world.getBlockState(playerPos.east().up().up()).getBlock().equals(Blocks.AIR)) {
                        if (!trapTimer.passedMs(trapDelay.getValue()))
                            return;
                        if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                            disableModule();
                        InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                        BlockUtil.placeBlock(playerPos.east().up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                        if (blockSwing.getValue())
                            swingArm(true, true);
                        mc.player.inventory.currentItem = currentSlot;
                        mc.playerController.updateController();
                        trapTimer.reset();
                    }
                }
            }
        }

        if (!startTimer.passedMs(startDelay.getValue()))
            return;

        if (!occupiedNorth) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(180, 0, mc.player.onGround));
            int currentSlot = mc.player.inventory.currentItem;
            if (mc.world.getBlockState(playerPos.north().north()).getBlock().equals(Blocks.AIR) && i == 0) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.north().north().getX(), playerPos.north().north().getY(), playerPos.north().north().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                    BlockUtil.placeBlock(playerPos.north().north(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 1;
                }
            }
            if (mc.world.getBlockState(playerPos.north().north()).getBlock().equals(Blocks.OBSIDIAN) && i == 0)
                i = 1;
            if (mc.world.getBlockState(playerPos.north().north().up()).getBlock().equals(Blocks.AIR) && i == 1) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.north().north().up().getX(), playerPos.north().north().up().getY(), playerPos.north().north().up().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)));
                    BlockUtil.placeBlock(playerPos.north().north().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 2;
                }
            }
            if (mc.world.getBlockState(playerPos.north().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(playerPos.north().up().up()).getBlock().equals(Blocks.AIR) && i == 2) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.north().north().getX(), playerPos.north().getY(), playerPos.north().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL));
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.north(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 3;
                }
            }
            if (mc.world.getBlockState(playerPos.north().north().north().up()).getBlock().equals(Blocks.AIR) && i == 3) {
                if (timer.passedMs(redstoneDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.north().north().north().up().getX(), playerPos.north().north().north().up().getY(), playerPos.north().north().north().up().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)));
                    BlockUtil.placeBlock(playerPos.north().north().north().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                }
            }
        }
        if (!occupiedEast && occupiedNorth) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(-90, 0, mc.player.onGround));
            int currentSlot = mc.player.inventory.currentItem;
            if (mc.world.getBlockState(playerPos.east().east()).getBlock().equals(Blocks.AIR) && i == 0) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.east().east().getX(), playerPos.east().east().getY(), playerPos.east().east().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                    BlockUtil.placeBlock(playerPos.east().east(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 1;
                }
            }
            if (mc.world.getBlockState(playerPos.east().east()).getBlock().equals(Blocks.OBSIDIAN) && i == 0)
                i = 1;
            if (mc.world.getBlockState(playerPos.east().east().up()).getBlock().equals(Blocks.AIR) && i == 1) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.east().east().up().getX(), playerPos.east().east().up().getY(), playerPos.east().east().up().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)));
                    BlockUtil.placeBlock(playerPos.east().east().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 2;
                }
            }
            if (mc.world.getBlockState(playerPos.east().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(playerPos.east().up().up()).getBlock().equals(Blocks.AIR) && i == 2) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.east().getX(), playerPos.east().getY(), playerPos.east().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL));
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.east(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 3;
                }
            }
            if (mc.world.getBlockState(playerPos.east().east().east().up()).getBlock().equals(Blocks.AIR) && i == 3) {
                if (timer.passedMs(redstoneDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.east().east().up().getX(), playerPos.east().east().up().getY(), playerPos.east().east().up().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)));
                    BlockUtil.placeBlock(playerPos.east().east().east().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                }
            }
        }
        if (!occupiedSouth && occupiedEast && occupiedNorth) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0.0f, 0, mc.player.onGround));
            int currentSlot = mc.player.inventory.currentItem;
            if (mc.world.getBlockState(playerPos.south().south()).getBlock().equals(Blocks.AIR) && i == 0) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.south().south().getX(), playerPos.south().south().getY(), playerPos.south().south().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                        disableModule();
                    ;
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                    BlockUtil.placeBlock(playerPos.south().south(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 1;
                }
            }
            if (mc.world.getBlockState(playerPos.south().south()).getBlock().equals(Blocks.OBSIDIAN) && i == 0)
                i = 1;
            if (mc.world.getBlockState(playerPos.south().south().up()).getBlock().equals(Blocks.AIR) && i == 1) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.south().south().up().getX(), playerPos.south().south().up().getY(), playerPos.south().south().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)));
                    BlockUtil.placeBlock(playerPos.south().south().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 2;
                }
            }
            if (mc.world.getBlockState(playerPos.south().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(playerPos.south().up().up()).getBlock().equals(Blocks.AIR) && i == 2) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.south().getX(), playerPos.south().south().getY(), playerPos.south().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL));
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.south(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 3;
                }
            }
            if (mc.world.getBlockState(playerPos.south().south().south().up()).getBlock().equals(Blocks.AIR) && i == 3) {
                if (timer.passedMs(redstoneDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.south().south().south().up().getX(), playerPos.south().south().south().up().getY(), playerPos.south().south().south().up().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)));
                    BlockUtil.placeBlock(playerPos.south().south().south().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                }
            }
        }
        if (!occupiedWest && occupiedNorth && occupiedEast && occupiedSouth) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(90.0f, 0, mc.player.onGround));
            int currentSlot = mc.player.inventory.currentItem;
            if (mc.world.getBlockState(playerPos.west().west()).getBlock().equals(Blocks.AIR) && i == 0) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.west().west().getX(), playerPos.west().west().getY(), playerPos.west().west().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)));
                    BlockUtil.placeBlock(playerPos.west().west(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 1;
                }
            }
            if (mc.world.getBlockState(playerPos.west().west()).getBlock().equals(Blocks.OBSIDIAN) && i == 0)
                i = 1;
            if (mc.world.getBlockState(playerPos.west().west().up()).getBlock().equals(Blocks.AIR) && i == 1) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.west().west().up().getX(), playerPos.west().west().up().getY(), playerPos.west().west().up().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON)));
                    BlockUtil.placeBlock(playerPos.west().west().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (blockSwing.getValue())
                        swingArm(true, true);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 2;
                }
            }
            if (mc.world.getBlockState(playerPos.west().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(playerPos.west().up().up()).getBlock().equals(Blocks.AIR) && i == 2) {
                if (timer.passedMs(placeDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.west().getX(), playerPos.west().getY(), playerPos.west().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL));
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(playerPos.west(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                    i = 3;
                }
            }
            if (mc.world.getBlockState(playerPos.west().west().west().up()).getBlock().equals(Blocks.AIR) && i == 3) {
                if (timer.passedMs(redstoneDelay.getValue())) {
                    if (mc.player.getDistance(playerPos.west().west().west().up().getX(), playerPos.west().west().west().up().getY(), playerPos.west().west().west().up().getZ()) > placeRange.getValue())
                        return;
                    if (InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) == -1)
                        disableModule();
                    InventoryUtil.SilentSwitchToSlot(InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)));
                    BlockUtil.placeBlock(playerPos.west().west().west().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, false, EnumHand.MAIN_HAND);
                    if (crystalPlaceSwing.getValue())
                        swingArm(true, false);
                    mc.player.inventory.currentItem = currentSlot;
                    mc.playerController.updateController();
                    timer.reset();
                }
            }
        }

    }

    public void swingArm(boolean place, boolean blockSwing) {
        if (place) {
            if (blockSwing) {
                switch (blockSwingMode.getValue()) {
                    case MAINHAND:
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        break;
                    case OFFHAND:
                        mc.player.swingArm(EnumHand.OFF_HAND);
                        break;
                    case PACKET:
                        mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
                        break;
                }
            } else {
                switch (placeSwingHand.getValue()) {
                    case MAINHAND:
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        break;
                    case OFFHAND:
                        mc.player.swingArm(EnumHand.OFF_HAND);
                        break;
                    case PACKET:
                        mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
                        break;
                }
            }
        } else {
            switch (breakSwingHand.getValue()) {
                case MAINHAND:
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    break;
                case OFFHAND:
                    mc.player.swingArm(EnumHand.OFF_HAND);
                    break;
                case PACKET:
                    mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
                    break;
            }
        }
    }

    void disableModule() {
        MessageManager.sendMessage(ChatFormatting.BOLD + "Auto Piston: " + ChatFormatting.RESET + "Disabled due to missing blocks!");
        disable();
    }


    public void onRender3D(Render3DEvent event) {
        if (target == null)
            return;
        for (BlockPos pos : BlockUtil.getSphere(placeRange.getValue(), true)) {
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.PISTON)) {
                RenderUtil.drawBox(pos, new Color(190, 150, 100, 50));
            }
            if (mc.world.getBlockState(pos).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
                RenderUtil.drawBox(pos, new Color(255, 0, 0, 50));
            }
        }
    }
}
