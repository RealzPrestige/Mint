package mint.newgui.hud;

import mint.newgui.hud.hudcomponents.HudWatermarkComponent;

import java.util.ArrayList;

public class HudComponentManager {
    static HudComponentManager INSTANCE = new HudComponentManager();
    ArrayList<HudModule> hudModules = new ArrayList<>();
    public HudComponentManager(){
        setInstance();
    }

    public static HudComponentManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new HudComponentManager();
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }

    public void load(){
        init();
    }

    public void init(){
        hudModules.add(new HudWatermarkComponent());
    }

    public void Unload(){
        hudModules.clear();
    }

    public ArrayList<HudModule> getHudModules(){
        return hudModules;
    }

    public void drawText(){
        for(HudModule hudModule : hudModules){
            if(hudModule.getValue())
                hudModule.drawText();
        }
    }
}
