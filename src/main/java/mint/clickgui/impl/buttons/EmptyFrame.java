package mint.clickgui.impl.buttons;

import mint.clickgui.setting.Setting;
import mint.modules.core.Gui;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;

public class EmptyFrame
        extends ButtonFrame {
    private final Setting setting;

    public EmptyFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int sideColor = ColorUtil.toRGBA(Gui.getInstance().sideRed.getValue(), Gui.getInstance().sideGreen.getValue(), Gui.getInstance().sideBlue.getValue(), Gui.getInstance().sideAlpha.getValue()); RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x + 113, this.y - 2, this.x + 114, this.y + this.height, sideColor);
    }

    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        setting.setValue(!((Boolean) this.setting.getValue()));
    }

    @Override
    public boolean getState() {
        return (Boolean) this.setting.getValue();
    }
}

