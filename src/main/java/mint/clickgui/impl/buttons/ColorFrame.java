package mint.clickgui.impl.buttons;

import mint.Mint;
import mint.setting.Setting;

/**
 * @author kambing
 * 2/10/2021
 */

public class ColorFrame extends ButtonFrame {
    public Setting setting;

    public ColorFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Mint.textManager.drawStringWithShadow("use new gui for colo picka", x, y, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.setHidden(!this.setting.isVisible());
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY) && !this.setting.isOpen) {
            this.setting.isOpen = true;
        } else if (mouseButton == 1 && this.isHovering(mouseX, mouseY) && this.setting.isOpen) {
            this.setting.isOpen = false;
        }
    }
}