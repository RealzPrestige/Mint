package mint.settingsrewrite.impl;

import mint.modules.Module;
import mint.settingsrewrite.SettingRewrite;
import mint.utils.EnumUtil;

import java.util.function.Predicate;

public final class EnumSetting extends SettingRewrite<Enum> {

    Enum<?>[] constants;
    int modeIndex;
    int valueIndex;

    public EnumSetting(String name, Enum<?> value, Module module) {
        super(name, value, module);
        constants = value.getDeclaringClass().getEnumConstants();
        valueIndex = modeIndex = indexOf(value);
    }

    public EnumSetting(String name, Enum<?> value, Module module, Predicate<Enum> shown) {
        super(name, value, module, shown);
        constants = value.getDeclaringClass().getEnumConstants();
        valueIndex = modeIndex = indexOf(value);
    }

    public Enum<?>[] getConstants(){
        return constants;
    }

    public void increase() {
        if (modeIndex == constants.length - 1)
            modeIndex = 0;
        else modeIndex++;
    }

    public void decrease() {
        if (modeIndex == 0)
            modeIndex = constants.length - 1;
        else modeIndex--;
    }

    public Enum<?> getValueEnum() {
        return constants[modeIndex];
    }

    public int indexOf(Enum<?> value) {
        for (int i = 0; i < constants.length; i++)
            if (constants[i] == value)
                return i;
        return -1;
    }

    public void setEnum(String enumString) {
        EnumUtil converter = new EnumUtil(((Enum) getValue()).getClass());
        Enum value = converter.doBackward(enumString);
        setValue(value == null ? getValue() : value);
    }
}
