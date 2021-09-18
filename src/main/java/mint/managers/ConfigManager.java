package mint.managers;

import com.google.gson.*;
import mint.Mint;
import mint.clickgui.setting.BindSetting;
import mint.clickgui.setting.EnumSetting;
import mint.clickgui.setting.Setting;
import mint.modules.Feature;
import mint.modules.Module;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {
    public ArrayList<Feature> features = new ArrayList<>();

    public String config = "mint/config/";

    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        String str;
        switch (setting.getType()) {
            case "Boolean":
                setting.setValue(element.getAsBoolean());
                return;
            case "Double":
                setting.setValue(element.getAsDouble());
                return;
            case "Float":
                setting.setValue(element.getAsFloat());
                return;
            case "Integer":
                setting.setValue(element.getAsInt());
                return;
            case "String":
                str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                return;
            case "Bind":
                setting.setValue((new BindSetting.BindConverter()).doBackward(element));
                return;
            case "Enum":
                try {
                    EnumSetting converter = new EnumSetting(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue((value == null) ? setting.getDefaultValue() : value);
                } catch (Exception exception) {
                }
                return;
        }
    }

    private static void loadFile(JsonObject input, Feature feature) {
        for (Map.Entry<String, JsonElement> entry : input.entrySet()) {
            String settingName = entry.getKey();
            JsonElement element = entry.getValue();
            if (feature instanceof FriendManager) {
                try {
                    Mint.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            boolean settingFound = false;
            for (Setting setting : feature.getSettings()) {
                if (settingName.equals(setting.getName())) {
                    try {
                        setValueFromJson(feature, setting, element);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    settingFound = true;
                }
            }
            if (settingFound) ;
        }
    }

    public void loadConfig(String name) {
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("mint").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        if (files.contains(new File("mint/" + name + "/"))) {
            this.config = "mint/" + name + "/";
        } else {
            this.config = "mint/config/";
        }
        Mint.friendManager.onLoad();
        for (Feature feature : this.features) {
            try {
                loadSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveCurrentConfig();
    }

    public boolean configExists(String name) {
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("mint").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        return files.contains(new File("mint/" + name + "/"));
    }

    public void saveConfig(String name) {
        this.config = "mint/" + name + "/";
        File path = new File(this.config);
        if (!path.exists())
            path.mkdir();
        Mint.friendManager.saveFriends();
        for (Feature feature : this.features) {
            try {
                saveSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveCurrentConfig();
    }

    public void saveCurrentConfig() {
        File currentConfig = new File("mint/currentconfig.txt");
        try {
            if (currentConfig.exists()) {
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("mint", ""));
                writer.close();
            } else {
                currentConfig.createNewFile();
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("mint", ""));
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadCurrentConfig() {
        File currentConfig = new File("mint/currentconfig.txt");
        String name = "config";
        try {
            if (currentConfig.exists()) {
                Scanner reader = new Scanner(currentConfig);
                while (reader.hasNextLine())
                    name = reader.nextLine();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public void resetConfig(boolean saveConfig, String name) {
        for (Feature feature : this.features)
            feature.reset();
        if (saveConfig)
            saveConfig(name);
    }

    public void saveSettings(Feature feature) throws IOException {
        JsonObject object = new JsonObject();
        File directory = new File(this.config + getDirectory(feature));
        if (!directory.exists())
            directory.mkdir();
        String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
        Path outputFile = Paths.get(featureName);
        if (!Files.exists(outputFile))
            Files.createFile(outputFile);
        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        String json = gson.toJson(writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
        writer.write(json);
        writer.close();
    }

    public void init() {
        this.features.addAll(Mint.moduleManager.moduleList);
        this.features.add(Mint.friendManager);
        String name = loadCurrentConfig();
        loadConfig(name);
    }

    private void loadSettings(Feature feature) throws IOException {
        String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
        Path featurePath = Paths.get(featureName);
        if (!Files.exists(featurePath))
            return;
        loadPath(featurePath, feature);
    }

    private void loadPath(Path path, Feature feature) throws IOException {
        InputStream stream = Files.newInputStream(path);
        try {
            loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
        } catch (IllegalStateException e) {
            loadFile(new JsonObject(), feature);
        }
        stream.close();
    }

    public JsonObject writeSettings(Feature feature) {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();
        for (Setting setting : feature.getSettings()) {
            if (setting.isEnumSetting()) {
                EnumSetting converter = new EnumSetting(((Enum) setting.getValue()).getClass());
                object.add(setting.getName(), converter.doForward((Enum) setting.getValue()));
                continue;
            }
            if (setting.isStringSetting()) {
                String str = (String) setting.getValue();
                setting.setValue(str.replace(" ", "_"));
            }
            try {
                object.add(setting.getName(), jp.parse(setting.getValueAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public String getDirectory(Feature feature) {
        String directory = "";
        if (feature instanceof Module)
            directory = directory + ((Module) feature).getCategory().getName() + "/";
        return directory;
    }
}
