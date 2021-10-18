package mint.newgui.buttons;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.core.NewGuiModule;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class NumberButton extends Button{

    Setting setting;
    int minimax;
    Number min;
    Number max;

    public NumberButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        min = (Number) setting.getMin();
        max = (Number) setting.getMax();
        minimax = max.intValue() - min.intValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragSlider(mouseX, mouseY);
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        RenderUtil.drawRect(x, y, ((Number) setting.getValue()).floatValue() <= min.floatValue() ? x : x + ((float) width + 2f) * ((((Number) setting.getValue()).floatValue() - min.floatValue()) / (max.floatValue() - min.floatValue())) - 2, y + (float) height, new Color(NewGuiModule.getInstance().enabledRed.getValue(), NewGuiModule.getInstance().enabledGreen.getValue(), NewGuiModule.getInstance().enabledBlue.getValue(), NewGuiModule.getInstance().enabledAlpha.getValue()).getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName() + " " + (setting.getValue() instanceof Float ? setting.getValue() : Double.valueOf(((Number) setting.getValue()).doubleValue())), x, y, - 1);
    }

    void dragSlider(int mouseX, int mouseY) {
        if (isInsideExtended(mouseX, mouseY) && Mouse.isButtonDown(0))
            setSliderValue(mouseX);

    }

    public boolean isInsideExtended(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width + 10) && (mouseY > y && mouseY < y + height);
    }

    void setSliderValue(int mouseX) {
        float percent = ((float) mouseX - x - 1) / ((float) width + 7.4f);
        if (setting.getValue() instanceof Double) {
            double result = (Double) setting.getMin() + (double) ((float) minimax * percent);
            setting.setValue((double) Math.round(10.0 * result) / 10.0);
        } else if (setting.getValue() instanceof Float) {
            float result = (Float) setting.getMin() + (float) minimax * percent;
            setting.setValue((float) Math.round(10.0f * result) / 10.0f);
        } else if (setting.getValue() instanceof Integer)
            setting.setValue((Integer) setting.getMin() + (int) ((float) minimax * percent));

    }
}
