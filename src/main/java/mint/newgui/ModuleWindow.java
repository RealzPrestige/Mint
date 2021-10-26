package mint.newgui;

import mint.Mint;
import mint.modules.Module;
import mint.newgui.buttons.Button;
import mint.newgui.buttons.*;
import mint.setting.Bind;
import mint.setting.Setting;
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
        for (Setting setting : module.getSettings()) {
            if (module.getSettings().isEmpty())
                continue;

            if (!setting.isVisible())
                continue;

            if (setting.getValue() instanceof Bind && !setting.getName().equalsIgnoreCase("Keybind"))
                buttons.add(new KeybindButton(setting));

            if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName"))
                buttons.add(new StringButton(setting));

            if (setting.getValue() instanceof Boolean && setting.isParent())
                buttons.add(new ParentButton(setting));

            if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled") && !setting.isParent())
                buttons.add(new BooleanButton(setting));

            if (setting.isNumberSetting() && setting.hasRestriction())
                buttons.add(new NumberButton(setting));

            if (setting.isEnumSetting())
                buttons.add(new ModeButton(setting));

            if (setting.isColorSetting())
                buttons.add(new ColorButton(setting));
        }
        buttons.add(new KeybindButton(module.getSettingByName("Keybind")));
        button = buttons;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, module.isEnabled() ? enabledColor.getRGB() : disabledColor.getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(name, isInside(mouseX, mouseY) ? x + 1 : x,  y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        Mint.textManager.drawStringWithShadow(module.isOpened ? "-" : "+", x + width - Mint.textManager.getStringWidth(module.isOpened ? "-" : "+") - 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        if (module.isOpened) {
            int y = this.y;
            for (Button button : button) {
                button.setX(x);
                button.setY(y += height);
                button.setWidth(width);
                button.setHeight(height);
                button.drawScreen(mouseX, mouseY, partialTicks);
                if (button instanceof ColorButton && button.getSetting().isOpen) {
                    y += 112;
                    if (button.getSetting().selected)
                        y += 10;
                }
            }
            RenderUtil.drawOutlineRect(x, this.y + height, x + width, y + height - 1, new Color(0,0,0), 0.1f);
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
    }

    public void initGui() {
        if (module.isOpened)
            button.forEach(Button::initGui);
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        button.forEach(button -> button.onKeyTyped(typedChar, keyCode));
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    public int getHeight() {
        return height;
    }
}
