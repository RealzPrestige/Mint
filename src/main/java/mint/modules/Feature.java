package mint.modules;

import mint.Mint;
import mint.managers.TextManager;
import mint.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class Feature {
    public List<Setting> settings = new ArrayList<>();
    public TextManager renderer = Mint.textManager;
    private final String name;

    public Feature(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }


    public Setting register(Setting setting) {
        setting.setFeature(this);
        this.settings.add(setting);
        return setting;
    }

    public void clearSettings() {
        this.settings = new ArrayList<>();
    }
}

