package mint.settingsrewrite;

import mint.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class SettingsRewrite {

    List<SettingRewrite> settingRewriteList;

    public SettingsRewrite() {
        settingRewriteList = new ArrayList<>();
    }

    public void addSetting(SettingRewrite setting) {
        settingRewriteList.add(setting);
    }

    public List<SettingRewrite> getSettings() {
        return settingRewriteList;
    }

    public List<SettingRewrite> getSettingFromModule(Module module) {
        List<SettingRewrite> settings = new ArrayList<>();
        for (SettingRewrite setting : settingRewriteList)
            if (setting.getModule() == module)
                settings.add(setting);

        return settings;
    }
}
