package mint.modules.core;

import mint.modules.Module;
import mint.setting.Setting;

public class GuiTest extends Module {
    static GuiTest INSTANCE = new GuiTest();
    public Setting<Integer> pickerX = register(new Setting("Picker X",100, 0, 200));
    public Setting<Integer> pickerY = register(new Setting("Picker Y",100, 0, 200));
    public Setting<Integer> pickerWidth = register(new Setting("Picker Width",100, 0, 200));
    public Setting<Integer> pickerHeight = register(new Setting("Picker Height",100, 0, 200));

    public Setting<Integer> hueSliderX = register(new Setting("Hue Slider X",100, 0, 200));
    public Setting<Integer> hueSliderY = register(new Setting("Hue Slider Y",100, 0, 200));
    public Setting<Integer> hueSliderWidth = register(new Setting("Hue Slider Width",100, 0, 200));
    public Setting<Integer> hueSliderYHeight= register(new Setting("Hue Slider Height",100, 0, 200));

    public Setting<Integer> alphaSliderX = register(new Setting("Alpha Slider X",100, 0, 200));
    public Setting<Integer> alphaSliderY = register(new Setting("Alpha Slider Y",100, 0, 200));
    public Setting<Integer> alphaSliderWidth = register(new Setting("Alpha Slider Width",100, 0, 200));
    public Setting<Integer> alphaSliderHeight= register(new Setting("Alpha Slider Height",100, 0, 100));

    public GuiTest(){
        super("Gui Test", Category.CORE, "");
        this.setInstance();
    }

    public static GuiTest getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuiTest();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
