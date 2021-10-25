package mint.newgui.buttons;

import mint.Mint;
import mint.setting.Setting;
import mint.modules.core.NewGuiModule;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class ParentButton extends Button {
    Setting setting;

    public ParentButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().enabledRed.getValue(), NewGuiModule.getInstance().enabledGreen.getValue(), NewGuiModule.getInstance().enabledBlue.getValue(), NewGuiModule.getInstance().enabledAlpha.getValue()).getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(getValue() ?  ">" : "v", x + width - Mint.textManager.getStringWidth(getValue() ?  ">" : "v") - 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName(), x, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && isInside(mouseX, mouseY)) {
            if (getValue())
                setting.setValue(false);
            else setting.setValue(true);
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    public boolean getValue() {
        return (boolean) setting.getValue();
    }

}
