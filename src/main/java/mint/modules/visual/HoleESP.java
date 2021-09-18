package mint.modules.visual;

import com.google.common.collect.Sets;
import mint.clickgui.setting.Setting;
import mint.events.ClientEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.awt.*;
import java.util.HashSet;

public class HoleESP extends Module {

    public int updates;

    HashSet<BlockPos> bedrockholes = Sets.newHashSet();
    HashSet<BlockPos> obsidianholes = Sets.newHashSet();
    public Setting<Integer> updateDelay = register(new Setting<>("UpdateDelay", 1, 0, 30));
    public Setting<Boolean> rangesParent = register(new Setting("Ranges", true, false));
    public Setting<Integer> range = register(new Setting<>("X-Range", 0, 1, 20, v-> rangesParent.getValue()));
    public Setting<Integer> rangeY = register(new Setting<>("Y-Range", 0, 1, 20, v-> rangesParent.getValue()));
    public Setting<Boolean> bedrockParent = register(new Setting("Bedrock", true, false));
    public Setting<Boolean> bedrockBox = register(new Setting("Box", true, v-> bedrockParent.getValue()));
    public Setting<Integer> bedrockBoxRed = register(new Setting<>( "BoxRed", 0, 0, 255, v-> bedrockBox.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockBoxGreen = register(new Setting<>("BoxGreen", 255, 0, 255, v-> bedrockBox.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockBoxBlue = register(new Setting<>("BoxBlue", 0, 0, 255, v->bedrockBox.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockBoxAlpha = register(new Setting<>("BoxAlpha", 120, 0, 255, v-> bedrockBox.getValue() && bedrockParent.getValue()));
    public Setting<Boolean> bedrockOutline = register(new Setting("Outline", true, v-> bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineRed = register(new Setting<>( "OutlineRed", 0, 0, 255, v-> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineGreen = register(new Setting<>("OutlineGreen", 255, 0, 255, v-> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineBlue = register(new Setting<>("OutlineBlue", 0, 0, 255, v-> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineAlpha = register(new Setting<>("OutlineAlpha", 255, 0, 255, v-> bedrockOutline.getValue() && bedrockParent.getValue()));
    public Setting<Integer> bedrockOutlineLineWidth = register(new Setting<>("OutlineLineWidth", 1, 0, 5, v-> bedrockOutline.getValue() && bedrockParent.getValue()));

    public Setting<Boolean> obsidianParent = register(new Setting("Obsidian", true, false));
    public Setting<Boolean> obsidianBox = register(new Setting("Box", true, v-> obsidianParent.getValue()));
    public Setting<Integer> obsidianBoxRed = register(new Setting<>( "BoxRed", 255, 0, 255, v-> obsidianBox.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianBoxGreen = register(new Setting<>("BoxGreen", 0, 0, 255, v-> obsidianBox.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianBoxBlue = register(new Setting<>("BoxBlue", 0, 0, 255, v-> obsidianBox.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianBoxAlpha = register(new Setting<>("BoxAlpha", 120, 0, 255, v-> obsidianBox.getValue() && obsidianParent.getValue()));
    public Setting<Boolean> obsidianOutline = register(new Setting("Outline", true, v-> obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineRed = register(new Setting<>( "OutlineRed", 255, 0, 255, v-> obsidianOutline.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineGreen = register(new Setting<>("OutlineGreen", 0, 0, 255, v-> obsidianOutline.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineBlue = register(new Setting<>("OutlineBlue", 0, 0, 255, v-> obsidianOutline.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineAlpha = register(new Setting<>("OutlineAlpha", 255, 0, 255, v-> obsidianOutline.getValue() && obsidianParent.getValue()));
    public Setting<Integer> obsidianOutlineLineWidth = register(new Setting<>("OutlineLineWidth", 1, 0, 5, v-> obsidianOutline.getValue() && obsidianParent.getValue()));

    public HoleESP() {
        super("Hole ESP", Category.VISUAL, "Shows where safe holes are.");
    }

    public void onTick() {
            if (updates > updateDelay.getValue()) {
                updates = 0;
            } else {
                ++updates;
        }
    }

    public void onEnable(){
        updates = 0;
    }

    public void onRender3D(Render3DEvent event) {
        for (BlockPos pos : bedrockholes) {
            RenderUtil.drawBoxESPFlat(pos, new Color(ColorUtil.toRGBA(bedrockBoxRed.getValue(), bedrockBoxGreen.getValue(), bedrockBoxBlue.getValue(), bedrockBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(bedrockOutlineRed.getValue(), bedrockOutlineGreen.getValue(), bedrockOutlineBlue.getValue(), bedrockOutlineAlpha.getValue())), bedrockOutlineLineWidth.getValue(), bedrockOutline.getValue(), bedrockBox.getValue(), bedrockBoxAlpha.getValue(), true);
        }
        for (BlockPos pos : obsidianholes) {
            RenderUtil.drawBoxESPFlat(pos, new Color(ColorUtil.toRGBA(obsidianBoxRed.getValue(), obsidianBoxGreen.getValue(), obsidianBoxBlue.getValue(), obsidianBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(obsidianOutlineRed.getValue(), obsidianOutlineGreen.getValue(), obsidianOutlineBlue.getValue(), obsidianOutlineAlpha.getValue())), obsidianOutlineLineWidth.getValue(), obsidianOutline.getValue(), obsidianBox.getValue(), obsidianBoxAlpha.getValue(), true);
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
                    if(updates > updateDelay.getValue()) {
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
    public String hudInfoString(){
        return updates + " | " + Minecraft.getDebugFPS();
    }
}