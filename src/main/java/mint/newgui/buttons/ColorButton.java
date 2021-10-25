package mint.newgui.buttons;

import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.setting.Setting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ColorButton extends Button {
    private Setting setting;
    private final float[] color;
    private boolean pickingColor;
    private boolean pickingHue;
    private boolean pickingAlpha;
    private int pickerX, pickerY, pickerWidth, pickerHeight;
    private int hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight;
    private int alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight;
    private boolean rainbowState = false;

    public ColorButton(Setting setting) {
        super(setting);
        float[] settingColor = Color.RGBtoHSB(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue(), null);
        this.color = new float[]{settingColor[0], settingColor[1], settingColor[2], setting.getColor().getAlpha() / 255.0f};
        pickingColor = false;
        this.setting = setting;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        RenderUtil.drawRect(x + width - 12, y + 1, x + width - 3, y + 9, setting.getColor().getRGB());
        if (setting.isOpen) {
            setHeight(height + 100);

            pickerX = x;
            pickerY = y + 10;
            pickerWidth = 120;
            pickerHeight = 100;

            hueSliderX = pickerX;
            hueSliderY = pickerY + pickerHeight + 6;
            hueSliderWidth = pickerWidth;
            hueSliderHeight = 10;

            alphaSliderX = pickerX + pickerWidth + 6;
            alphaSliderY = pickerY;
            alphaSliderWidth = 10;
            alphaSliderHeight = pickerHeight;

            if (pickingHue) {
                float restrictedX = (float) Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
                color[0] = (restrictedX - (float) hueSliderX) / hueSliderWidth;
            }
            if (pickingAlpha) {
                float restrictedY = (float) Math.min(Math.max(alphaSliderY, mouseY), alphaSliderY + alphaSliderHeight);
                color[3] = 1 - (restrictedY - (float) alphaSliderY) / alphaSliderHeight;
            }
            if (pickingColor) {
                float restrictedX = (float) Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
                float restrictedY = (float) Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
                color[1] = (restrictedX - (float) pickerX) / pickerWidth;
                color[2] = 1 - (restrictedY - (float) pickerY) / pickerHeight;
            }
            int selectedX = pickerX + pickerWidth + 6;
            int selectedY = pickerY + pickerHeight + 6;
            int selectedWidth = 10;
            int selectedHeight = 10;
            Gui.drawRect(pickerX - 2, pickerY - 2, pickerX + pickerWidth + 2, pickerY + pickerHeight + 2, 0xFC000000);
            Gui.drawRect(hueSliderX - 2, hueSliderY - 2, hueSliderX + hueSliderWidth + 2, hueSliderY + hueSliderHeight + 2, 0xFC000000);
            Gui.drawRect(alphaSliderX - 2, alphaSliderY - 2, alphaSliderX + alphaSliderWidth + 2, alphaSliderY + alphaSliderHeight + 2, 0xFC000000);
            int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);
            float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
            float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
            float selectedBlue = (selectedColor & 0xFF) / 255.0f;
            RenderUtil.drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
            RenderUtil.drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight, color[0]);
            RenderUtil.drawAlphaSlider(alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, color[3]);
            final int selectedColorFinal = alpha(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);
            Gui.drawRect(selectedX - 2, selectedY - 2, selectedX + selectedWidth + 2, selectedY + selectedHeight + 2, 0xFC000000);
            Gui.drawRect(selectedX, selectedY, selectedX + selectedWidth, selectedY + selectedHeight, selectedColorFinal);
            {
                final int cursorX = (int) (pickerX + color[1] * pickerWidth);
                final int cursorY = (int) ((pickerY + pickerHeight) - color[2] * pickerHeight);
                Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);
            }
            setting.setColor(urmom(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]));
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                setting.isOpen = false;
            }
        }
        if (isInsideButtonOnly(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + 10, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName(), x, y, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        pickingColor = check(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY);
        pickingHue = check(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY);
        pickingAlpha = check(alphaSliderX, alphaSliderY, alphaSliderX + alphaSliderWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY);
        if (isInsideButtonOnly(mouseX, mouseY) && mouseButton == 1)
            setting.isOpen = !setting.isOpen;

    }

    public boolean isInsideButtonOnly(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + 10);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        pickingColor = pickingHue = pickingAlpha = false;
    }


    public boolean check(int minX, int minY, int maxX, int maxY, int curX, int curY) {
        return curX >= minX && curY >= minY && curX < maxX && curY < maxY;
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_R) {
            rainbowState = !rainbowState;
        }
    }

    final int alpha(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha).getRGB();
    }

    final Color urmom(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha);
    }
}
