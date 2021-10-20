package mint.newgui.buttons;

import mint.Mint;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.modules.core.NewGuiModule;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@SuppressWarnings("unchecked")
public class BindButton extends Button {
    Setting setting;
    boolean isTyping;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(isTyping ? setting.getName() + " JOE " + Mint.textManager.getIdleSign() : (setting.getName() + " " + setting.getValue().toString().toUpperCase()), x, y, -1);
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
        super.onKeyTyped(typedChar, keyCode);
        if (isTyping) {
            if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
                setting.setValue(new Bind(-1));
                isTyping = false;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
                setting.setValue(new Bind(-1));
                isTyping = false;
            } else {
                setting.setValue(new Bind(keyCode));
                isTyping = false;
            }
        }
    }
}
