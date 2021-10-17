package mint.newgui;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.Module;
import mint.modules.core.NewGuiModule;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.ArrayList;

public class Window {

    String name;
    int x;
    int y;
    int width;
    int height;
    Module.Category category;

    boolean isDragging;
    int dragX;
    int dragY;

    boolean isOpened;

    ArrayList<ModuleWindow> modules = new ArrayList<>();

    public Window(String name, int x, int y, int width, int height, Module.Category category) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;
        isOpened = true;
    }

    public void dragScreen(int mouseX, int mouseY) {
        if (!isDragging)
            return;
        x = dragX + mouseX;
        y = dragY + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragScreen(mouseX, mouseY);
        RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(NewGuiModule.getInstance().topRed.getValue(), NewGuiModule.getInstance().topGreen.getValue(), NewGuiModule.getInstance().topBlue.getValue(), NewGuiModule.getInstance().topAlpha.getValue()));
        Mint.textManager.drawStringWithShadow(name, x + (width / 2f) - (Mint.textManager.getStringWidth(name) / 2f), y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        Mint.textManager.drawStringWithShadow(isOpened ? (isInsideCloseButton(mouseX, mouseY) ? ChatFormatting.UNDERLINE + "x" : "x") : (isInsideCloseButton(mouseX, mouseY) ? ChatFormatting.UNDERLINE + "+" : "+"), x + width - Mint.textManager.getStringWidth("x"), y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        if (isOpened) {
            modules.clear();
            int y = this.y;
            for (Module module : Mint.moduleManager.getModulesByCategory(category))
                modules.add(new ModuleWindow(module.getName(), x, y += height, width, 10, new Color(
                        NewGuiModule.getInstance().moduleRed.getValue(),
                        NewGuiModule.getInstance().moduleGreen.getValue(),
                        NewGuiModule.getInstance().moduleBlue.getValue(),
                        NewGuiModule.getInstance().moduleAlpha.getValue()
                ), new Color(
                        NewGuiModule.getInstance().enabledRed.getValue(),
                        NewGuiModule.getInstance().enabledGreen.getValue(),
                        NewGuiModule.getInstance().enabledBlue.getValue(),
                        NewGuiModule.getInstance().enabledAlpha.getValue()
                ), module));
        }
        if (isOpened)
            modules.forEach(modules -> modules.drawScreen(mouseX, mouseY, partialTicks));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isInside(mouseX, mouseY) && !isInsideCloseButton(mouseX, mouseY)) {
            dragX = x - mouseX;
            dragY = y - mouseY;
            isDragging = true;
        }
        if (mouseButton == 0 && isInsideCloseButton(mouseX, mouseY)) {
            isOpened = !isOpened;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        for(ModuleWindow moduleWindow : modules)
            moduleWindow.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0)
            isDragging = false;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    public boolean isInsideCloseButton(int mouseX, int mouseY) {
        return (mouseX > x + width - Mint.textManager.getStringWidth("x") && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }
}