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
        if (getState() || setting.isParent()) {
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 5.4f, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(Gui.getInstance().red.getValue(),Gui.getInstance().green.getValue(), Gui.getInstance().blue.getValue(), Gui.getInstance().alpha.getValue()));
        }else{
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 5.4f, this.y + (float) this.height - 0.5f, ColorUtil.toRGBA(0,0,0,0));
        }

        int sideColor = ColorUtil.toRGBA(Gui.getInstance().sideRed.getValue(), Gui.getInstance().sideGreen.getValue(), Gui.getInstance().sideBlue.getValue(), Gui.getInstance().sideAlpha.getValue()); RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x + 113, this.y - 2, this.x + 114, this.y + this.height, sideColor);
        if(setting.isParent()){
            Mint.textManager.drawStringWithShadow(this.getName(), this.x + (this.width / 2) - (renderer.getStringWidth(this.getName()) / 2), this.y - 1.7f - (float) MintGui.getClickGui().getTextOffset(), setting.isParent() ? -1 : this.getState() ? -1 : -5592406);
        } else {
            Mint.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) MintGui.getClickGui().getTextOffset(), setting.isParent() ? -1 : this.getState() ? -1 : -5592406);
        }
        if (setting.isParent()) {
            Mint.textManager.drawString((this.getState() ? "V" : ">"), this.x - 1.5f + (float) this.width - 7.4f, this.y - 2.0f - (float) MintGui.getClickGui().getTextOffset(), -1, false);

        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY) && !setting.isParent()) {
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }else if (this.isHovering(mouseX, mouseY) && mouseButton == 1 && setting.isParent()) {
            this.toggle();
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

