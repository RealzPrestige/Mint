package me.alpha432.oyvey.clickgui.impl.buttons;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.clickgui.OyVeyGui;
import me.alpha432.oyvey.clickgui.impl.Component;
import me.alpha432.oyvey.clickgui.impl.Frame;
import me.alpha432.oyvey.modules.client.Gui;
import me.alpha432.oyvey.utils.ColorUtil;
import me.alpha432.oyvey.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class ButtonFrame
        extends Frame {
    private boolean state;

    public ButtonFrame(String name) {
        super(name);
        this.height = 15;
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height - 0.5f, getState() ? ColorUtil.toRGBA(Gui.getInstance().red.getValue(), Gui.getInstance().green.getValue(), Gui.getInstance().blue.getValue(), isHovering(mouseX, mouseY) ? Gui.getInstance().alpha.getValue() - 20 : Gui.getInstance().alpha.getValue()) : -2007673515);
        OyVey.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float) OyVeyGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        OyVey.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : OyVeyGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}

