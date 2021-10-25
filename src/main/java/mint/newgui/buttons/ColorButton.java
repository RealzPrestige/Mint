package mint.newgui.buttons;

import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.setting.Setting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ColorButton extends Button {
    Setting setting;
    private final float[] color;
    private Color finalColor;
    boolean pickingColor = false;
    boolean pickingHue = false;
    boolean pickingAlpha = false;

    public ColorButton(Setting setting) {
        super(setting);
        float[] settingColor = Color.RGBtoHSB(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue(), null);
        this.color = new float[]{settingColor[0], settingColor[1], settingColor[2], setting.getColor().getAlpha() / 255.0f};
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        RenderUtil.drawRect(x, y, x + width, y + height, new Color(NewGuiModule.getInstance().moduleRed.getValue(), NewGuiModule.getInstance().moduleGreen.getValue(), NewGuiModule.getInstance().moduleBlue.getValue(), NewGuiModule.getInstance().moduleAlpha.getValue()).getRGB());
        RenderUtil.drawRect(x + width - 12, y + 1, x + width - 3, y + 9, setting.getColor().getRGB());
        if (isInsideButtonOnly(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + 10, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(setting.getName(), x, y, -1);
        if (setting.isOpen) {
            setHeight(height + 100);
            //change all the shit here ez - for zprestige daddy
            drawPicker(setting, x, y + 10, x + 10, y + 10, x, y + 15, mouseX, mouseY);
            setting.setColor(finalColor);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInsideButtonOnly(mouseX, mouseY) && mouseButton == 1) {
            setting.isOpen = !setting.isOpen;
        }
    }

    public boolean isInsideButtonOnly(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + 10);
    }

    public void drawPicker(Setting subColor, int pickerX, int pickerY, int hueSliderX, int hueSliderY, int alphaSliderX, int alphaSliderY, int mouseX, int mouseY) {
        float[] color = new float[]{
                0, 0, 0, 0
        };

        try {
            color = new float[]{
                    Color.RGBtoHSB(subColor.getColor().getRed(), subColor.getColor().getGreen(), subColor.getColor().getBlue(), null)[0], Color.RGBtoHSB(subColor.getColor().getRed(), subColor.getColor().getGreen(), subColor.getColor().getBlue(), null)[1], Color.RGBtoHSB(subColor.getColor().getRed(), subColor.getColor().getGreen(), subColor.getColor().getBlue(), null)[2], subColor.getColor().getAlpha() / 255f
            };
        } catch (Exception ignored) {

        }

        int pickerWidth = 90;
        int pickerHeight = 51;
        int hueSliderWidth = 10;
        int hueSliderHeight = 59;
        int alphaSliderHeight = 10;
        int alphaSliderWidth = 90;
        if (!pickingColor && !pickingHue && !pickingAlpha) {
            if (Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY)) {
                pickingColor = true;
            } else if (Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY)) {
                pickingHue = true;
            } else if (Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + alphaSliderWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY))
                pickingAlpha = true;
        }

        if (pickingHue) {
            float restrictedY = (float) Math.min(Math.max(hueSliderY, mouseY), hueSliderY + hueSliderHeight);
            color[0] = (restrictedY - (float) hueSliderY) / hueSliderHeight;
            color[0] = (float) Math.min(0.97, color[0]);
        }

        if (pickingAlpha) {
            float restrictedX = (float) Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + pickerWidth);
            color[3] = 1 - (restrictedX - (float) alphaSliderX) / pickerWidth;
        }

        if (pickingColor) {
            float restrictedX = (float) Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = (float) Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            color[1] = (restrictedX - (float) pickerX) / pickerWidth;
            color[2] = 1 - (restrictedY - (float) pickerY) / pickerHeight;
            color[2] = (float) Math.max(0.04000002, color[2]);
            color[1] = (float) Math.max(0.022222223, color[1]);
        }

        int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);

        float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (selectedColor & 0xFF) / 255.0f;

        drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, 255);

        drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight, color[0]);

        int cursorX = (int) (pickerX + color[1] * pickerWidth);
        int cursorY = (int) ((pickerY + pickerHeight) - color[2] * pickerHeight);

        Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);

        finalColor = ColorPicker.urmom(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);

        drawAlphaSlider(alphaSliderX, alphaSliderY, pickerWidth, alphaSliderHeight, finalColor.getRed() / 255f, finalColor.getGreen() / 255f, finalColor.getBlue() / 255f, color[3]);
    }

    public static boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }

    public static void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_POLYGON);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glVertex2f(pickerX, pickerY);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glColor4f(red, green, blue, alpha);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glDisable(GL_ALPHA_TEST);
        glBegin(GL_POLYGON);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX, pickerY);
        glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glEnable(GL_ALPHA_TEST);
        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            RenderUtil.drawRect(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;

            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                RenderUtil.drawGradientRect(x, y + step * (height / 6f), x + width, y + (step + 1) * (height / 6f), previousStep, nextStep, false);
                step++;
            }
            int sliderMinY = (int) (y + height*hue) - 4;
            RenderUtil.drawRect(x, sliderMinY - 1, x + width, sliderMinY + 1,-1);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                RenderUtil.gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6), y + height, previousStep, nextStep, true);
                step++;
            }

            int sliderMinX = (int) (x + (width * hue));
            RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        }
    }

    public void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        boolean left = true;
        int checkerBoardSquareSize = height / 2;

        for (int squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                RenderUtil.drawRect(x + squareIndex, y, x + squareIndex + checkerBoardSquareSize, y + height, 0xFFFFFFFF);
                RenderUtil.drawRect(x + squareIndex, y + checkerBoardSquareSize, x + squareIndex + checkerBoardSquareSize, y + height, 0xFF909090);

                if (squareIndex < width - checkerBoardSquareSize) {
                    int minX = x + squareIndex + checkerBoardSquareSize;
                    int maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
                    RenderUtil.drawRect(minX, y, maxX, y + height, 0xFF909090);
                    RenderUtil.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height,0xFFFFFFFF);
                }
            }

            left = !left;
        }

        RenderUtil.drawLeftGradientRect(x, y, x + width, y + height, new Color(red, green, blue, 1).getRGB(), 0);
        int sliderMinX = (int) (x + width - (width * alpha));
        RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
    }


}