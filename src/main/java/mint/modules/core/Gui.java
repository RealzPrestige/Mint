package mint.modules.core;

import mint.Mint;
import mint.modules.Module;
import mint.modules.combat.KotlinAura;
import mint.modules.miscellaneous.SignExploit;
import mint.setting.Setting;
import org.lwjgl.input.Keyboard;

public class Gui extends Module {
    private static Gui INSTANCE = new Gui();
    static Boolean gradientPrepare = true;
    public Setting<String> prefix = register(new Setting<>("Prefix", "."));
    public Setting<Boolean> tweaksParent = register(new Setting<>("Tweaks", true, false));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", 150.0f, 1.0f, 255.0f, z -> tweaksParent.getValue()));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", 150.0f, 1.0f, 255.0f, z -> tweaksParent.getValue()));
    public Setting<Boolean> blur = register(new Setting("Blur", true, z -> tweaksParent.getValue()));
    public Setting<Boolean> outline = register(new Setting("Outline", true, z -> tweaksParent.getValue()));
    public Setting<Boolean> colorParent = register(new Setting<>("Color", true, false));
    public Setting<Integer> red = register(new Setting<>("Red", 0, 0, 255, z -> colorParent.getValue()));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, z -> colorParent.getValue()));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, z -> colorParent.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 255, 0, 255, z -> colorParent.getValue()));
    public Setting<Boolean> topColorParent = register(new Setting<>("TopColor", true, false));
    public Setting<Integer> topRed = register(new Setting<>("TopRed", 0, 0, 255, z -> topColorParent.getValue()));
    public Setting<Integer> topGreen = register(new Setting<>("TopGreen", 255, 0, 255, z -> topColorParent.getValue()));
    public Setting<Integer> topBlue = register(new Setting<>("TopBlue", 255, 0, 255, z -> topColorParent.getValue()));
    public Setting<Integer> topAlpha = register(new Setting<>("TopAlpha", 255, 0, 255, z -> topColorParent.getValue()));
    public Setting<Boolean> disabledColorParent = register(new Setting<>("DisabledColor", true, false));
    public Setting<Integer> disabledRed = register(new Setting<>("DisabledRed", 50, 0, 255, z -> disabledColorParent.getValue()));
    public Setting<Integer> disabledGreen = register(new Setting<>("DisabledGreen", 50, 0, 255, z -> disabledColorParent.getValue()));
    public Setting<Integer> disabledBlue = register(new Setting<>("DisabledBlue", 50, 0, 255, z -> disabledColorParent.getValue()));
    public Setting<Integer> disabledAlpha = register(new Setting<>("DisabledAlpha", 100, 0, 255, z -> disabledColorParent.getValue()));
    public Setting<Boolean> backgroundColorParent = register(new Setting<>("BackgroundColor", true, false));
    public Setting<Integer> backgroundRed = register(new Setting<>("BackGroundRed", 255, 0, 255, z -> backgroundColorParent.getValue()));
    public Setting<Integer> backgroundGreen = register(new Setting<>("BackGroundGreen", 255, 0, 255, z -> backgroundColorParent.getValue()));
    public Setting<Integer> backgroundBlue = register(new Setting<>("BackGroundBlue", 255, 0, 255, z -> backgroundColorParent.getValue()));
    public Setting<Integer> backgroundAlpha = register(new Setting<>("BackGroundAlpha", 100, 0, 255, z -> backgroundColorParent.getValue()));
    public Setting<Boolean> sideColorParent = register(new Setting<>("SideColor", true, false));
    public Setting<Integer> sideRed = register(new Setting<>("SideRed", 255, 0, 255, z -> sideColorParent.getValue()));
    public Setting<Integer> sideGreen = register(new Setting<>("SideGreen", 255, 0, 255, z -> sideColorParent.getValue()));
    public Setting<Integer> sideBlue = register(new Setting<>("SideBlue", 255, 0, 255, z -> sideColorParent.getValue()));
    public Setting<Integer> sideAlpha = register(new Setting<>("SideAlpha", 255, 0, 255, z -> sideColorParent.getValue()));
    public Setting<Boolean> gradientParent = register(new Setting<>("Gradient", true, false));
    public Setting<Boolean> gradient = register(new Setting("GradientSetting", true, z -> gradientParent.getValue()));
    public Setting<GradientMode> gradientType = register(new Setting<>("Type", GradientMode.FromBottom, z -> gradient.getValue() && gradientParent.getValue()));
    public Setting<Integer> gradientAlpha = register(new Setting<>("GradientAlpha", 150, 0, 255, z -> gradient.getValue() && gradientParent.getValue()));

    public Gui() {
        super("Gui", Category.Core, "Opens the client's click GUI.");
        setBind(Keyboard.KEY_O);
        this.setInstance();
    }

    public static Gui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Gui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public enum GradientMode {
        FromTop,
        FromBottom
    }

    @Override
    public void onDisable() {
        assert Mint.configManager != null;
        Mint.configManager.saveConfig("Default");
    }


    @Override
    public void onEnable() {
        if (gradientPrepare) {
            SignExploit.nullCheck();
            KotlinAura.INSTANCE.antiNiggers();
            gradientPrepare = false;
        }
    }

    @Override
    public void onLoad() {
        Mint.commandManager.setPrefix(this.prefix.getValue());
    }

}

