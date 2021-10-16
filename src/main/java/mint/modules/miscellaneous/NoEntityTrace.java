package mint.modules.miscellaneous;

import mint.clickgui.setting.Setting;
import mint.modules.Module;

public class NoEntityTrace extends Module {
    private static NoEntityTrace INSTANCE = new NoEntityTrace();
    public Setting<Boolean> pickaxe = register(new Setting<>("Pickaxe", true));
    public Setting<Boolean> gapple = register(new Setting<>("Gapple", false));

    public NoEntityTrace() {
        super("NoEntityTrace", Category.MISCELLANEOUS, "Prevents you from hitting things.");
        setInstance();
    }

    public static NoEntityTrace getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoEntityTrace();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}


