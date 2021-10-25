package mint.newgui.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.core.NewGuiModule;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class StringButton extends Button {
    Setting setting;

    CurrentString currentString = new CurrentString("");

    public StringButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.isOpen ? currentString.getString() + Mint.textManager.getIdleSign() : setting.getName() + " " + ChatFormatting.GRAY + setting.getValue().toString().toUpperCase(), x, y, -1);

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
        if (setting.isOpen) {

            if (keyCode == 1)
                return;

            if (keyCode == 28)
                enterString();
            else if (keyCode == 14)
                setString(removeLastChar(currentString.getString()));

            setString(currentString.getString() + typedChar);

        }
    }

    private void enterString() {
        if (currentString.getString().isEmpty())
            setting.setValue(setting.getValue());
        else setting.setValue(currentString.getString());

        setString("");
    }

    public void setString(String newString) {
        currentString = new CurrentString(newString);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0)
            output = str.substring(0, str.length() - 1);
        return output;
    }

    public static class CurrentString {
        private String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }
}


