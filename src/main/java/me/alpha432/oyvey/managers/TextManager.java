package me.alpha432.oyvey.managers;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.modules.Feature;
import me.alpha432.oyvey.clickgui.impl.font.CustomFont;
import me.alpha432.oyvey.modules.client.FontMod;
import me.alpha432.oyvey.utils.Timer;
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

    public void init(boolean startup) {
        FontMod cFont = OyVey.moduleManager.getModuleByClass(FontMod.class);
        try {
            this.setFontRenderer(new Font(cFont.fontName.getValue(), cFont.fontStyle.getValue(), cFont.fontSize.getValue()), cFont.antiAlias.getValue(), cFont.fractionalMetrics.getValue());
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public void drawString(String text, float x, float y, int color, boolean shadow) {
        if (OyVey.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
            if (shadow) {
                this.customFont.drawStringWithShadow(text, x, y, color);
            } else {
                this.customFont.drawString(text, x, y, color);
            }
            return;
        }
        OyVey.INSTANCE.mc.fontRenderer.drawString(text, x, y, color, shadow);
    }

    public int getStringWidth(String text) {
        if (OyVey.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
            return this.customFont.getStringWidth(text);
        }
        return OyVey.INSTANCE.mc.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        if (OyVey.moduleManager.isModuleEnabled(FontMod.getInstance().getName())) {
            String text = "A";
            return this.customFont.getStringHeight();
        }
        return OyVey.INSTANCE.mc.fontRenderer.FONT_HEIGHT;
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public Font getCurrentFont() {
        return this.customFont.getFont();
    }

    public void updateResolution() {
        this.scaledWidth = OyVey.INSTANCE.mc.displayWidth;
        this.scaledHeight = OyVey.INSTANCE.mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = OyVey.INSTANCE.mc.isUnicode();
        int i = OyVey.INSTANCE.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        double scaledWidthD = this.scaledWidth / this.scaleFactor;
        double scaledHeightD = this.scaledHeight / this.scaleFactor;
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

