package mint.newgui;

import mint.Mint;
import mint.utils.RenderUtil;

import java.awt.*;

public class ModuleWindow {
    public String name;
    public int x;
    public int y;
    public int width;
    public int height;
    public Color color;

    public ModuleWindow(String name, int x, int y, int width, int height, Color color){
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, color.getRGB());
        Mint.textManager.drawStringWithShadow(name, x, y ,-1);
    }
}
