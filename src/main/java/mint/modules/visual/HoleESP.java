package mint.modules.visual;

import com.google.common.collect.Sets;
import mint.clickgui.setting.Setting;
import mint.events.ClientEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;
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

    public Setting<Boolean> holes = register(new Setting<>("Holes", true));
    public Setting<Integer> range = register(new Setting<>("X-Range", 0, 1, 20, v-> holes.getValue()));
    public Setting<Integer> rangeY = register(new Setting<>("Y-Range", 0, 1, 20, v-> holes.getValue()));
    public Setting<Integer> updateDelay = register(new Setting<>("UpdateDelay", 1, 0, 30, v-> holes.getValue()));

    public Setting<Boolean> bedrockBox = register(new Setting<>("BedrockBox", true, v-> holes.getValue()));
    public Setting<Boolean> bedrockFlat = register(new Setting<>("BedrockFlat", true, v-> holes.getValue()));
    public Setting<Integer> bedrockBoxRed = register(new Setting<>( "BedrockBoxRed", 0, 0, 255, v-> holes.getValue() && bedrockBox.getValue()));
    public Setting<Integer> bedrockBoxGreen = register(new Setting<>("BedrockBoxGreen", 255, 0, 255, v-> holes.getValue() && bedrockBox.getValue()));
    public Setting<Integer> bedrockBoxBlue = register(new Setting<>("BedrockBoxBlue", 0, 0, 255, v-> holes.getValue() && bedrockBox.getValue()));
    public Setting<Integer> bedrockBoxAlpha = register(new Setting<>("BedrockBoxAlpha", 120, 0, 255, v-> holes.getValue() && bedrockBox.getValue()));
    public Setting<Boolean> bedrockOutline = register(new Setting<>("BedrockOutline", true, v-> holes.getValue()));
    public Setting<Integer> bedrockOutlineRed = register(new Setting<>( "BedrockOutlineRed", 0, 0, 255, v-> holes.getValue() && bedrockOutline.getValue()));
    public Setting<Integer> bedrockOutlineGreen = register(new Setting<>("BedrockOutlineGreen", 255, 0, 255, v-> holes.getValue() && bedrockOutline.getValue()));
    public Setting<Integer> bedrockOutlineBlue = register(new Setting<>("BedrockOutlineBlue", 0, 0, 255, v-> holes.getValue() && bedrockOutline.getValue()));
    public Setting<Integer> bedrockOutlineAlpha = register(new Setting<>("BedrockOutlineAlpha", 255, 0, 255, v-> holes.getValue() && bedrockOutline.getValue()));
    public Setting<Integer> bedrockOutlineLineWidth = register(new Setting<>("BedrockOutlineLineWidth", 1, 0, 5, v-> holes.getValue() && bedrockOutline.getValue()));

    public Setting<Boolean> obsidianBox = register(new Setting<>("ObsidianBox", true, v-> holes.getValue()));
    public Setting<Boolean> obsidianFlat = register(new Setting<>("ObsidianFlat", false, v-> holes.getValue()));
    public Setting<Integer> obsidianBoxRed = register(new Setting<>( "ObsidianBoxRed", 255, 0, 255, v-> holes.getValue() && obsidianBox.getValue()));
    public Setting<Integer> obsidianBoxGreen = register(new Setting<>("ObsidianBoxGreen", 0, 0, 255, v-> holes.getValue() && obsidianBox.getValue()));
    public Setting<Integer> obsidianBoxBlue = register(new Setting<>("ObsidianBoxBlue", 0, 0, 255, v-> holes.getValue() && obsidianBox.getValue()));
    public Setting<Integer> obsidianBoxAlpha = register(new Setting<>("ObsidianBoxAlpha", 120, 0, 255, v-> holes.getValue() && obsidianBox.getValue()));
    public Setting<Boolean> obsidianOutline = register(new Setting<>("ObsidianOutline", true, v-> holes.getValue()));
    public Setting<Integer> obsidianOutlineRed = register(new Setting<>( "ObsidianOutlineRed", 255, 0, 255, v-> holes.getValue() && obsidianOutline.getValue()));
    public Setting<Integer> obsidianOutlineGreen = register(new Setting<>("ObsidianOutlineGreen", 0, 0, 255, v-> holes.getValue() && obsidianOutline.getValue()));
    public Setting<Integer> obsidianOutlineBlue = register(new Setting<>("ObsidianOutlineBlue", 0, 0, 255, v-> holes.getValue() && obsidianOutline.getValue()));
    public Setting<Integer> obsidianOutlineAlpha = register(new Setting<>("ObsidianOutlineAlpha", 255, 0, 255, v-> holes.getValue() && obsidianOutline.getValue()));
    public Setting<Integer> obsidianOutlineLineWidth = register(new Setting<>("ObsidianOutlineLineWidth", 1, 0, 5, v-> holes.getValue() && obsidianOutline.getValue()));

    public HoleESP() {
        super("Hole ESP", Category.VISUAL, "Shows where safe holes are.");
    }

    public void onTick() {
        if (holes.getValue()) {
            if (updates > updateDelay.getValue()) {
                updates = 0;
            } else {
                ++updates;
            }
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if(!holes.getValue()) {
            obsidianholes.clear();
            bedrockholes.clear();
            updates = 0;
        }
    }

    public void onEnable(){
        updates = 0;
    }
    public void onRender3D(Render3DEvent event) {
        for (BlockPos pos : bedrockholes) {
            if (bedrockFlat.getValue()) {
                RenderUtil.drawBoxESPFlat(pos, new Color(ColorUtil.toRGBA(bedrockBoxRed.getValue(), bedrockBoxGreen.getValue(), bedrockBoxBlue.getValue(), bedrockBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(bedrockOutlineRed.getValue(), bedrockOutlineGreen.getValue(), bedrockOutlineBlue.getValue(), bedrockOutlineAlpha.getValue())), bedrockOutlineLineWidth.getValue(), bedrockOutline.getValue(), bedrockBox.getValue(), bedrockBoxAlpha.getValue(), true);
            } else {
                RenderUtil.drawBoxESP(pos, new Color(ColorUtil.toRGBA(bedrockBoxRed.getValue(), bedrockBoxGreen.getValue(), bedrockBoxBlue.getValue(), bedrockBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(bedrockOutlineRed.getValue(), bedrockOutlineGreen.getValue(), bedrockOutlineBlue.getValue(), bedrockOutlineAlpha.getValue())), bedrockOutlineLineWidth.getValue(), bedrockOutline.getValue(), bedrockBox.getValue(), bedrockBoxAlpha.getValue(), true);
            }
        }
        for (BlockPos pos : obsidianholes) {
            if (obsidianFlat.getValue()) {
                RenderUtil.drawBoxESPFlat(pos, new Color(ColorUtil.toRGBA(obsidianBoxRed.getValue(), obsidianBoxGreen.getValue(), obsidianBoxBlue.getValue(), obsidianBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(obsidianOutlineRed.getValue(), obsidianOutlineGreen.getValue(), obsidianOutlineBlue.getValue(), obsidianOutlineAlpha.getValue())), obsidianOutlineLineWidth.getValue(), obsidianOutline.getValue(), obsidianBox.getValue(), obsidianBoxAlpha.getValue(), true);
            } else {
                RenderUtil.drawBoxESP(pos, new Color(ColorUtil.toRGBA(obsidianBoxRed.getValue(), obsidianBoxGreen.getValue(), obsidianBoxBlue.getValue(), obsidianBoxAlpha.getValue())), true, new Color(ColorUtil.toRGBA(obsidianOutlineRed.getValue(), obsidianOutlineGreen.getValue(), obsidianOutlineBlue.getValue(), obsidianOutlineAlpha.getValue())), obsidianOutlineLineWidth.getValue(), obsidianOutline.getValue(), obsidianBox.getValue(), obsidianBoxAlpha.getValue(), true);
            }
        }
        if (updates > updateDelay.getValue() && holes.getValue()) {
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