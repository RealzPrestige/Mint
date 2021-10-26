package mint.newgui.hud;

import com.google.gson.JsonObject;
import mint.newgui.hud.hudcomponents.HudWatermarkComponent;

import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class HudComponentManager {
    static HudComponentManager INSTANCE = new HudComponentManager();
    JsonObject hudObject;
    OutputStreamWriter stream;
    ArrayList<HudModule> hudModules = new ArrayList<>();

    public HudComponentManager() {
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

    public void load() {
        init();
    }

    public void init() {
        loadHuds();
        hudModules.add(new HudWatermarkComponent());
    }

    public void unload() {
        saveHudsActive();
        saveHudsPos();
        hudModules.clear();
    }

    public ArrayList<HudModule> getHudModules() {
        return hudModules;
    }

    public void drawText() {
        for (HudModule hudModule : hudModules) {
            if (hudModule.getValue())
                hudModule.drawText();
        }
    }

    /**
     * @author kambing
     */

    void loadHuds() {

    }

    void saveHudsActive() {
        //
    }

    void saveHudsPos() {
        //
    }
}