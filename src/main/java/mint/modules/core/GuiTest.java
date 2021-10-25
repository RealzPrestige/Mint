package mint.modules.core;

import mint.modules.Module;
import mint.setting.Bind;
import mint.setting.Setting;

public class GuiTest extends Module {
    public Setting<Boolean> booleanSetting = register(new Setting("Boolean Setting", false));
    public Setting<Boolean> parentSetting = register(new Setting("Parent Setting",true, false));
    public Setting<Bind> keybindSetting = register(new Setting("Keybind Setting",new Bind(-1)));
    public Setting<Mode> modeSetting = register(new Setting("Mode Setting",Mode.One));
    public enum Mode{One, Two}
    public Setting<Integer> integerSetting = register(new Setting("Integer Setting",100, 0, 1000));
    public Setting<String> stringSetting = register(new Setting("String Setting", "Joe"));
    public GuiTest(){
        super("Gui Test", Category.CORE, "");
    }
}
