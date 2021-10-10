package mint.managers;

import java.awt.*;

public class ColorManager {
    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private final float alpha = 1.0f;
    private Color color = new Color(this.red, this.green, this.blue, this.alpha);

    public void setColor(Color color) {
        this.color = color;
    }

    public void updateColor() {
        this.setColor(new Color(this.red, this.green, this.blue, this.alpha));
    }

    public void setRed(float red) {
        this.red = red;
        this.updateColor();
    }

    public void setGreen(float green) {
        this.green = green;
        this.updateColor();
    }

    public void setBlue(float blue) {
        this.blue = blue;
        this.updateColor();
    }

}

