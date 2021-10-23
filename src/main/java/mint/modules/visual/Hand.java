package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderItemEvent;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Hand extends Module {
    static Hand INSTANCE = new Hand();

    public Setting<Boolean> mainhandParent = register(new Setting<>("Mainhand", true, false));

    public Setting<Boolean> mainhandTranslation = register(new Setting<>("Mainhand Translation", false, true, v -> mainhandParent.getValue()));
    public Setting<Float> mainhandX = register(new Setting<>("Mainhand X", 0.0f, -10.0f, 10.0f, v -> mainhandParent.getValue() && mainhandTranslation.getValue()));
    public Setting<Float> mainhandY = register(new Setting<>("Mainhand Y", 0.0f, -10.0f, 10.0f, v -> mainhandParent.getValue() && mainhandTranslation.getValue()));
    public Setting<Float> mainhandZ = register(new Setting<>("Mainhand Z", 0.0f, -10.0f, 10.0f, v -> mainhandParent.getValue() && mainhandTranslation.getValue()));

    public Setting<Boolean> mainhandScaling = register(new Setting<>("Mainhand Scaling", false, true, v -> mainhandParent.getValue()));
    public Setting<Float> mainhandScaleX = register(new Setting<>("Mainhand Scale X", 0.0f, -10.0f, 10.0f, v -> mainhandParent.getValue() && mainhandScaling.getValue()));
    public Setting<Float> mainhandScaleY = register(new Setting<>("Mainhand Scale Y", 0.0f, -10.0f, 10.0f, v -> mainhandParent.getValue() && mainhandScaling.getValue()));
    public Setting<Float> mainhandScaleZ = register(new Setting<>("Mainhand Scale Z", 0.0f, -10.0f, 10.0f, v -> mainhandParent.getValue() && mainhandScaling.getValue()));

    public Setting<Boolean> mainhandRotation = register(new Setting<>("Mainhand Rotation", false, true, v -> mainhandParent.getValue()));
    public Setting<Float> mainhandRotationX = register(new Setting<>("Mainhand Rotation X", 0.0f, 0.0f, 10.0f, v -> mainhandParent.getValue() && mainhandRotation.getValue()));
    public Setting<Float> mainhandRotationY = register(new Setting<>("Mainhand Rotation Y", 0.0f, 0.0f, 10.0f, v -> mainhandParent.getValue() && mainhandRotation.getValue()));
    public Setting<Float> mainhandRotationZ = register(new Setting<>("Mainhand Rotation Z", 0.0f, 0.0f, 10.0f, v -> mainhandParent.getValue() && mainhandRotation.getValue()));

    public Setting<Boolean> offhandParent = register(new Setting<>("Offhand", true, false));

    public Setting<Boolean> offhandTranslation = register(new Setting<>("Offhand Translation", false, true, v -> offhandParent.getValue()));
    public Setting<Float> offhandX = register(new Setting<>("Offhand X", 0.0f, -10.0f, 10.0f, v -> offhandParent.getValue() && offhandTranslation.getValue()));
    public Setting<Float> offhandY = register(new Setting<>("Offhand Y", 0.0f, -10.0f, 10.0f, v -> offhandParent.getValue() && offhandTranslation.getValue()));
    public Setting<Float> offhandZ = register(new Setting<>("Offhand Z", 0.0f, -10.0f, 10.0f, v -> offhandParent.getValue() && offhandTranslation.getValue()));

    public Setting<Boolean> offhandScaling = register(new Setting<>("Offhand Scaling", false, true, v -> offhandParent.getValue()));
    public Setting<Float> offhandScaleX = register(new Setting<>("Offhand Scale X", 0.0f, -10.0f, 10.0f, v -> offhandParent.getValue() && offhandScaling.getValue()));
    public Setting<Float> offhandScaleY = register(new Setting<>("Offhand Scale Y", 0.0f, -10.0f, 10.0f, v -> offhandParent.getValue() && offhandScaling.getValue()));
    public Setting<Float> offhandScaleZ = register(new Setting<>("Offhand Scale Z", 0.0f, -10.0f, 10.0f, v -> offhandParent.getValue() && offhandScaling.getValue()));

    public Setting<Boolean> offhandRotation = register(new Setting<>("Offhand Rotation", false, true, v -> offhandParent.getValue()));
    public Setting<Float> offhandRotationX = register(new Setting<>("Offhand Rotation X", 0.0f, 0.0f, 10.0f, v -> offhandParent.getValue() && offhandRotation.getValue()));
    public Setting<Float> offhandRotationY = register(new Setting<>("Offhand Rotation Y", 0.0f, 0.0f, 10.0f, v -> offhandParent.getValue() && offhandRotation.getValue()));
    public Setting<Float> offhandRotationZ = register(new Setting<>("Offhand Rotation Z", 0.0f, 0.0f, 10.0f, v -> offhandParent.getValue() && offhandRotation.getValue()));

    public Setting<Float> alpha = register(new Setting("Item Opacity", 255.0f, 0.0f, 255.0f));


    public Hand() {
        super("Hand", Category.VISUAL, "");
        this.setInstance();
    }

    public static Hand getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Hand();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderMainhand(RenderItemEvent.MainHand event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;

        if (!event.getItemStack().getItem().equals(mc.player.getHeldItemMainhand().getItem()))
            return;

        GL11.glTranslated(mainhandX.getValue() / 40.0f, mainhandY.getValue() / 40.0f, mainhandZ.getValue() / 40.0f);
        GlStateManager.scale((mainhandScaleX.getValue() / 10.0f) + 1.0f, (mainhandScaleY.getValue() / 10.0f) + 1.0f, (mainhandScaleZ.getValue() / 10.0f) + 1.0f);
        GlStateManager.rotate(mainhandRotationX.getValue() * 36.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(mainhandRotationY.getValue() * 36.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mainhandRotationZ.getValue() * 36.0f, 0.0f, 0.0f, 1.0f);
    }

    @SubscribeEvent
    public void onRenderOffhand(RenderItemEvent.Offhand event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;
        if(!event.getItemStack().getItem().equals(mc.player.getHeldItemOffhand().getItem()))
            return;
        GL11.glTranslated(offhandX.getValue() / 40.0f, offhandY.getValue() / 40.0f, offhandZ.getValue() / 40.0f);
        GlStateManager.scale((offhandScaleX.getValue() / 10.0f) + 1.0f, (offhandScaleY.getValue() / 10.0f) + 1.0f, (offhandScaleZ.getValue() / 10.0f) + 1.0f);
        GlStateManager.rotate(offhandRotationX.getValue() * 36.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(offhandRotationY.getValue() * 36.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(offhandRotationZ.getValue() * 36.0f, 0.0f, 0.0f, 1.0f);
    }
}
