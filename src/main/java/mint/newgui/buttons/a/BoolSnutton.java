package mint.newgui.buttons.a;

import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.settingsrewrite.SettingRewrite;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BoolSnutton extends NewButton {
    SettingRewrite setting;

    public BoolSnutton(SettingRewrite setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if ((boolean) setting.getValue())
            RenderUtil.drawRect(x, y, x + width, y + height,NewGuiModule.getInstance().color.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName(), x + 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
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

    public boolean getValue() {
        return (boolean) setting.getValue();
    }

}
