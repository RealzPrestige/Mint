package me.alpha432.oyvey.modules.client;

import com.google.common.primitives.Booleans;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.events.Render2DEvent;
import me.alpha432.oyvey.clickgui.impl.buttons.ParentFrame;
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
    public Setting<String> prefix = register(new Setting<>("Prefix", ".", false));
    public Setting<Boolean> outline = register(new Setting<>("Outline", false, false));
    public Setting<Boolean> color = register(new Setting<>("Color", false, true));
    public Setting<Integer> red = register(new Setting<>("Red", 0, 0, 255, v-> color.getValue()));
    public Setting<Integer> green = register(new Setting<>("Green", 0, 0, 255, v-> color.getValue()));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v-> color.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 180, 0, 255, v-> color.getValue()));
    public Setting<Integer> topRed = register(new Setting<>("TopRed", 0, 0, 255));
    public Setting<Integer> topGreen = register(new Setting<>("TopGreen", 0, 0, 255));
    public Setting<Integer> topBlue = register(new Setting<>("TopBlue", 150, 0, 255));
    public Setting<Integer> topAlpha = register(new Setting<>("TopAlpha", 180, 0, 255));
    public Setting<Integer> sliderBgRed = register(new Setting<>("SliderBgRed", 120, 0, 255));
    public Setting<Integer> sliderBgGreen = register(new Setting<>("SliderBgGreen", 120, 0, 255));
    public Setting<Integer> sliderBgBlue = register(new Setting<>("SliderBgBlue", 120, 0, 255));
    public Setting<Integer> sliderBgAlpha = register(new Setting<>("SliderBgAlpha", 55, 0, 255));
    public Setting<Integer> sideRed = register(new Setting<>("SideRed", 255, 0, 255));
    public Setting<Integer> sideGreen = register(new Setting<>("SideGreen", 255, 0, 255));
    public Setting<Integer> sideBlue = register(new Setting<>("SideBlue", 255, 0, 255));
    public Setting<Integer> sideAlpha = register(new Setting<>("SideAlpha", 255, 0, 255));
    public Setting<Integer> stateFalseBooleanRed = register(new Setting<>("BoolDisabledRed", 255, 0, 255));
    public Setting<Integer> stateFalseBooleanGreen = register(new Setting<>("BoolDisabledGreen", 0, 0, 255));
    public Setting<Integer> stateFalseBooleanBlue = register(new Setting<>("BoolDisabledBlue", 0, 0, 255));
    public Setting<Integer> stateFalseBooleanAlpha = register(new Setting<>("BoolDisabledAlpha", 255, 0, 255));
    public Setting<Integer> stateTrueBooleanRed = register(new Setting<>("BoolEnabledRed", 0, 0, 255));
    public Setting<Integer> stateTrueBooleanGreen = register(new Setting<>("BoolEnabledGreen", 255, 0, 255));
    public Setting<Integer> stateTrueBooleanBlue = register(new Setting<>("BoolEnabledBlue", 0, 0, 255));
    public Setting<Integer> stateTrueBooleanAlpha = register(new Setting<>("BoolEnabledAlpha", 255, 0, 255));
    public Setting<Boolean> gradient = this.register(new Setting<>("Gradient", true));
    public Setting<Integer> gradientAlpha = this.register(new Setting<>("G-Alpha", 150, 0, 255, v -> gradient.getValue()));
    public Setting<Array> gradientType = this.register(new Setting<>("GradientType", Array.FromBottom, v -> gradient.getValue()));



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
        
    public enum Array {
        FromTop,
        FromBottom
    }
    @Override
    public void onDisable() {
        OyVey.configManager.saveConfig("Default");
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
        public void onRender2D(Render2DEvent event) {
                 if (gradient.getValue()) {
            if (gradientType.getValue() == Array.FromBottom) {
                if (mc.currentScreen instanceof OyVeyGui) {
                    OyVeyGui.getInstance().drawGradient(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), new Color(0, 0, 0, 0).getRGB(), ClickGui.getInstance().rainbow.getValue() ? new Color(rainbowCol.getRed(), rainbowCol.getGreen(), rainbowCol.getBlue(), gradientAlpha.getValue()).getRGB() : new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue(), gradientAlpha.getValue()).getRGB());
                }
            }
            if (gradientType.getValue() == Array.FromTop) {
                if (mc.currentScreen instanceof OyVeyGui) {
                    OyVeyGui.getInstance().drawGradient(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(),  ClickGui.getInstance().rainbow.getValue() ? new Color(rainbowCol.getRed(), rainbowCol.getGreen(), rainbowCol.getBlue(), gradientAlpha.getValue()).getRGB() : new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue(), gradientAlpha.getValue()).getRGB(), new Color(0,0,0,0).getRGB());
                }
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

