package mint.clickgui.impl.buttons;

import mint.Mint;
import mint.clickgui.MintGui;
import mint.modules.core.Gui;
import mint.clickgui.setting.Setting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BooleanFrame
        extends ButtonFrame {
    private final Setting setting;

    public BooleanFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 5.4f, this.y + (float) this.height - 0.5f, getState() ? ColorUtil.toRGBA(Gui.getInstance().red.getValue(),Gui.getInstance().green.getValue(), Gui.getInstance().blue.getValue(), Gui.getInstance().alpha.getValue()) : ColorUtil.toRGBA(0,0,0,0));
        int sideColor = ColorUtil.toRGBA(Gui.getInstance().sideRed.getValue(), Gui.getInstance().sideGreen.getValue(), Gui.getInstance().sideBlue.getValue(), Gui.getInstance().sideAlpha.getValue()); RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x + 113, this.y - 2, this.x + 114, this.y + this.height, sideColor);
        Mint.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) MintGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);

    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.setting.setValue(!((Boolean) this.setting.getValue()));
    }

    @Override
    public boolean getState() {
        return (Boolean) this.setting.getValue();
    }
}

