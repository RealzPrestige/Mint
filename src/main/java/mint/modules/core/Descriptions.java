package mint.modules.core;

import mint.clickgui.setting.Setting;
import mint.modules.Module;

public class Descriptions extends Module {
    private static Descriptions INSTANCE = new Descriptions();
    public Setting<Mode> mode = register(new Setting("Mode", Mode.HOVER));
    public enum Mode{BOTTOMLEFT, HOVER}
    public Setting<Boolean> rect = register(new Setting("Rectangle", false));
    public Setting<Boolean> outline = register(new Setting("Outline", false));
    public Descriptions(){
        super("Descriptions", Category.CORE, "Shows descriptions when you hover over a Module");
        this.setInstance();
    }

    public static Descriptions getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Descriptions();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
