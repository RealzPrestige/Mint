package mint.newgui;

import mint.Mint;
import mint.modules.Module;
import mint.utils.RenderUtil;

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

    public ModuleWindow(String name, int x, int y, int width, int height, Color disabledColor, Color enabledColor, Module module){
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
        Mint.textManager.drawStringWithShadow(name, x, y ,-1);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton){
        if(isInside(mouseX, mouseY) && mouseButton == 0)
            module.setEnabled(!module.isEnabled());
    }

    public boolean isInside(int mouseX, int mouseY){
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }
}
