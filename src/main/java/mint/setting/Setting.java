package mint.setting;

import mint.modules.Feature;

import java.awt.*;

public class Setting<T> {
    private final String name;
    private T defaultValue;
    public T value;
    private T plannedValue;
    private T max;
    private Color color;
    public boolean selected = false;
    Feature feature;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.plannedValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }

    public T getPlannedValue() {
        return this.plannedValue;
    }

    public void setPlannedValue(T value) {
        this.plannedValue = value;
    }

    public T getMax() {
        return this.max;
    }

    public void setMax(T max) {
        this.max = max;
    }


    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public int getEnum(String input) {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; ++i) {
            Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (!e.name().equalsIgnoreCase(input)) continue;
            return i;
        }
        return -1;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

