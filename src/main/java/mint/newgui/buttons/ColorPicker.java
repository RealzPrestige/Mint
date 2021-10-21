package mint.newgui.buttons;

import mint.clickgui.setting.Setting;
import mint.utils.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

/**
 * @author kambing
 * 20/10/2021
 */

public class ColorPicker extends GuiScreen {
    Setting setting;
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
        RenderUtil.drawRect(0, 0, 100.0f, 300.0f, setting.getColor().getRGB());
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        if (Mouse.isButtonDown(0)) {
            setting.setColor(robot.getPixelColor(mouseX, mouseY));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            setting.isOpen = false;
            mc.displayGuiScreen(null);
        }
    }
}
