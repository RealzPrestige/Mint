package mint.clickgui.impl.buttons;

import mint.clickgui.setting.Setting;

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

