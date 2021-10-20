package mint.clickgui.impl.buttons;

import mint.Mint;
import mint.clickgui.MintGui;
import mint.clickgui.impl.ColorPicker;
import mint.clickgui.setting.Setting;
import mint.modules.core.Gui;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;

/**
 * @author kambing
 * 2/10/2021
 */

public class ColorFrame extends ButtonFrame { //TODO: finish this
    public static final Minecraft mc = Minecraft.getMinecraft();
    public Setting setting;
    public Boolean open = false;

    public ColorFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int sideColor = ColorUtil.toRGBA(Gui.getInstance().sideRed.getValue(), Gui.getInstance().sideGreen.getValue(), Gui.getInstance().sideBlue.getValue(), Gui.getInstance().sideAlpha.getValue());
        RenderUtil.drawRect(this.x + 83.0f - 4.0f, this.y + 4.0f, this.x + this.width, this.y + this.height - 3.0f, ColorUtil.toRGBA(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue()));
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 8.0f, this.y + (float) this.height - 0.5f, sideColor);
        Mint.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) MintGui.getClickGui().getTextOffset(), -1);
        if (this.open) {
            mc.displayGuiScreen(new ColorPicker(setting));
        }
    }


    @Override
    public void update() {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.setHidden(!this.setting.isVisible());
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY) && !this.open) {
            this.open = true;
        }else if (mouseButton == 0 && this.isHovering(mouseX, mouseY) && this.open) {
            this.open = false;
        }
    }
}