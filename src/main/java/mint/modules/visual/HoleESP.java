package mint.modules.visual;

import com.google.common.collect.Sets;
import mint.clickgui.setting.Setting;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.HashSet;

public class HoleESP extends Module {

    /**
     * @author zPrestige
     */

    public int updates;

    HashSet<BlockPos> bedrockholes = Sets.newHashSet();
    HashSet<BlockPos> obsidianholes = Sets.newHashSet();
     public Setting<Boolean> rangesParent = register(new Setting("Ranges", true, false));
    public Setting<Integer> range = register(new Setting<>("X Range", 8, 1, 20, v -> rangesParent.getValue()));
    public Setting<Integer> rangeY = register(new Setting<>("Y Range", 6, 1, 20, v -> rangesParent.getValue()));
    public Setting<Boolean> othersParent = register(new Setting("Others", true, false));
    public Setting<Integer> updateDelay = register(new Setting<>("Update Delay", 1, 0, 30, v -> othersParent.getValue()));
    public Setting<Boolean> gradient = register(new Setting("Gradient", false, v -> othersParent.getValue()));
    public Setting<Boolean> dynamicHeights = register(new Setting("Dynamic Height", false, v -> gradient.getValue() && othersParent.getValue()));
    public Setting<Double> height = register(new Setting<>("Height", 1.0, 0.0, 3.0, v -> gradient.getValue() && othersParent.getValue()));
    public Setting<Double> value = register(new Setting<>("height Value", 10.0, 0.1, 30.0, v -> gradient.getValue() && othersParent.getValue()));
    public Setting<Boolean> antiInverse = register(new Setting("Anti Inverse", false, v -> gradient.getValue() && othersParent.getValue()));
    public Setting<Boolean> bedrockParent = register(new Setting("Bedrock", true, false));
    public Setting<Boolean> bedrockBox = register(new Setting("Bedrock Box", true, v -> bedrockParent.getValue()));
    public Setting<Integer> bedrockBoxRed = register(new Setting<>("Bedrock Box Red", 0, 0, 255, v -> bedrockBox.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockBoxGreen = register(new Setting<>("Bedrock Box Green", 255, 0, 255, v -> bedrockBox.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockBoxBlue = register(new Setting<>("Bedrock Box Blue", 0, 0, 255, v -> bedrockBox.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockBoxAlpha = register(new Setting<>("Bedrock Box Alpha", 120, 0, 255, v -> bedrockBox.getValue() && bedrockParent.getValue()));
    public Setting<Boolean> bedrockOutline = register(new Setting("Bedrock Outline", true, v -> bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineRed = register(new Setting<>("Bedrock Outline Red", 0, 0, 255, v -> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineGreen = register(new Setting<>("Bedrock Outline Green", 255, 0, 255, v -> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineBlue = register(new Setting<>("Bedrock Outline Blue", 0, 0, 255, v -> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineAlpha = register(new Setting<>("Bedrock Outline Alpha", 255, 0, 255, v -> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineLineWidth = register(new Setting<>("Bedrock Outline Line Width", 1, 0, 5, v -> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Boolean> obsidianParent = register(new Setting("Obsidian", true, false));
    public Setting<Boolean> obsidianBox = register(new Setting("Obsidian Box", true, v -> obsidianParent.getValue()));
    public Setting<Integer> obsidianBoxRed = register(new Setting<>("Obsidian Box Red", 255, 0, 255, v -> obsidianBox.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianBoxGreen = register(new Setting<>("Obsidian Box Green", 0, 0, 255, v -> obsidianBox.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianBoxBlue = register(new Setting<>("Obsidian Box Blue", 0, 0, 255, v -> obsidianBox.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianBoxAlpha = register(new Setting<>("Obsidian Box Alpha", 120, 0, 255, v -> obsidianBox.getValue() && obsidianParent.getValue()));
    public Setting<Boolean> obsidianOutline = register(new Setting("Obsidian Outline", true, v -> obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineRed = register(new Setting<>("Obsidian Outline Red", 255, 0, 255, v -> obsidianOutline.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineGreen = register(new Setting<>("Obsidian Outline Green", 0, 0, 255, v -> obsidianOutline.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineBlue = register(new Setting<>("Obsidian Outline Blue", 0, 0, 255, v -> obsidianOutline.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineAlpha = register(new Setting<>("Obsidian Outline Alpha", 255, 0, 255, v -> obsidianOutline.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineLineWidth = register(new Setting<>("Obsidian Outline Line Width", 1, 0, 5, v -> obsidianOutline.getValue() && obsidianParent.getValue()));

    public HoleESP() {
        super("Hole ESP", Category.VISUAL, "Draws a box around safe holes for crystal PVP.");
    }

    public void onTick() {
        if (updates > updateDelay.getValue()) {
            updates = 0;
        } else {
            ++updates;
        }
    }

    public void onToggle() {
        bedrockholes.clear();
        obsidianholes.clear();
    }

    public void onEnable() {
        updates = 0;
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        for (BlockPos pos : bedrockholes) {
            double dynamicHeight = height.getValue() - mc.player.getDistanceSq(pos) / (range.getValue() * value.getValue());
            double finalDynamicHeight = (antiInverse.getValue() && dynamicHeight < -1) ? -1 : dynamicHeight;
            if (gradient.getValue()) {
                RenderUtil.drawGlowBox(pos, new Color(bedrockBoxRed.getValue(), bedrockBoxGreen.getValue(), bedrockBoxBlue.getValue(), bedrockBoxAlpha.getValue()), dynamicHeights.getValue() ? finalDynamicHeight : height.getValue() - 1);
            }
            RenderUtil.drawBoxESPFlat(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), new Color(ColorUtil.toRGBA(bedrockBoxRed.getValue(), bedrockBoxGreen.getValue(), bedrockBoxBlue.getValue(), bedrockBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(bedrockOutlineRed.getValue(), bedrockOutlineGreen.getValue(), bedrockOutlineBlue.getValue(), bedrockOutlineAlpha.getValue())), bedrockOutlineLineWidth.getValue(), bedrockOutline.getValue(), bedrockBox.getValue(), bedrockBoxAlpha.getValue(), true);
        }
        for (BlockPos pos : obsidianholes) {
            double dynamicHeight = height.getValue() - mc.player.getDistanceSq(pos) / (range.getValue() * value.getValue());
            double finalDynamicHeight = (antiInverse.getValue() && dynamicHeight < -1) ? -1 : dynamicHeight;
            if (gradient.getValue()) {
                RenderUtil.drawGlowBox(pos, new Color(obsidianBoxRed.getValue(), obsidianBoxGreen.getValue(), obsidianBoxBlue.getValue(), obsidianBoxAlpha.getValue()), dynamicHeights.getValue() ? finalDynamicHeight : height.getValue() - 1);
            }
            RenderUtil.drawBoxESPFlat(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), new Color(ColorUtil.toRGBA(obsidianBoxRed.getValue(), obsidianBoxGreen.getValue(), obsidianBoxBlue.getValue(), obsidianBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(obsidianOutlineRed.getValue(), obsidianOutlineGreen.getValue(), obsidianOutlineBlue.getValue(), obsidianOutlineAlpha.getValue())), obsidianOutlineLineWidth.getValue(), obsidianOutline.getValue(), obsidianBox.getValue(), obsidianBoxAlpha.getValue(), true);
        }
        if (updates > updateDelay.getValue()) {
            obsidianholes.clear();
            bedrockholes.clear();
            findHoles();
        }
    }

    public void findHoles() {
        assert (mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - this.range.getValue(); x < playerPos.getX() + this.range.getValue(); ++x) {
            for (int z = playerPos.getZ() - this.range.getValue(); z < playerPos.getZ() + this.range.getValue(); ++z) {
                for (int y = playerPos.getY() + this.rangeY.getValue(); y > playerPos.getY() - this.rangeY.getValue(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (updates > updateDelay.getValue()) {
                        if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            bedrockholes.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)) {
                            obsidianholes.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            bedrockholes.add(pos);
                            bedrockholes.add(pos.north());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK)) {
                            obsidianholes.add(pos);
                            obsidianholes.add(pos.north());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK) {
                            bedrockholes.add(pos);
                            bedrockholes.add(pos.west());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.OBSIDIAN)) {
                            obsidianholes.add(pos);
                            obsidianholes.add(pos.west());
                        }
                    }
                }
            }
        }
    }

    public String hudInfoString() {
        return updates + " | " + Minecraft.getDebugFPS();
    }
}