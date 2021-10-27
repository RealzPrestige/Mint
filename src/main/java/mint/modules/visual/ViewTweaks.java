package mint.modules.visual;

import mint.setting.Bind;
import mint.setting.Setting;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author kambing, zPrestige
 */

public class ViewTweaks extends Module {

    private static ViewTweaks INSTANCE = new ViewTweaks();

    public Setting<Boolean> fullBright = register(new Setting("Full Bright", false));

    private final Setting<Boolean> fovParent = register(new Setting<>("FOV", true, false));
    public Setting<Boolean> fov = register(new Setting("FOV Changer", true, z -> fovParent.getValue()));
    private final Setting<Boolean> fovStay = register(new Setting("FOV Stay", false, z -> fovParent.getValue()));
    public Setting<Float> fovValue = register(new Setting("Fov Value", 140.0f, 0.0f, 180.0f, z -> fovParent.getValue()));

    public Setting<Boolean> antiFog = register(new Setting("Anti Fog", false));
    public Setting<Boolean> noWeather = register(new Setting("No Weather", false));

    public Setting<Boolean> timeParent = register(new Setting("Time", true, false));
    public Setting<Boolean> timeChange = register(new Setting("Time Changer", true, timeParent.getDefaultValue()));
    public Setting<Integer> time = register(new Setting<>("Time", 0, 0, 23000, z -> timeParent.getValue()));

    public Setting<Boolean> skyParent = register(new Setting("Sky", true, false));
    public Setting<Boolean> skyColorChange = register(new Setting("Sky Color Changer", true, z -> skyParent.getValue()));
    public Setting<Boolean> rainbow = register(new Setting("Rainbow", false, z -> skyParent.getValue()));
    public Setting<Float> red = register(new Setting<>("Sky Red", 255.0f, 0.0f, 255.0f, z -> skyParent.getValue() && !rainbow.getValue()));
    public Setting<Float> green = register(new Setting<>("Sky Green", 255.0f, 0.0f, 255.0f, z -> skyParent.getValue() && !rainbow.getValue()));
    public Setting<Float> blue = register(new Setting<>("Sky Blue", 255.0f, 0.0f, 255.0f, z -> skyParent.getValue() && !rainbow.getValue()));

    public Setting<Bind> bind = register(new Setting<Object>("Third Person Hold Bind", new Bind(-1)));


    public ViewTweaks() {
        super("View Tweaks", Category.Visual, "Tweak how your game looks.");
        setInstance();
    }

    public static ViewTweaks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewTweaks();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (fullBright.getValue()) {
            mc.gameSettings.gammaSetting = 6969.0f;
        }
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (rainbow.getValue())
            doRainbow();


        if (bind.getValue().getKey() > -1) {
            if (Keyboard.isKeyDown(bind.getValue().getKey())) {
                mc.gameSettings.thirdPersonView = 1;
            } else {
                mc.gameSettings.thirdPersonView = 0;
            }
        }

        if (fov.getValue() && !fovStay.getValue()) {
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fovValue.getValue());
        }
        if (mc.gameSettings.gammaSetting != 6969.0f && fullBright.getValue()) {
            mc.gameSettings.gammaSetting = 6969.0f;
        }
        if (noWeather.getValue()) {
            mc.world.setRainStrength(0);
        }
    }

    @SubscribeEvent
    public void onWorld(EntityViewRenderEvent.RenderFogEvent event) {
        if (timeChange.getValue()) {
            mc.world.setTotalWorldTime((long) time.getValue());
            mc.world.setWorldTime((long) time.getValue());
        }
    }

    public void onLogin() {
        if (this.isEnabled()) {
            this.disable();
            this.enable();
        }
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (antiFog.getValue()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFogColor(final EntityViewRenderEvent.FogColors event) {
        if (skyColorChange.getValue()) {
            event.setRed(red.getValue() / 255.0f);
            event.setGreen(green.getValue() / 255.0f);
            event.setBlue(blue.getValue() / 255.0f);
        }
    }

    @SubscribeEvent
    public void onFovChange(EntityViewRenderEvent.FOVModifier event) {
        if (fovStay.getValue()) {
            event.setFOV(fovValue.getValue());
        }
    }

    public void doRainbow() {

        float[] tick_color = {
                (System.currentTimeMillis() % (360 * 32)) / (360f * 32)
        };

        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 0.8f);

        red.setValue((float) ((color_rgb_o >> 16) & 0xFF));
        green.setValue((float) ((color_rgb_o >> 8) & 0xFF));
        blue.setValue((float) (color_rgb_o & 0xFF));
    }

}
