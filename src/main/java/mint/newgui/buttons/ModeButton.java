package mint.newgui.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.setting.Setting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class ModeButton extends Button {
    public Setting setting;


    public ModeButton(Setting setting) {
        super(setting);
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName() + " " + ChatFormatting.GRAY + (setting.currentEnumName().equalsIgnoreCase("ABC") ? "ABC" : setting.currentEnumName()), x + 2, y, -1);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY)) {
            if(mouseButton == 0)
            setting.increaseEnum();
            else if(mouseButton == 1)
                setting.resetEnum();
        }
        Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
}

