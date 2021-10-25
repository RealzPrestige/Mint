package mint.newgui.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.core.NewGuiModule;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class ModeButton extends Button {
    public Setting setting;


    public ModeButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName() + " " + ChatFormatting.GRAY + (setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : setting.currentEnumName()), x, y, -1);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY)) {
            if(mouseButton == 0)
            setting.increaseEnum();
            else if(mouseButton == 1)
                setting.decreaseEnum();
        }
        Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
}

