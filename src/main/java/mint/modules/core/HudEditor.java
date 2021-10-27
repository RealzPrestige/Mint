package mint.modules.core;

import mint.modules.Module;
import mint.newgui.hud.HudWindow;

public class HudEditor extends Module {

    public HudEditor(){
        super("Hud Editor", Category.Core, "Edits the hud ye");
    }

    public void onEnable(){
        mc.displayGuiScreen(HudWindow.getInstance());
        disable();
    }
}
