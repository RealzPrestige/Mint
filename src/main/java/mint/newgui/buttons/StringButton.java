package mint.newgui.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.setting.Setting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class StringButton extends Button {
    Setting setting;

    public StringButton(Setting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.isOpen ? setting.getName() + " " + ChatFormatting.GRAY + setting.getValueAsString() + Mint.textManager.getIdleSign() : setting.getName() + " " + ChatFormatting.GRAY + setting.getValueAsString(), x + 2, y, -1);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY) && mouseButton == 0) {
            setting.isOpen = !setting.isOpen;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (!setting.isOpen)
            return;

        if (keyCode == 14) {
            if (setting.getValueAsString() != null && setting.getValueAsString().length() > 0)
                setting.setValue(setting.getValueAsString().substring(0, setting.getValueAsString().length() - 1));
        } else if (keyCode == 28)
            setting.isOpen = false;
        else if(keyCode == 27)
            setting.isOpen = false;
        else setting.setValue(setting.getValue() + "" + typedChar);
    }
}


