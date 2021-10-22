package mint.newgui.buttons;

import mint.clickgui.setting.Setting;
import mint.utils.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class ColorPicker extends GuiScreen {

    private Setting setting;
    private final float[] color;
    private boolean pickingColor;
    private boolean pickingHue;
    private boolean pickingAlpha;
    private int pickerX, pickerY, pickerWidth, pickerHeight;
    private int hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight;
    private int alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight;
    private float rainbowSpeed = 20.0f;
    private boolean rainbowState = false;

    public ColorPicker(Setting setting) {
        color = new float[]{0.4f, 1.0f, 1.0f, 1.0f};
        pickingColor = false;
        this.setting = setting;
    }

    @Override
    public void initGui() {
        pickerWidth = 120;
        pickerHeight = 100;
        pickerX = width / 2 - pickerWidth / 2;
        pickerY = height / 2 - pickerHeight / 2;
        hueSliderX = pickerX;
        hueSliderY = pickerY + pickerHeight + 6;
        hueSliderWidth = pickerWidth;
        hueSliderHeight = 10;
        alphaSliderX = pickerX + pickerWidth + 6;
        alphaSliderY = pickerY;
        alphaSliderWidth = 10;
        alphaSliderHeight = pickerHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (rainbowState) {
            double rainbowState = Math.ceil((System.currentTimeMillis() + 200) / 20.0);
            rainbowState %= 360.0;
            color[0] = (float) (rainbowState / 360.0);
        }
        drawDefaultBackground();
        if (pickingHue) {
            if (hueSliderWidth > hueSliderHeight) {
                float restrictedX = (float) Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
                color[0] = (restrictedX - (float) hueSliderX) / hueSliderWidth;
            } else {
                float restrictedY = (float) Math.min(Math.max(hueSliderY, mouseY), hueSliderY + hueSliderHeight);
                color[0] = (restrictedY - (float) hueSliderY) / hueSliderHeight;
            }
        }
        if (pickingAlpha) {
            if (alphaSliderWidth > alphaSliderHeight) {
                float restrictedX = (float) Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + alphaSliderWidth);
                color[3] = 1 - (restrictedX - (float) alphaSliderX) / alphaSliderWidth;
            } else {
                float restrictedY = (float) Math.min(Math.max(alphaSliderY, mouseY), alphaSliderY + alphaSliderHeight);
                color[3] = 1 - (restrictedY - (float) alphaSliderY) / alphaSliderHeight;
            }
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
//        for (int i = 1; i < pickerHeight/10; i++) {
//            Gui.drawRect(selectedX - 2, pickerY + i * 14, selectedX + 12, pickerY + i * 14, 0xFC000000);
//        }
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            setting.isOpen = false;
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

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        pickingColor = check(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY);
        pickingHue = check(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY);
        pickingAlpha = check(alphaSliderX, alphaSliderY, alphaSliderX + alphaSliderWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY);
        if (!(pickingColor == pickingHue == pickingAlpha)) {
            setting.isOpen = false;
            mc.displayGuiScreen(null);
            mint.modules.core.Gui.getInstance().enable();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        pickingColor = pickingHue = pickingAlpha = false;
    }

    protected boolean check(int minX, int minY, int maxX, int maxY, int curX, int curY) {
        return curX >= minX && curY >= minY && curX < maxX && curY < maxY;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_R) {
            rainbowState = !rainbowState;
        }
        if (keyCode == Keyboard.KEY_LEFT) {
            rainbowSpeed -= 0.1;
        } else if (keyCode == Keyboard.KEY_RIGHT) rainbowSpeed += 0.1;
    }
}