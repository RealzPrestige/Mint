package mint.newgui.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mint.newgui.hud.hudcomponents.HudWatermarkComponent;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        hudModules.add(new HudWatermarkComponent());
    }

    public void Unload() {
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

    void saveHuds() {
        if (!(Files.exists(Paths.get("mint/Default/Huds" + ".json"))))
            try {
                Files.createFile(Paths.get("mint/Default/Huds" + ".json"));
            } catch (IOException ignored) {
            }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            stream = new OutputStreamWriter(new FileOutputStream("mint/Default/Huds" + ".json"), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
        }
        hudObject = new JsonObject();
        for (HudModule hud : hudModules) {
            hudObject.add(hud.getName(), new JsonPrimitive(hud.getValue()));
        }
        try {
            stream.write(gson.toJson(hudObject));
            stream.close();
        } catch (IOException ignored) {
        }
    }
}