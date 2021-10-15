package mint.modules.miscellaneous;

import mint.clickgui.setting.Setting;
import mint.events.ChorusEvent;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class ChorusPredict extends Module {

    public Setting<Integer> time = register(new Setting<>("Duration", 500, 50, 3000));
    public Setting<Boolean> blocker = register(new Setting<>("Blocker", true));
    public Setting<Boolean> boxParent = register(new Setting<>("Box", true, false));
    public Setting<Boolean> box = register(new Setting("BoxSetting", true, v -> boxParent.getValue()));
    public Setting<Integer> boxRed = register(new Setting<>("BoxRed", 255, 0, 255, v -> box.getValue() && boxParent.getValue()));
    public Setting<Integer> boxGreen = register(new Setting<>("BoxGreen", 255, 0, 255, v -> box.getValue() && boxParent.getValue()));
    public Setting<Integer> boxBlue = register(new Setting<>("BoxBlue", 255, 0, 255, v -> box.getValue() && boxParent.getValue()));
    public Setting<Integer> boxAlpha = register(new Setting<>("BoxAlpha", 120, 0, 255, v -> box.getValue() && boxParent.getValue()));
    public Setting<Boolean> outlineParent = register(new Setting<>("Outline", true, false));
    public Setting<Boolean> outline = register(new Setting<>("OutlineSetting", true, false, v -> outlineParent.getValue()));
    public Setting<Integer> outlineRed = register(new Setting<>("OutlineRed", 255, 0, 255, v -> outline.getValue() && outlineParent.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("OutlineGreen", 255, 0, 255, v -> outline.getValue() && outlineParent.getValue()));
    public Setting<Integer> outlineBlue = register(new Setting<>("OutlineBlue", 255, 0, 255, v -> outline.getValue() && outlineParent.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("OutlineAlpha", 255, 0, 255, v -> outline.getValue() && outlineParent.getValue()));
    public Setting<Float> lineWidth = register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f, v -> outline.getValue() && outlineParent.getValue()));
    public Timer timer = new Timer();
    public double x;
    public double y;
    public double z;
    long startTime;
    int alpha = boxAlpha.getValue();
    int alphaOutline = outlineAlpha.getValue();
    long urMom;
    double normal;

    public ChorusPredict() {
        super("Chorus Predict", Category.MISCELLANEOUS, "Renders where a player's chorus will go to.");
    }

    public void onLogin() {
        if (isEnabled()) {
            disable();
            enable();
        }
    }

    @SubscribeEvent
    public void onEntityChorus(ChorusEvent event) {
        x = event.getEventPosX();
        y = event.getEventY();
        z = event.getEventZ();
        timer.reset();
        startTime = System.currentTimeMillis();
    }
    @Override
    public void renderWorldLastEvent(RenderWorldEvent render3DEvent) {
        if (timer.passedMs(time.getValue())) return;
        if (System.currentTimeMillis() - this.startTime > this.time.getValue().longValue()) {
            urMom = System.currentTimeMillis() - this.startTime - this.time.getValue().longValue();
            normal = normalize(urMom, 0.0, this.time.getValue().doubleValue());
            normal = MathHelper.clamp(normal, 0.0, 1.0);
            normal = -normal + 1.0;
            alpha = (int) (normal * (double) alpha);
            alphaOutline = (int) (normal * (double) alpha);
        }
        AxisAlignedBB pos = RenderUtil.interpolateAxis(new AxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3));
        if (outline.getValue()) {
            RenderUtil.drawBlockOutline(pos, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), alphaOutline), lineWidth.getValue());
        }
        if (box.getValue()) {
            RenderUtil.drawFilledBox(pos, ColorUtil.toRGBA(boxRed.getValue(), boxGreen.getValue(), boxBlue.getValue(), alpha));

        }
    }

    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}