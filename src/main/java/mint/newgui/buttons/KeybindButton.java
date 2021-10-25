package mint.newgui.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.modules.core.NewGuiModule;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class KeybindButton extends Button {
    Setting setting;

    public KeybindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.isOpen ? setting.getName() + " " + Mint.textManager.getIdleSign() :  setting.getName() + " " + ChatFormatting.GRAY + setting.getValue().toString().toUpperCase(), x, y, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isInside(mouseX, mouseY)) {
            setting.isOpen = !setting.isOpen;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (!setting.isOpen)
            return;

        Bind bindChar = new Bind(keyCode);

        if (bindChar.toString().equalsIgnoreCase("Delete") || bindChar.toString().equalsIgnoreCase("Escape") || bindChar.toString().equalsIgnoreCase("Back"))
            bindChar = new Bind(-1);

        setting.setValue(bindChar);
        Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        setting.isOpen = !setting.isOpen;

    }
}
