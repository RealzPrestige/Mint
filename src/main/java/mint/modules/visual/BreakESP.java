package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.RenderUtil;

import java.awt.*;

/**
 * @Author zPrestige_
 * @Since 24/09/2021
 */

public class BreakESP extends Module {
    public Setting<Boolean> miscParent = register(new Setting("Misc", true, false));
    public Setting<Integer> range = register(new Setting("Range", 50, 0, 200, v -> miscParent.getValue()));

    public Setting<Boolean> visual = register(new Setting("Visuals", true, false));
    public Setting<Boolean> playerName = register(new Setting("Player Name", false, v -> visual.getValue()));
    public Setting<Boolean> breakPercentage = register(new Setting("Break Percentage", false, v -> visual.getValue()));

    public Setting<Boolean> boxParent = register(new Setting("Boxes", true, false));
    public Setting<Boolean> box = register(new Setting("Box", false, v -> boxParent.getValue()));
    public Setting<Integer> boxRed = register(new Setting<>("Box Red", 255, 0, 255, v -> boxParent.getValue()));
    public Setting<Integer> boxGreen = register(new Setting<>("Box Green", 255, 0, 255, v -> boxParent.getValue()));
    public Setting<Integer> boxBlue = register(new Setting<>("Box Blue", 255, 0, 255, v -> boxParent.getValue()));
    public Setting<Integer> boxAlpha = register(new Setting<>("Box Alpha", 150, 0, 255, v -> boxParent.getValue()));

    public Setting<Boolean> outlineParent = register(new Setting("Outlines", true, false));
    public Setting<Boolean> outline = register(new Setting("Outline", false, v -> outlineParent.getValue()));
    public Setting<Integer> outlineRed = register(new Setting<>("Outline Red", 255, 0, 255, v -> outlineParent.getValue() && outline.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("Outline Green", 255, 0, 255, v -> outlineParent.getValue() && outline.getValue()));
    public Setting<Integer> outlineBlue = register(new Setting<>("Outline Blue", 255, 0, 255, v -> outlineParent.getValue() && outline.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("Outline Alpha", 150, 0, 255, v -> outlineParent.getValue() && outline.getValue()));
    public Setting<Float> lineWidth = register(new Setting<>("LineWidth", 3.0f, 0.1f, 5.0f, v -> outlineParent.getValue() && outline.getValue()));

    public BreakESP() {
        super("BreakESP", Category.VISUAL, "Renders when a block is being broken.");
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress.getPosition().getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {
                RenderUtil.drawBoxESP(destroyBlockProgress.getPosition(), new Color(boxRed.getValue(), boxGreen.getValue(), boxBlue.getValue(), boxAlpha.getValue()), true, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()), lineWidth.getDefaultValue(), outline.getValue(), box.getValue(), boxAlpha.getDefaultValue(), true);
                RenderUtil.drawText(destroyBlockProgress.getPosition(), (playerName.getValue() ? mc.world.getEntityByID(integer).getName() : "") + (breakPercentage.getValue() ? " " + (destroyBlockProgress.getPartialBlockDamage() * 12.5) + "%" : ""), -1);
            }
        });
    }
}
