package mint.newgui.buttons;

import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.setting.Setting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;

import java.awt.*;

public class ColorButton extends Button {
    Setting setting;

    public ColorButton(Setting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (setting.isOpen)
            setHeight(height + 100);
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        RenderUtil.drawRect(x + width - 12, y + 1, x + width - 3, y + 9, setting.getColor().getRGB());
        if (isInsideButtonOnly(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + 10, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName(), x, y, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInsideButtonOnly(mouseX, mouseY) && mouseButton == 1) {
            setting.isOpen = !setting.isOpen;
        }
    }

    public boolean isInsideButtonOnly(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + 10);
    }
}