package mint.clickgui.impl.buttons;

import mint.Mint;
import mint.clickgui.MintGui;
import mint.clickgui.setting.Setting;
import mint.modules.core.Gui;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

/**
 * @author kambing
 * 2/10/2021
 */

public class ColorFrame extends ButtonFrame { //TODO: finish this
    public static final Minecraft mc = Minecraft.getMinecraft();
    public Setting setting;

    public ColorFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int sideColor = ColorUtil.toRGBA(Gui.getInstance().sideRed.getValue(), Gui.getInstance().sideGreen.getValue(), Gui.getInstance().sideBlue.getValue(), Gui.getInstance().sideAlpha.getValue()); RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x + 113, this.y - 2, this.x + 114, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x + 110, this.y + 12, this.x + 103, this.y + 4, ColorUtil.toRGBA(setting.r, setting.g, setting.b, setting.a));
        RenderUtil.drawBorder(this.x + width - 4, this.y + (height / 2) - 3, 7, height / 2 + 1, new Color(0,0,0,150));
        Mint.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) MintGui.getClickGui().getTextOffset(), -1);
        if (setting.isOpen) {
            RenderUtil.drawColorPickerSquare(x + 2, y + 16, x + 94, y + 89, (int) (90 * 6f), 255);
            RenderUtil.drawBorder(x + 3, y + 16,  90, 72, new Color(0,0,0));
        }
    }


    @Override
    public void update() {
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.setHidden(!this.setting.isVisible());
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY) && !setting.isOpen) {
            setting.isOpen = true;
        } else if (mouseButton == 1 && this.isHovering(mouseX, mouseY) && setting.isOpen) {
            setting.isOpen = false;
        }
    }
}