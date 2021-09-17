package me.alpha432.oyvey.clickgui.impl.buttons;

import me.alpha432.oyvey.Mint;
import me.alpha432.oyvey.clickgui.OyVeyGui;
import me.alpha432.oyvey.modules.client.Gui;
import me.alpha432.oyvey.clickgui.setting.Setting;
import me.alpha432.oyvey.utils.ColorUtil;
import me.alpha432.oyvey.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BooleanFrame
        extends ButtonFrame {
    private final Setting setting;

    public BooleanFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int booleancolor = ColorUtil.toARGB(0,0,0, 50);
        int falseboolean = ColorUtil.toARGB(Gui.getInstance().stateFalseBooleanRed.getValue(), Gui.getInstance().stateFalseBooleanGreen.getValue(), Gui.getInstance().stateFalseBooleanBlue.getValue(), Gui.getInstance().stateFalseBooleanAlpha.getValue());
        int trueboolean = ColorUtil.toARGB(Gui.getInstance().stateTrueBooleanRed.getValue(), Gui.getInstance().stateTrueBooleanGreen.getValue(), Gui.getInstance().stateTrueBooleanBlue.getValue(), Gui.getInstance().stateTrueBooleanAlpha.getValue());
        int sidecolor = ColorUtil.toARGB(Gui.getInstance().sideRed.getValue(), Gui.getInstance().sideGreen.getValue(), Gui.getInstance().sideBlue.getValue(), Gui.getInstance().sideAlpha.getValue());
        RenderUtil.drawRect(this.x, this.y, this.x + 1, this.y + (float) this.height + 0.5f, sidecolor);
        Mint.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) OyVeyGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        RenderUtil.drawRect(this.x + 85, this.y + 5, this.x + 105, this.y + 13, booleancolor);
            if(getState()) {
                RenderUtil.drawRect(this.x + 95, this.y + 6, this.x + 104, this.y + 12, trueboolean);
            } else {
                RenderUtil.drawRect(this.x + 85, this.y + 6, this.x + 94, this.y + 12, falseboolean);
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.setting.setValue(!((Boolean) this.setting.getValue()));
    }

    @Override
    public boolean getState() {
        return (Boolean) this.setting.getValue();
    }
}

