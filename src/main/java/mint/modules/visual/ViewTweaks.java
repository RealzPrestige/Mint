package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author kambing, zPrestige
 */
public class ViewTweaks extends Module {

    private static ViewTweaks INSTANCE = new ViewTweaks();
    public Setting<Boolean> fullBright = register(new Setting("FullBright", false));
    public Setting<Boolean> glow = register(new Setting("ItemGlow", false));
    private final Setting<Boolean> fov = register(new Setting<>("FOV", true, false));
    private final Setting<Boolean> fovStay = register(new Setting("FOVStay", false, v -> fov.getValue()));
    public Setting<Float> fovValue = register(new Setting("FovValue", 140.0f, 0.0f, 180.0f, v -> fov.getValue()));
    public Setting<Boolean> antiFog = register(new Setting("AntiFog", false));
    public Setting<Boolean> noWeather = register(new Setting("NoWeather", false));
    public Setting<Boolean> timeChanger = register(new Setting("TimeChanger", true, false));
    public Setting<Integer> time = register(new Setting<>("Time", 0, 0, 23000, v -> timeChanger.getValue()));
    public Setting<Boolean> skyColor = register(new Setting("SkyColor", true, false));
    public Setting<Float> red = register(new Setting<>("SkyRed", 255.0f, 0.0f, 255.0f, v -> skyColor.getValue()));
    public Setting<Float> green = register(new Setting<>("SkyGreen", 255.0f, 0.0f, 255.0f, v -> skyColor.getValue()));
    public Setting<Float> blue = register(new Setting<>("SkyBlue", 255.0f, 0.0f, 255.0f, v -> skyColor.getValue()));

    public ViewTweaks() {
        super("ViewTweaks", Category.VISUAL, "Tweak ur mom");
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
        if (fov.getValue() && !fovStay.getValue()) {
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fovValue.getValue());
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
        if (timeChanger.getValue()) {
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
        if (skyColor.getValue()) {
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

}
