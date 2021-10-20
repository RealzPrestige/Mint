package mint.newgui.buttons;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.core.NewGuiModule;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class BindButton extends Button {
    Setting setting;
    boolean isTyping;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.isTyping = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow("IsTyping: " + isTyping + " " + setting.getName() + " " + setting.getValue().toString().toUpperCase(), x, y, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY) && mouseButton == 0) {
            isTyping = !isTyping;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        //  if (isTyping) {
        // Bind bindChar = new Bind(keyCode);

        //  if (bindChar.toString().equalsIgnoreCase("Delete") || bindChar.toString().equalsIgnoreCase("Escape"))
        //       bindChar = new Bind(-1);

        //   setting.setValue(bindChar);
        //  isTyping = !isTyping;
        // }
    }
}
