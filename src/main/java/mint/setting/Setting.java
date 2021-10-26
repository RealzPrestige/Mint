package mint.setting;

import mint.events.ClientEvent;
import mint.modules.Feature;
import net.minecraftforge.common.MinecraftForge;

import java.awt.*;
import java.util.function.Predicate;

public class Setting<T> {
    private final String name;
    private T defaultValue;
    public T value;
    private T plannedValue;
    private T min;
    private T max;
    private boolean hasRestriction;
    private boolean isParent;
    private Predicate<T> visibility;
    private Color color;
    private boolean isColorSetting;
    private Feature feature;
    public boolean isOpen = false;
    public boolean selected = false;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.plannedValue = defaultValue;
    }

    public Setting(String name, T defaultValue, Boolean parent, Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.visibility = visibility;
        this.plannedValue = defaultValue;
        this.isParent = parent;
    }

    public Setting(String name, Boolean parent, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.plannedValue = defaultValue;
        this.isParent = parent;
    }

    public Setting(String name, T defaultValue, Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.visibility = visibility;
        this.plannedValue = defaultValue;
    }

    public Setting(String name, T defaultValue, T min, T max) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.plannedValue = defaultValue;
        this.hasRestriction = true;
    }

    public Setting(String name, T defaultValue, T min, T max, Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.plannedValue = defaultValue;
        this.visibility = visibility;
        this.hasRestriction = true;
    }

    public Setting(String name, Color color, Predicate<T> visibility) {
        this.name = name;
        this.isColorSetting = true;
        this.color = color;
        this.visibility = visibility;
        this.hasRestriction = true;
    }

    public Setting(String name, Color color) {
        this.name = name;
        this.isColorSetting = true;
        this.color = color;
        this.hasRestriction = true;
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.setPlannedValue(value);
        if (this.hasRestriction) {
            if (((Number) this.min).floatValue() > ((Number) value).floatValue()) {
                this.setPlannedValue(this.min);
            }
            if (((Number) this.max).floatValue() < ((Number) value).floatValue()) {
                this.setPlannedValue(this.max);
            }
        }
        ClientEvent event = new ClientEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.value = this.plannedValue;
        } else {
            this.plannedValue = this.value;
        }
    }

    public T getPlannedValue() {
        return this.plannedValue;
    }

    public void setPlannedValue(T value) {
        this.plannedValue = value;
    }

    public T getMin() {
        return this.min;
    }

    public T getMax() {
        return this.max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public Feature getFeature() {
        return this.feature;
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

    public String currentEnumName() {
        return EnumSetting.getProperName((Enum) this.value);
    }

    public int currentEnum() {
        return EnumSetting.currentEnum((Enum) value);
    }

    public void increaseEnum() {
        this.plannedValue = (T) EnumSetting.increaseEnum((Enum) value);
        ClientEvent event = new ClientEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            this.value = this.plannedValue;
        } else {
            this.plannedValue = this.value;
        }
    }

    public void resetEnum() {
        value = (T) EnumSetting.resetEnum((Enum) value);
    }

    public String getType() {
        if (isEnumSetting())
            return "Enum";
        if (getValue() instanceof Bind)
            return "Bind";
        if (getValue() instanceof Boolean)
            return "Boolean";
        if (getValue() instanceof Double)
            return "Double";
        if (getValue() instanceof Float)
            return "Float";
        if (getValue() instanceof Integer)
            return "Integer";

        return "String";
    }


    public boolean isNumberSetting() {
        return this.value instanceof Double || this.value instanceof Integer || this.value instanceof Short || this.value instanceof Long || this.value instanceof Float;
    }

    public boolean isEnumSetting() {
        return !this.isNumberSetting() && !(this.value instanceof String) && !(this.value instanceof Bind) && !(this.value instanceof Character) && !(this.value instanceof Boolean) && !isColorSetting;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public String getValueAsString() {
        return this.value.toString();
    }

    public String getColorAsString() {
        return String.valueOf(color.getRGB());
    }

    public boolean hasRestriction() {
        return this.hasRestriction;
    }

    public boolean isParent() {
        return this.isParent;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isColorSetting() {
        return isColorSetting;
    }


    public boolean isVisible() {
        if (this.visibility == null) {
            return true;
        }
        return this.visibility.test(this.getValue());
    }
}

