package mint.newgui;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

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


    public ModuleWindow(String name, int x, int y, int width, int height, Color disabledColor, Color enabledColor, Module module) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.disabledColor = disabledColor;
        this.enabledColor = enabledColor;
        this.module = module;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, module.isEnabled() ? enabledColor.getRGB() : disabledColor.getRGB());
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(name, x, y, -1);
        if (subOpen) {
            for (Setting setting : module.getSettings()) {
                //add all the buttons yes
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && this.isInside(mouseX, mouseY)) {
            this.subOpen = !this.subOpen;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        if (isInside(mouseX, mouseY) && mouseButton == 0)
            module.setEnabled(!module.isEnabled());
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }
}
