package mint.newgui.buttons;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.core.NewGuiModule;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class BooleanButton extends Button {
    Setting setting;

    public BooleanButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, (boolean) setting.getValue() ? new Color(NewGuiModule.getInstance().enabledRed.getValue(), NewGuiModule.getInstance().enabledGreen.getValue(), NewGuiModule.getInstance().enabledBlue.getValue(), NewGuiModule.getInstance().enabledAlpha.getValue()).getRGB() : new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName(), x, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isInside(mouseX, mouseY)) {
            if (getValue())
                setting.setValue(false);
            else setting.setValue(true);
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }

    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    public boolean getValue() {
        return (boolean) setting.getValue();
    }

}
