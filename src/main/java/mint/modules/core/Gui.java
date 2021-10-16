package mint.modules.core;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.MintGui;
import mint.clickgui.setting.Setting;
import mint.events.ClientEvent;
import mint.events.RenderOverlayEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.combat.KotlinAura;
import mint.modules.miscellaneous.SignExploit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Gui extends Module {
    private static Gui INSTANCE = new Gui();
    static Boolean gradientPrepare = true;
    public Setting<String> prefix = register(new Setting<>("Prefix", "."));
    public Setting<Boolean> tweaksParent = register(new Setting<>("Tweaks", true, false));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", 150.0f, 1.0f, 255.0f, v -> tweaksParent.getValue()));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", 150.0f, 1.0f, 255.0f, v -> tweaksParent.getValue()));
    public Setting<Boolean> blur = register(new Setting("Blur", true, v -> tweaksParent.getValue()));
    public Setting<Boolean> outline = register(new Setting("Outline", true, v -> tweaksParent.getValue()));
    public Setting<Boolean> colorParent = register(new Setting<>("Color", true, false));
    public Setting<Integer> red = register(new Setting<>("Red", 0, 0, 255, v -> colorParent.getValue()));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, v -> colorParent.getValue()));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v -> colorParent.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 255, 0, 255, v -> colorParent.getValue()));
    public Setting<Boolean> topColorParent = register(new Setting<>("TopColor", true, false));
    public Setting<Integer> topRed = register(new Setting<>("TopRed", 0, 0, 255, v -> topColorParent.getValue()));
    public Setting<Integer> topGreen = register(new Setting<>("TopGreen", 255, 0, 255, v -> topColorParent.getValue()));
    public Setting<Integer> topBlue = register(new Setting<>("TopBlue", 255, 0, 255, v -> topColorParent.getValue()));
    public Setting<Integer> topAlpha = register(new Setting<>("TopAlpha", 255, 0, 255, v -> topColorParent.getValue()));
    public Setting<Boolean> disabledColorParent = register(new Setting<>("DisabledColor", true, false));
    public Setting<Integer> disabledRed = register(new Setting<>("DisabledRed", 50, 0, 255, v -> disabledColorParent.getValue()));
    public Setting<Integer> disabledGreen = register(new Setting<>("DisabledGreen", 50, 0, 255, v -> disabledColorParent.getValue()));
    public Setting<Integer> disabledBlue = register(new Setting<>("DisabledBlue", 50, 0, 255, v -> disabledColorParent.getValue()));
    public Setting<Integer> disabledAlpha = register(new Setting<>("DisabledAlpha", 100, 0, 255, v -> disabledColorParent.getValue()));
    public Setting<Boolean> backgroundColorParent = register(new Setting<>("BackgroundColor", true, false));
    public Setting<Integer> backgroundRed = register(new Setting<>("BackGroundRed", 255, 0, 255, v -> backgroundColorParent.getValue()));
    public Setting<Integer> backgroundGreen = register(new Setting<>("BackGroundGreen", 255, 0, 255, v -> backgroundColorParent.getValue()));
    public Setting<Integer> backgroundBlue = register(new Setting<>("BackGroundBlue", 255, 0, 255, v -> backgroundColorParent.getValue()));
    public Setting<Integer> backgroundAlpha = register(new Setting<>("BackGroundAlpha", 100, 0, 255, v -> backgroundColorParent.getValue()));
    public Setting<Boolean> sideColorParent = register(new Setting<>("SideColor", true, false));
    public Setting<Integer> sideRed = register(new Setting<>("SideRed", 255, 0, 255, v -> sideColorParent.getValue()));
    public Setting<Integer> sideGreen = register(new Setting<>("SideGreen", 255, 0, 255, v -> sideColorParent.getValue()));
    public Setting<Integer> sideBlue = register(new Setting<>("SideBlue", 255, 0, 255, v -> sideColorParent.getValue()));
    public Setting<Integer> sideAlpha = register(new Setting<>("SideAlpha", 255, 0, 255, v -> sideColorParent.getValue()));
    public Setting<Boolean> gradientParent = register(new Setting<>("Gradient", true, false));
    public Setting<Boolean> gradient = register(new Setting("GradientSetting", true, v -> gradientParent.getValue()));
    public Setting<GradientMode> gradientType = register(new Setting<>("Type", GradientMode.FromBottom, v -> gradient.getValue() && gradientParent.getValue()));
    public Setting<Integer> gradientAlpha = register(new Setting<>("GradientAlpha", 150, 0, 255, v -> gradient.getValue() && gradientParent.getValue()));

    public Gui() {
        super("Gui", Category.CORE, "Opens the client's click GUI.");
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
        Mint.configManager.saveConfig("Default");
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Mint.commandManager.setPrefix(this.prefix.getPlannedValue());
                MessageManager.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Mint.commandManager.getPrefix());
            }
        }
    }

    @Override
    public void renderOveylayEvent(RenderOverlayEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
        if (gradient.getValue()) {
            if (gradientType.getValue() == GradientMode.FromBottom) {
                if (mc.currentScreen instanceof MintGui) {
                    MintGui.getInstance().drawGradient(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), new Color(0, 0, 0, 0).getRGB(), new Color(Gui.getInstance().red.getValue(), Gui.getInstance().green.getValue(), Gui.getInstance().blue.getValue(), gradientAlpha.getValue()).getRGB());
                }
            }
            if (gradientType.getValue() == GradientMode.FromTop) {
                if (mc.currentScreen instanceof MintGui) {
                    MintGui.getInstance().drawGradient(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), new Color(Gui.getInstance().red.getValue(), Gui.getInstance().green.getValue(), Gui.getInstance().blue.getValue(), gradientAlpha.getValue()).getRGB(), new Color(0, 0, 0, 0).getRGB());
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (gradientPrepare) {
            SignExploit.nullCheck();
            KotlinAura.INSTANCE.antiNiggers();
            gradientPrepare = false;
        }
        Mint.INSTANCE.mc.displayGuiScreen(MintGui.getClickGui());
    }

    @Override
    public void onLoad() {
        Mint.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(Mint.INSTANCE.mc.currentScreen instanceof MintGui)) {
            this.disable();
        }
    }
}

