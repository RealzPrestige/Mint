package mint.newgui.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.setting.Bind;
import mint.setting.Setting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class KeybindButton extends Button {
    Setting setting;

    public KeybindButton(Setting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));

        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.isOpen ? setting.getName() + " " + Mint.textManager.getIdleSign() : setting.getName() + " " + ChatFormatting.GRAY + setting.getValue().toString().toUpperCase(), x, y, -1);
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
