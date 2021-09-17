package mint.managers;

import mint.Mint;
import mint.modules.Feature;
import mint.clickgui.impl.font.CustomFont;
import mint.modules.client.FontChanger;
import mint.utils.Timer;
import net.minecraft.util.math.MathHelper;
import java.awt.*;

public class TextManager
        extends Feature {
    private final Timer idleTimer = new Timer();
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    private CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, false);
    private boolean idling;

    public TextManager() {
        this.updateResolution();
    }

    public void init() {
        FontChanger cFont = Mint.moduleManager.getModuleByClass(FontChanger.class);
        try {
            this.setFontRenderer(new Font("Dialog", getStyle(), cFont.fontSize.getValue()), true, true);
        } catch (Exception ignored) {
        }
    }

    public int getStyle(){
        switch(FontChanger.getInstance().style.getValue()) {
            case NORMAL: {
                return 0;
            }
            case ITALIC: {
                return 2;
            }
            case BOLD: {
                return 1;
            }
            case ITALICBOLD: {
                return 3;
            }
        }
        return 0;
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public void drawString(String text, float x, float y, int color, boolean shadow) {
        if (Mint.moduleManager.isModuleEnabled(FontChanger.getInstance().getName())) {
            if (shadow) {
                this.customFont.drawStringWithShadow(text, x, y, color);
            } else {
                this.customFont.drawString(text, x, y, color);
            }
            return;
        }
        Mint.INSTANCE.mc.fontRenderer.drawString(text, x, y, color, shadow);
    }

    public int getStringWidth(String text) {
        if (Mint.moduleManager.isModuleEnabled(FontChanger.getInstance().getName())) {
            return this.customFont.getStringWidth(text);
        }
        return Mint.INSTANCE.mc.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        if (Mint.moduleManager.isModuleEnabled(FontChanger.getInstance().getName())) {
            return this.customFont.getStringHeight();
        }
        return Mint.INSTANCE.mc.fontRenderer.FONT_HEIGHT;
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public void updateResolution() {
        this.scaledWidth = Mint.INSTANCE.mc.displayWidth;
        this.scaledHeight = Mint.INSTANCE.mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = Mint.INSTANCE.mc.isUnicode();
        int i = Mint.INSTANCE.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        double scaledWidthD = scaledWidth / scaleFactor;
        double scaledHeightD = scaledHeight / scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public String getIdleSign() {
        if (this.idleTimer.passedMs(500L)) {
            this.idling = !this.idling;
            this.idleTimer.reset();
        }
        if (this.idling) {
            return "_";
        }
        return "";
    }
}

