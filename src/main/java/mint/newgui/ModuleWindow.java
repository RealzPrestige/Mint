package mint.newgui;

import mint.Mint;
import mint.modules.Module;
import mint.modules.core.NewGuiModule;
import mint.newgui.buttons.Button;
import mint.newgui.buttons.KeybindButton;
import mint.newgui.buttons.ModeButton;
import mint.newgui.buttons.NumberButton;
import mint.newgui.buttons.a.*;
import mint.setting.Bind;
import mint.setting.Setting;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.*;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.ArrayList;

public class ModuleWindow {
    public String name;
    public int x;
    public int y;
    public int width;
    public int height;
    public Color disabledColor;
    public Color enabledColor;
    public Module module;
    ArrayList<Button> button = new ArrayList<>();
    ArrayList<NewButton> newButton = new ArrayList<>();

    public ModuleWindow(String name, int x, int y, int width, int height, Color disabledColor, Color enabledColor, Module module) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.disabledColor = disabledColor;
        this.enabledColor = enabledColor;
        this.module = module;
        getSettings();
    }

    public void getSettings() {
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<NewButton> penius = new ArrayList<>();
        for (Setting setting : module.getSettings()) {
            if (module.getSettings().isEmpty())
                continue;

            if (!setting.isVisible())
                continue;

            if (setting.getValue() instanceof Bind && !setting.getName().equalsIgnoreCase("Keybind"))
                buttons.add(new KeybindButton(setting));

            if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName"))
                buttons.add(new mint.newgui.buttons.StringButton(setting));

            if (setting.getValue() instanceof Boolean && setting.isParent())
                buttons.add(new mint.newgui.buttons.ParentButton(setting));

            if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled") && !setting.isParent())
                buttons.add(new mint.newgui.buttons.BooleanButton(setting));

            if (setting.isNumberSetting() && setting.hasRestriction())
                buttons.add(new NumberButton(setting));

            if (setting.isEnumSetting())
                buttons.add(new ModeButton(setting));

            if (setting.isColorSetting())
                buttons.add(new mint.newgui.buttons.ColorButton(setting));
        }
        buttons.add(new KeybindButton(module.getSettingByName("Keybind")));

        assert Mint.settingsRewrite != null;
        for (SettingRewrite settingsRewrite : Mint.settingsRewrite.doesModuleContainSetting(module)) {
            if (!settingsRewrite.isVisible())
                continue;

            if (settingsRewrite instanceof BooleanSetting)
                penius.add(new BooleanButton(settingsRewrite));

            if (settingsRewrite instanceof IntegerSetting)
                penius.add(new IntegerButton(settingsRewrite, (IntegerSetting) settingsRewrite));

            if (settingsRewrite instanceof FloatSetting)
                penius.add(new FloatButton(settingsRewrite, (FloatSetting) settingsRewrite));

            if (settingsRewrite instanceof DoubleSetting)
                penius.add(new DoubleButton(settingsRewrite, (DoubleSetting) settingsRewrite));

            if (settingsRewrite instanceof KeySetting)
                penius.add(new KeyButton(settingsRewrite, (KeySetting) settingsRewrite));

            if (settingsRewrite instanceof EnumSetting)
                penius.add(new EnumButton(settingsRewrite, (EnumSetting) settingsRewrite));

            if (settingsRewrite instanceof StringSetting)
                penius.add(new StringButton(settingsRewrite, (StringSetting) settingsRewrite));

            if (settingsRewrite instanceof ColorSetting)
                penius.add(new ColorButton(settingsRewrite, (ColorSetting) settingsRewrite));

            if (settingsRewrite instanceof ParentSetting)
                penius.add(new ParentButton(settingsRewrite, (ParentSetting) settingsRewrite));
        }

        button = buttons;
        newButton = penius;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if (module.isEnabled())
            RenderUtil.drawRect(x + 1, y, x + width - 1, y + height, enabledColor.getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(name, isInside(mouseX, mouseY) ? x + 2 : x + 1, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        if (module.isOpened) {
            int y = this.y;
            for (Button button : button) {
                button.setX(x + 2);
                button.setY(y += height);
                button.setWidth(width - 4);
                button.setHeight(height);
                button.drawScreen(mouseX, mouseY, partialTicks);
                if (button instanceof mint.newgui.buttons.ColorButton && button.getSetting().isOpen) {
                    y += 112;
                    if (button.getSetting().selected)
                        y += 10;
                }
            }
            for (NewButton button : newButton) {
                button.setX(x + 2);
                button.setY(y += height);
                button.setWidth(width - 4);
                button.setHeight(button instanceof EnumButton ? height + 4 : height);
                button.drawScreen(mouseX, mouseY, partialTicks);
                if (button instanceof EnumButton)
                    y += 4;
                if (button instanceof ColorButton && ((ColorButton) button).getColorSetting().isOpen()) {
                    y += 112;
                    if (((ColorButton) button).getColorSetting().isSelected())
                        y += 10;
                }
            }
            RenderUtil.drawOutlineRect(x + 2, this.y + height, x + width - 2, y + height - 1, NewGuiModule.getInstance().color.getColor(), 1f);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && isInside(mouseX, mouseY)) {
            module.isOpened = !module.isOpened;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        if (isInside(mouseX, mouseY) && mouseButton == 0)
            module.toggle();

        button.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        newButton.forEach(newButton -> newButton.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void initGui() {
        if (module.isOpened) {
            button.forEach(Button::initGui);
            newButton.forEach(NewButton::initGui);
        }

    }

    public void onKeyTyped(char typedChar, int keyCode) {
        button.forEach(button -> button.onKeyTyped(typedChar, keyCode));
        newButton.forEach(newButton -> newButton.onKeyTyped(typedChar, keyCode));
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    public int getHeight() {
        return height;
    }
}
