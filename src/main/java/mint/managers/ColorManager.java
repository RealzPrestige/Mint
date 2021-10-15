package mint.managers;

import java.awt.*;

public class ColorManager {
    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;

    public void updateColor() {
        float alpha = 1.0f;
        new Color(this.red, this.green, this.blue, alpha);
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

