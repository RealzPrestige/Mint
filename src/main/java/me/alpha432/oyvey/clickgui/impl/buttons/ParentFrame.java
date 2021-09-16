package me.alpha432.oyvey.clickgui.impl.buttons;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.clickgui.OyVeyGui;
import me.alpha432.oyvey.modules.client.ClickGui;
import me.alpha432.oyvey.clickgui.setting.Setting;
import me.alpha432.oyvey.utils.ColorUtil;
import me.alpha432.oyvey.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class ParentFrame extends ButtonFrame {
    private final Setting setting;
    public boolean isVisible;
    public ParentFrame(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int booleancolor = ColorUtil.toARGB(0,0,0, 50);
        int falseboolean = ColorUtil.toARGB(ClickGui.getInstance().stateFalseBooleanRed.getValue(), ClickGui.getInstance().stateFalseBooleanGreen.getValue(), ClickGui.getInstance().stateFalseBooleanBlue.getValue(), ClickGui.getInstance().stateFalseBooleanAlpha.getValue());
        int trueboolean = ColorUtil.toARGB(ClickGui.getInstance().stateTrueBooleanRed.getValue(), ClickGui.getInstance().stateTrueBooleanGreen.getValue(), ClickGui.getInstance().stateTrueBooleanBlue.getValue(), ClickGui.getInstance().stateTrueBooleanAlpha.getValue());
        int sidecolor = ColorUtil.toARGB(ClickGui.getInstance().sideRed.getValue(), ClickGui.getInstance().sideGreen.getValue(), ClickGui.getInstance().sideBlue.getValue(), ClickGui.getInstance().sideAlpha.getValue());
        RenderUtil.drawRect(this.x, this.y, this.x + 1, this.y + (float) this.height + 0.5f, sidecolor);
        OyVey.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) OyVeyGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
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
            OyVey.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
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

