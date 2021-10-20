package mint.clickgui.impl;

import mint.clickgui.setting.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author kambing
 * 20/10/2021
 */

public class ColorPicker extends GuiScreen {
    public ResourceLocation resourceLocation;
    Setting setting;
    public static BufferedImage image;
    Robot robot;

    public ColorPicker(Setting setting) {
        this.setting = setting;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // @zPrestige_
        this.resourceLocation = new ResourceLocation("textures/colorPicker.png");
        /**
         * ive made a color picker image to make it easier
         * you can easily get the color
         * by using:
         */
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        setting.setColor(robot.getPixelColor(mouseX,mouseY));
    }
}
