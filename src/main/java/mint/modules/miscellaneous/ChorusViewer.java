package mint.modules.miscellaneous;

import mint.clickgui.setting.Setting;
import mint.events.ChorusEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.awt.*;

public class ChorusViewer extends Module {
    private final Setting<Integer> time = this.register(new Setting<>("Duration", 500, 50, 3000));
    private final Setting<Boolean> box = this.register(new Setting<>("Box", true));
    private final Setting<Boolean> outline = this.register(new Setting<>("Outline", true));
    private final Setting<Integer> boxRed = this.register(new Setting<>("BoxRed", 255, 0, 255, v -> this.box.getValue()));
    private final Setting<Integer> boxGreen = this.register(new Setting<>("BoxGreen", 255, 0, 255, v -> this.box.getValue()));
    private final Setting<Integer> boxBlue = this.register(new Setting<>("BoxBlue", 255, 0, 255, v -> this.box.getValue()));
    private final Setting<Integer> boxAlpha = this.register(new Setting<>("BoxAlpha", 120, 0, 255, v -> this.box.getValue()));
    private final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f, v -> this.outline.getValue()));
    private final Setting<Integer> outlineRed = this.register(new Setting<>("OutlineRed", 255, 0, 255, v -> this.outline.getValue()));
    private final Setting<Integer> outlineGreen = this.register(new Setting<>("OutlineGreen", 255, 0, 255, v -> this.outline.getValue()));
    private final Setting<Integer> outlineBlue = this.register(new Setting<>("OutlineBlue", 255, 0, 255, v -> this.outline.getValue()));
    private final Setting<Integer> outlineAlpha = this.register(new Setting<>("OutlineAlpha", 255, 0, 255, v -> this.outline.getValue()));
    private final Timer timer = new Timer();
    private double x;
    private double y;
    private double z;

    public ChorusViewer() {
        super("Chorus Predict", Category.MISCELLANEOUS, "Renders where a player's chorus will go to.");
    }

    public void onLogin(){
        if(this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }
    @SubscribeEvent
    public void onEntityChorus(ChorusEvent event) {
        this.x = event.getEventPosX();
        this.y = event.getEventY();
        this.z = event.getEventZ();
        this.timer.reset();
    }

    @Override
    public void onRender3D(Render3DEvent render3DEvent) {
        if (timer.passedMs(time.getValue())) return;
        AxisAlignedBB pos = RenderUtil.interpolateAxis(new AxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3));
        if (this.outline.getValue()) {
            RenderUtil.drawBlockOutline(pos, new Color(this.outlineRed.getValue(), this.outlineGreen.getValue(), this.outlineBlue.getValue(), this.outlineAlpha.getValue()), this.lineWidth.getValue());
        }
        if (this.box.getValue()) {
            RenderUtil.drawFilledBox(pos, ColorUtil.toRGBA(this.boxRed.getValue(), this.boxGreen.getValue(), this.boxBlue.getValue(), this.boxAlpha.getValue()));
        }
    }
}