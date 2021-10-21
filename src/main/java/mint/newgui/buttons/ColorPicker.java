package mint.newgui.buttons;

import mint.clickgui.setting.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author kambing
 * 20/10/2021
 */

public class ColorPicker extends GuiScreen {
    private ResourceLocation resourceLocation = new ResourceLocation("textures/colorpicker.png");
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
        /* @zPrestige make this how you want i dont mind
        RenderUtil.drawRect(0, 0, 100.0f, 300.0f, setting.getColor().getRGB());
        this.mc.getTextureManager().bindTexture(this.resourceLocation);
        drawCompleteImage(300,300,100,100);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }*/
        if (Mouse.isButtonDown(0)) {
            setting.setColor(robot.getPixelColor(mouseX, mouseY));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            setting.isOpen = false;
            mc.displayGuiScreen(null);
        }
    }


    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) posX, (float) posY, (float) 0.0f);
        GL11.glBegin((int) 7);
        GL11.glTexCoord2f((float) 0.0f, (float) 0.0f);
        GL11.glVertex3f((float) 0.0f, (float) 0.0f, (float) 0.0f);
        GL11.glTexCoord2f((float) 0.0f, (float) 1.0f);
        GL11.glVertex3f((float) 0.0f, (float) height, (float) 0.0f);
        GL11.glTexCoord2f((float) 1.0f, (float) 1.0f);
        GL11.glVertex3f((float) width, (float) height, (float) 0.0f);
        GL11.glTexCoord2f((float) 1.0f, (float) 0.0f);
        GL11.glVertex3f((float) width, (float) 0.0f, (float) 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
