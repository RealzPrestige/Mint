package mint.clickgui.impl.buttons;

import mint.Mint;
import mint.clickgui.MintGui;
import mint.clickgui.impl.Component;
import mint.clickgui.impl.Frame;
import mint.commands.Command;
import mint.modules.Module;
import mint.clickgui.setting.BindSetting;
import mint.clickgui.setting.Setting;
import mint.modules.client.Gui;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleFrame
        extends ButtonFrame {
    private final Module module;
    private List<Frame> items = new ArrayList<Frame>();
    private boolean subOpen;
    public ModuleFrame(Module module) {
        super(module.getName());
        this.module = module;
        this.initSettings();
    }


    public void initSettings() {
        ArrayList<Frame> newItems = new ArrayList<>();
        if (!this.module.getSettings().isEmpty()) {
            for (Setting setting : this.module.getSettings()) {
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanFrame(setting));
                }
                if (setting.getValue() instanceof BindSetting && !setting.getName().equalsIgnoreCase("Keybind") && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new BindFrame(setting));
                }
                if ((setting.getValue() instanceof String || setting.getValue() instanceof Character) && !setting.getName().equalsIgnoreCase("displayName")) {
                    newItems.add(new StringFrame(setting));
                }
                if (setting.isNumberSetting() && setting.hasRestriction()) {
                    newItems.add(new IntegerFrame(setting));
                    continue;
                }
                if (!setting.isEnumSetting()) continue;
                newItems.add(new EnumFrame(setting));
            }
        }
        newItems.add(new BindFrame(this.module.getSettingByName("Keybind")));
        this.items = newItems;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {
            Mint.textManager.drawString((this.subOpen ? "v" : ">"), this.x - 1.5f + (float) this.width - 7.4f, this.y - 2.0f - (float) MintGui.getClickGui().getTextOffset(), -1, false);
            if (this.subOpen) {
                float height = 1.0f;
                for (Frame item : this.items) {
                    Component.counter1[0] = Component.counter1[0] + 1;
                    if (!item.isHidden()) {
                        item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                        item.setHeight(15);
                        item.setWidth(this.width - 9);
                        item.drawScreen(mouseX, mouseY, partialTicks);
                    }
                    item.update();
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (mouseButton == 2 && this.isHovering(mouseX, mouseY)) {
                if(module.isDrawn()) {
                    module.setUndrawn();
                    Command.sendMessage(module.getName() + " is no longer Drawn.");
                } else {
                    module.setDrawn();
                    Command.sendMessage(module.getName() + " is now Drawn.");
                }
                Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (this.subOpen) {
                for (Frame item : this.items) {
                    if (item.isHidden()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
        if(isHovering(mouseX, mouseY)){
            RenderUtil.drawRect(mouseX + 10, mouseY, mouseX + 10 + renderer.getStringWidth(module.getDescription()), mouseY + 10, ColorUtil.toRGBA(0,0,0, 50));
            RenderUtil.drawBorder(mouseX + 10, mouseY, renderer.getStringWidth(module.getDescription()), 10, new Color(ColorUtil.toRGBA(Gui.getInstance().red.getValue(), Gui.getInstance().green.getValue(), Gui.getInstance().blue.getValue(), 30 / 255)));
            renderer.drawStringWithShadow(module.getDescription(), mouseX + 10, mouseY, -1);
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            for (Frame item : this.items) {
                if (item.isHidden()) continue;
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.subOpen) {
            int height = 14;
            for (Frame item : this.items) {
                if (item.isHidden()) continue;
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return 14;
    }

    public Module getModule() {
        return this.module;
    }

    @Override
    public void toggle() {
        this.module.toggle();
    }

    @Override
    public boolean getState() {
        return this.module.isEnabled();
    }
}

