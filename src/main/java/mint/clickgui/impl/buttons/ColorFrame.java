package mint.clickgui.impl.buttons;

import mint.Mint;
import mint.clickgui.MintGui;
import mint.clickgui.setting.Setting;
import mint.modules.core.Gui;
import mint.newgui.buttons.ColorPicker;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;

/**
 * @author kambing
 * 2/10/2021
 */

public class ColorFrame extends ButtonFrame {
    public static final Minecraft mc = Minecraft.getMinecraft();
    int bgColor = ColorUtil.toRGBA(Gui.getInstance().backgroundRed.getValue(), Gui.getInstance().backgroundGreen.getValue(), Gui.getInstance().backgroundBlue.getValue(), Gui.getInstance().backgroundAlpha.getValue());
    public Setting setting;

    public ColorFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x + 83.0f - 4.0f, this.y + 4.0f, this.x + this.width - 10, this.y + this.height - 3.0f, ColorUtil.toRGBA(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue()));
        int sideColor = ColorUtil.toRGBA(Gui.getInstance().sideRed.getValue(), Gui.getInstance().sideGreen.getValue(), Gui.getInstance().sideBlue.getValue(), Gui.getInstance().sideAlpha.getValue()); RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x, this.y - 2, this.x + 1, this.y + this.height, sideColor);
        RenderUtil.drawRect(this.x + 113, this.y - 2, this.x + 114, this.y + this.height, sideColor);
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) MintGui.getClickGui().getTextOffset(), -1);
        if (this.setting.isOpen) {
            mc.displayGuiScreen(new ColorPicker(setting));
        }
    }


    @Override
    public void update() {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.setHidden(!this.setting.isVisible());
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY) && !this.setting.isOpen) {
            this.setting.isOpen = true;
        }else if (mouseButton == 1 && this.isHovering(mouseX, mouseY) && this.setting.isOpen) {
            this.setting.isOpen = false;
        }
    }
}