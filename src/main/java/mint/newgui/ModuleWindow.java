package mint.newgui;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.newgui.buttons.BooleanButton;
import mint.newgui.buttons.Button;
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
    private boolean subOpen;
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
        subOpen = false;
        getSettings();
    }

    public void getSettings() {
        ArrayList<Button> items = new ArrayList<>();
        for (Setting setting : module.getSettings()) {
            if (module.getSettings().isEmpty())
                continue;

            if (setting.getValue() instanceof Boolean)
                items.add(new BooleanButton(setting));

        }
        button = items;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, module.isEnabled() ? enabledColor.getRGB() : disabledColor.getRGB());
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(name, x, y, -1);
        if (subOpen) {
            for (Button button : button) {
                button.setX(x);
                button.setY(y + height);
                button.setWidth(width);
                button.setHeight(y + height + height);
                button.drawScreen(mouseX, mouseY, partialTicks);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && isInside(mouseX, mouseY)) {
            subOpen = !subOpen;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        if (isInside(mouseX, mouseY) && mouseButton == 0)
            module.toggle();
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }
}
