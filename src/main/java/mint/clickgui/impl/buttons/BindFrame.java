package mint.clickgui.impl.buttons;

import mint.Mint;
import mint.clickgui.MintGui;
import mint.modules.core.Gui;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindFrame
        extends ButtonFrame {
    private final Setting setting;
    public boolean isListening;

    public BindFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int sideColor = ColorUtil.toRGBA(Gui.getInstance().sideRed.getValue(), Gui.getInstance().sideGreen.getValue(), Gui.getInstance().sideBlue.getValue(), Gui.getInstance().sideAlpha.getValue()); RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x, this.y + 14, this.x + width + 7, this.y + 15, sideColor);
        RenderUtil.drawRect(this.x + 113, this.y - 2, this.x + 114, this.y + this.height, sideColor);
        if (this.isListening) {
            Mint.textManager.drawStringWithShadow("Listening...", this.x + 2.3f, this.y - 1.7f - (float) MintGui.getClickGui().getTextOffset(), -1);
        } else {
            Mint.textManager.drawStringWithShadow(this.setting.getName() + " " + this.setting.getValue().toString().toUpperCase(), this.x + 2.3f, this.y - 1.7f - (float) MintGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
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
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            this.onMouseClick();
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }
}

