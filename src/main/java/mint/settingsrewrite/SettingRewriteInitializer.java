package mint.settingsrewrite;

import mint.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class SettingRewriteInitializer {

    List<SettingRewrite> settingRewriteList;

    public SettingRewriteInitializer() {
        settingRewriteList = new ArrayList<>();
    }

    public void addSetting(SettingRewrite setting) {
        settingRewriteList.add(setting);
    }


    public List<SettingRewrite> getSettingsInModule(Module module) {
        List<SettingRewrite> settings = new ArrayList<>();
        for (SettingRewrite setting : settingRewriteList) {
            if (setting == null || settingRewriteList.isEmpty())
                continue;

            if (setting.getModule().equals(module))
                settings.add(setting);
        }
        return settings;
    }

}
