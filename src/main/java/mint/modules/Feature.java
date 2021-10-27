package mint.modules;

import mint.Mint;
import mint.managers.TextManager;
import mint.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class Feature {
    public List<Setting> settings = new ArrayList<>();
    public TextManager renderer = Mint.textManager;
    private String name;

    public Feature(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public boolean isEnabled() {
        if (this instanceof Module) {
            return ((Module) this).isOn();
        }
        return false;
    }

    public boolean isDisabled() {
        return !this.isEnabled();
    }

    public Setting register(Setting setting) {
        setting.setFeature(this);
        this.settings.add(setting);
        return setting;
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.settings) {
            if (!setting.getName().equalsIgnoreCase(name)) continue;
            return setting;
        }
        return null;
    }

    public void clearSettings() {
        this.settings = new ArrayList<>();
    }
}

