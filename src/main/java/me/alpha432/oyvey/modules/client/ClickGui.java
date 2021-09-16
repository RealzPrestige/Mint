package me.alpha432.oyvey.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.events.ClientEvent;
import me.alpha432.oyvey.commands.Command;
import me.alpha432.oyvey.clickgui.OyVeyGui;
import me.alpha432.oyvey.modules.Module;
import me.alpha432.oyvey.clickgui.setting.Setting;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<String> prefix = register(new Setting<>("Prefix", "."));
    public Setting<Integer> red = register(new Setting<>("Red", 0, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 0, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 180, 0, 255));
    public Setting<Integer> topRed = register(new Setting<>("TopRed", 0, 0, 255));
    public Setting<Integer> topGreen = register(new Setting<>("TopGreen", 0, 0, 255));
    public Setting<Integer> topBlue = register(new Setting<>("TopBlue", 150, 0, 255));
    public Setting<Integer> topAlpha = register(new Setting<>("TopAlpha", 180, 0, 255));
    public Setting<Integer> sliderBgRed = register(new Setting<>("SliderBgRed", 0, 0, 255));
    public Setting<Integer> sliderBgGreen = register(new Setting<>("SliderBgGreen", 0, 0, 255));
    public Setting<Integer> sliderBgBlue = register(new Setting<>("SliderBgBlue", 150, 0, 255));
    public Setting<Integer> sliderBgAlpha = register(new Setting<>("SliderBgAlpha", 180, 0, 255));
    public Setting<Boolean> rainbow = register(new Setting<>("Rainbow", false));
    public Setting<Integer> rainbowHue = register(new Setting<>("Delay", 240, 0, 600, v -> this.rainbow.getValue()));
    public Setting<Float> rainbowBrightness = register(new Setting<>("Brightness ", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getValue()));
    public Setting<Float> rainbowSaturation = register(new Setting<>("Saturation", 150.0f, 1.0f, 255.0f, v -> this.rainbow.getValue()));

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        setBind(Keyboard.KEY_O);
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                OyVey.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + OyVey.commandManager.getPrefix());
            }
        }
    }

    @Override
    public void onEnable() {
        OyVey.INSTANCE.mc.displayGuiScreen(OyVeyGui.getClickGui());
    }

    @Override
    public void onLoad() {
        OyVey.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(OyVey.INSTANCE.mc.currentScreen instanceof OyVeyGui)) {
            this.disable();
        }
    }
}

