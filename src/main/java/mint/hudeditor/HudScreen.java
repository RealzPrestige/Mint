package mint.hudeditor;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.PacketManager;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;

import java.awt.*;

public class HudScreen extends GuiScreen {
    static HudScreen INSTANCE;
    public int x = 100;
    public int y = 100;
    public int width = 200;
    public int height = 10;
    public int dragX;
    public int dragY;
    boolean isDragging;
    boolean isOpened = true;
    public CurrentScreen currentScreen;

    public enum CurrentScreen {Sent, Incoming, None}

    static {
        INSTANCE = new HudScreen();
    }

    public HudScreen() {
        setInstance();
    }

    public static HudScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudScreen();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void dragScreen(int mouseX, int mouseY) {
        if (!isDragging)
            return;
        x = dragX + mouseX;
        y = dragY + mouseY;
    }

    @Override
    public void onGuiClosed() {
        PacketManager.getInstance().disable();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragScreen(mouseX, mouseY);
        RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(PacketManager.getInstance().topRed.getValue(), PacketManager.getInstance().topGreen.getValue(), PacketManager.getInstance().topBlue.getValue(), PacketManager.getInstance().topAlpha.getValue()));
        RenderUtil.drawBorder(x, y, width, isOpened ? height * 10 : height, new Color(PacketManager.getInstance().topRed.getValue(), PacketManager.getInstance().topGreen.getValue(), PacketManager.getInstance().topBlue.getValue(), PacketManager.getInstance().topAlpha.getValue()));
        if (isOpened) {
            RenderUtil.drawRect(x, y + height, x + width, y + (height * 10), ColorUtil.toRGBA(PacketManager.getInstance().componentRed.getValue(), PacketManager.getInstance().componentGreen.getValue(), PacketManager.getInstance().componentBlue.getValue(), PacketManager.getInstance().componentAlpha.getValue()));
            RenderUtil.drawRect(x, y + height, x + (width / 2f), y + (height * 2) + 5, isHoveringButton1(mouseX, mouseY) ? ColorUtil.toRGBA(255, 255, 255, 100) : ColorUtil.toRGBA(PacketManager.getInstance().componentRed.getValue(), PacketManager.getInstance().componentGreen.getValue(), PacketManager.getInstance().componentBlue.getValue(), PacketManager.getInstance().componentAlpha.getValue()));
            RenderUtil.drawRect(x + (width / 2f) + 1, y + height, x + width, y + (height * 2) + 5, isHoveringButton2(mouseX, mouseY) ? ColorUtil.toRGBA(255, 255, 255, 100) : ColorUtil.toRGBA(PacketManager.getInstance().componentRed.getValue(), PacketManager.getInstance().componentGreen.getValue(), PacketManager.getInstance().componentBlue.getValue(), PacketManager.getInstance().componentAlpha.getValue()));
            RenderUtil.drawRect(x, y + height + height + 5, x + width, y + height + height + 6, ColorUtil.toRGBA(PacketManager.getInstance().topRed.getValue(), PacketManager.getInstance().topGreen.getValue(), PacketManager.getInstance().topBlue.getValue(), PacketManager.getInstance().topAlpha.getValue()));
            RenderUtil.drawRect(x + (width / 2f), y + height, x + (width / 2f) + 1, y + (height * 2) + 5, ColorUtil.toRGBA(PacketManager.getInstance().topRed.getValue(), PacketManager.getInstance().topGreen.getValue(), PacketManager.getInstance().topBlue.getValue(), PacketManager.getInstance().topAlpha.getValue()));

            assert Mint.textManager != null;
            Mint.textManager.drawString("Incoming Packets", x + (width / 4f) - (Mint.textManager.getStringWidth("Incoming Packets") / 2f), y + height + (height / 2f) - (Mint.textManager.getFontHeight() / 2f) + 2, -1, true);
            Mint.textManager.drawString("Sent Packets", x + ((width / 4f) * 3) - (Mint.textManager.getStringWidth("Sent Packets") / 2f), y + height + (height / 2f) - (Mint.textManager.getFontHeight() / 2f) + 2, -1, true);
            int pY = y + (height * 2) + 7;
            if (currentScreen.equals(CurrentScreen.Incoming)) {
                for (Packet packet : Mint.eventManager.packets) {
                    if(Mint.eventManager.packets.isEmpty())
                        continue;
                    Mint.textManager.drawString("[Mint] : " + packet, x, pY, -1, true);
                    pY += 10;
                }
            }
        }
        assert Mint.textManager != null;
        Mint.textManager.drawString("Packet Manager", x + (width / 2f) - (Mint.textManager.getStringWidth("Packet Manager") / 2f) - 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f) - 1, -1, true);
        Mint.textManager.drawString(isHoveringCloseButton(mouseX, mouseY) ? ChatFormatting.UNDERLINE + "x" : "x", x + width - Mint.textManager.getStringWidth("x") - 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f) - 3, -1, true);
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    public boolean isHoveringButton1(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + (width / 2f)) && (mouseY > y + height && mouseY < y + (height * 2) + 5);
    }

    public boolean isHoveringButton2(int mouseX, int mouseY) {
        return (mouseX > x + (width / 2f) && mouseX < x + width) && (mouseY > y + height && mouseY < y + (height * 2) + 5);
    }

    public boolean isHoveringCloseButton(int mouseX, int mouseY) {
        return (mouseX > x + width - 10 && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHoveringCloseButton(mouseX, mouseY)) {
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            Mint.INSTANCE.mc.displayGuiScreen(null);
        }
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            dragX = x - mouseX;
            dragY = y - mouseY;
            isDragging = true;
        }
        if (mouseButton == 0 && isHoveringButton1(mouseX, mouseY)) {
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            currentScreen = CurrentScreen.Incoming;
        }

        if (mouseButton == 0 && isHoveringButton2(mouseX, mouseY)) {
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            currentScreen = CurrentScreen.Sent;
        }

        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (isOpened)
                isOpened = false;
            else
                isOpened = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            isDragging = false;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
