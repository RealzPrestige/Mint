package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Chams extends Module {
    private static Chams INSTANCE = new Chams();

    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.SOLID));
    public enum Mode {SOLID, WIREFRAME, BOTH}
    public Setting<Boolean> removeArmor = register(new Setting<>("RemoveArmor", false));
    public Setting<Boolean> animationDisabler = register(new Setting<>("AntimationDisabler", true, false));
    public Setting<Boolean> limbSwing = register(new Setting("LimbsSwing", false, v-> animationDisabler.getValue()));
    public Setting<Boolean> rotationPitch = register(new Setting("RotationPitch", false, v-> animationDisabler.getValue()));
    public Setting<Boolean> rotationYaw = register(new Setting("RotationYaw", false, v-> animationDisabler.getValue()));
    public Setting<Boolean> rotationYawHead = register(new Setting("RotationYawHead", false, v-> animationDisabler.getValue()));
    public Setting<Boolean> swingProgress= register(new Setting("SwingProgress", false, v-> animationDisabler.getValue()));
    public Setting<Boolean> cameraPitch = register(new Setting("CameraPitch", false, v-> animationDisabler.getValue()));
    public Setting<Boolean> rainbow = register(new Setting("Rainbow", false, v-> (mode.getValue() == Mode.SOLID || mode.getValue() == Mode.BOTH)));
    public Setting<Integer> rainbowHue = register(new Setting<>("RainbowHue", 100, 0, 600, v -> rainbow.getValue() && (mode.getValue() == Mode.SOLID || mode.getValue() == Mode.BOTH)));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255, v-> (mode.getValue() == Mode.SOLID || mode.getValue() == Mode.BOTH)));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, v-> (mode.getValue() == Mode.SOLID || mode.getValue() == Mode.BOTH)));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v-> (mode.getValue() == Mode.SOLID || mode.getValue() == Mode.BOTH)));
    public final Setting<Float> alpha = register(new Setting<>("Alpha", 50.0f, 0.1f, 255.0f, v-> (mode.getValue() == Mode.SOLID || mode.getValue() == Mode.BOTH)));
    public Setting<Boolean> o_rainbow = register(new Setting("OutlineRainbow", false, v-> (mode.getValue() == Mode.WIREFRAME || mode.getValue() == Mode.BOTH)));
    public Setting<Integer> o_rainbowHue = register(new Setting<>("OutlineRainbowHue", 100, 0, 600, v -> rainbow.getValue() && (mode.getValue() == Mode.WIREFRAME || mode.getValue() == Mode.BOTH)));
    public Setting<Integer> o_red = register(new Setting<>("OutlineRed", 255, 0, 255, v-> (mode.getValue() == Mode.WIREFRAME || mode.getValue() == Mode.BOTH)));
    public Setting<Integer> o_green = register(new Setting<>("OutlineGreen", 255, 0, 255, v-> (mode.getValue() == Mode.WIREFRAME || mode.getValue() == Mode.BOTH)));
    public Setting<Integer> o_blue = register(new Setting<>("OutlineBlue", 255, 0, 255, v-> (mode.getValue() == Mode.WIREFRAME || mode.getValue() == Mode.BOTH)));
    public final Setting<Float> o_alpha = register(new Setting<>("OutlineAlpha", 255.0f, 0.1f, 255.0f, v-> (mode.getValue() == Mode.WIREFRAME || mode.getValue() == Mode.BOTH)));
    public final Setting<Float> lineWidth = register(new Setting<>("LineWidth", 1.0f, 0.1f, 3.0f, v-> (mode.getValue() == Mode.WIREFRAME || mode.getValue() == Mode.BOTH)));
    public Setting<Boolean> enchantColor = register(new Setting<>("EnchantColor", true, false));
    public Setting<Integer> enchantColorRed = register(new Setting<>("EnchantColorRed", 255, 0, 255, v-> enchantColor.getValue()));
    public Setting<Integer> enchantColorGreen = register(new Setting<>("EnchantColorGreen", 255, 0, 255, v-> enchantColor.getValue()));
    public Setting<Integer> enchantColorBlue = register(new Setting<>("EnchantColorBlue", 255, 0, 255, v-> enchantColor.getValue()));

    public Chams() {
        super("Chams", Category.VISUAL,"Tweaks the way players look.");
        setInstance();
    }

    public static Chams getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Chams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }

    public int enchantColor() {
        if (enchantColor.getValue()) {
            return new Color(enchantColorRed.getValue(), enchantColorGreen.getValue(), enchantColorBlue.getValue()).getRGB();
        }
        return -1;
    }
    public String hudInfoString(){
        return mode.currentEnumName();
    }
}