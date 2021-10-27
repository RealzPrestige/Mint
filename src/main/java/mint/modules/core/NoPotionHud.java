package mint.modules.core;

import mint.modules.Module;

public class NoPotionHud extends Module {
    private static NoPotionHud INSTANCE = new NoPotionHud();
    public NoPotionHud(){
        super("No Potion Hud", Category.Core, "Removes the potion hud from vanilla");
    }

    public static NoPotionHud getInstance(){
        return INSTANCE;
    }
}
