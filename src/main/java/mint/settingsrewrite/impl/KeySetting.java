package mint.settingsrewrite.impl;

import mint.modules.Module;
import mint.settingsrewrite.SettingRewrite;

import java.util.function.Predicate;

public class KeySetting extends SettingRewrite<Integer> {

    public boolean isTyping = false;

    public KeySetting(String name, int value, Module module) {
        super(name, value, module);
    }

    public KeySetting(String name, int value, Module module, Predicate<Integer> shown) {
        super(name, value, module, shown);
    }

    public int getKey() {
        return value;
    }

    public void setBind(int bind){
        value = bind;
    }

}
