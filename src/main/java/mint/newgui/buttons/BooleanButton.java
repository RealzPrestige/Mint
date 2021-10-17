package mint.newgui.buttons;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.utils.RenderUtil;

public class BooleanButton extends Button {
    Setting setting;

    public BooleanButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, -1);
        Mint.textManager.drawStringWithShadow(setting.getName(), x, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
    }
}
