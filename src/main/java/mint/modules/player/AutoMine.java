package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.PlayerUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class AutoMine extends Module {

    public Setting<MineMode> mineMode = register(new Setting<>("Mine Mode", MineMode.Normal));
    public enum MineMode {Normal, Combat}

    public Setting<Priority> minePriority = register(new Setting<>("Mine Priority", Priority.Surrounds, v -> mineMode.getValue() == MineMode.Combat));
    public enum Priority {Surrounds, City, Dynamic}

    public Setting<Float> targetRange = register(new Setting<>("Target Range", 9.0f, 0.0f, 15.0f, v -> mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Float> mineRange = register(new Setting<>("Mine Range", 5.0f, 0.0f, 6.0f, v -> mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Boolean> boxParent = register(new Setting("Box", false, true, v -> mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Boolean> boxSetting = register(new Setting("Box Setting", false, v -> boxParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Integer> boxRed = register(new Setting<>("Box Red", 255, 0, 255, v -> boxParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Integer> boxGreen = register(new Setting<>("Box Green", 255, 0, 255, v -> boxParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Integer> boxBlue = register(new Setting<>("Box Blue", 255, 0, 255, v -> boxParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Integer> boxAlpha = register(new Setting<>("Box Alpha", 120, 0, 255, v -> boxParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Boolean> outlineParent = register(new Setting("Outline", false, true, v -> mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Boolean> outlineSetting = register(new Setting("Outline Setting", false, v -> outlineParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Integer> outlineRed = register(new Setting<>("Outline Red", 255, 0, 255, v -> outlineParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Integer> outlineGreen = register(new Setting<>("Outline Green", 255, 0, 255, v -> outlineParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Integer> outlineBlue = register(new Setting<>("Outline Blue", 255, 0, 255, v -> outlineParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));
    public Setting<Integer> outlineAlpha = register(new Setting<>("Outline Alpha", 120, 0, 255, v -> outlineParent.getValue() && mineMode.getValue().equals(MineMode.Combat)));

    BlockPos targetBlock = null;
    Timer timer = new Timer();

    public AutoMine() {
        super("Auto Mine", Category.PLAYER, "Automatically mines stuff.");
    }

    public void onDisable() {
        if (mineMode.getValue().equals(MineMode.Normal)) {
            mc.gameSettings.keyBindAttack.pressed = false;
        }
    }

    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }

        EntityPlayer target = EntityUtil.getTarget(targetRange.getValue());
        if (mineMode.getValue() == MineMode.Normal) {
            mc.gameSettings.keyBindAttack.pressed = true;
        }

        if (mineMode.getValue().equals(MineMode.Combat)) {

            if (target == null) return;

            BlockPos pos = PlayerUtil.getPlayerPos(target);

            if (EntityUtil.isPlayerSafe(target)) {
                if (minePriority.getValue().equals(Priority.Surrounds)) {
                    if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.north();
                    } else if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.east();
                    } else if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.south();
                    } else if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.west();
                    } else {
                        targetBlock = null;
                    }
                }
                if (minePriority.getValue().equals(Priority.City)) {
                    if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north().north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().north().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.north();
                    } else if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.east().east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().east().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.east();
                    } else if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.south().south()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().south().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.south();
                    } else if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west().west()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().west().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.west();
                    } else {
                        targetBlock = null;
                    }
                }
                if (minePriority.getValue().equals(Priority.Dynamic)) {
                    if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north().north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().north().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.north();
                    } else if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.east().east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().east().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.east();
                    } else if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.south().south()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().south().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.south();
                    } else if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west().west()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().west().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.west();
                    } else if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.north();
                    } else if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.east();
                    } else if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.south();
                    } else if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.west();
                    } else {
                        targetBlock = null;
                    }
                }
            }
            if (targetBlock != null && mc.world.getBlockState(targetBlock).getBlock().equals(Blocks.AIR)){
                targetBlock = null;
            }
        }

        if (targetBlock != null) {
            timer.reset();
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetBlock, EnumFacing.UP));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetBlock, EnumFacing.UP));
        }
    }

    public void onRender3D(Render3DEvent event) {
        if (fullNullCheck()) {
            return;
        }

        if (targetBlock != null && !mc.world.getBlockState(targetBlock).getBlock().equals(Blocks.AIR)) {
            RenderUtil.drawBoxESP(targetBlock, new Color(boxRed.getValue(), boxGreen.getValue(), boxBlue.getValue(), boxAlpha.getValue()), true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), 1, outlineSetting.getValue(), boxSetting.getValue(), boxAlpha.getValue(), true);
        }
    }
}