package mint.managers;

import com.google.gson.*;
import mint.Mint;
import mint.modules.Module;
import mint.setting.Bind;
import mint.setting.EnumSetting;
import mint.setting.Setting;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {
    public ArrayList<Module> features = new ArrayList<Module>();
    public String config = "mint/config/";
    public boolean loadingConfig;
    public boolean savingConfig;

    public void loadConfig(String name) {
        loadingConfig = true;
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("mint").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        config = files.contains(new File("mint/" + name + "/")) ? "mint/" + name + "/" : "mint/config/";
        assert Mint.friendManager != null;
        Mint.friendManager.onLoad();
        for (Module feature : features) {
            //    try {
            //       loadSettings(feature);
            //   } catch (IOException ignored) {
            //   }
        }
        saveCurrentConfig();
        loadingConfig = false;
    }

    public void saveConfig(String name) {
        savingConfig = true;
        config = "mint/" + name + "/";
        File path = new File(config);
        if (!path.exists()) {
            path.mkdir();
        }
        assert Mint.friendManager != null;
        //  Mint.friendManager.saveFriends();
        for (Module feature : features) {
            try {
                saveSettings(feature);
            } catch (IOException ignored) {
            }
        }
        saveCurrentConfig();
        savingConfig = false;
    }

    public void createFragFile() {
        File fragFile = new File("mint/customShader.frag/");
        try {
            if (!fragFile.exists())
                fragFile.createNewFile();
        } catch (Exception ignored) {
        }
    }

    public File getFragFile() {
        File fragFile = new File("mint/customShader.frag/");

        if (!fragFile.exists())
            return null;

        return fragFile;
    }

    public void saveCurrentConfig() {
        File currentConfig = new File("mint/Default/activeConfig.txt/");
        try {
            if (currentConfig.exists()) {
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("mint", ""));
                writer.close();
            } else {
                currentConfig.createNewFile();
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("mint", ""));
                writer.close();
            }
        } catch (Exception ignored) {
        }
    }

    public String loadCurrentConfig() {
        File currentConfig = new File("mint/Default/activeConfig.txt");
        String name = "config";
        try {
            if (currentConfig.exists()) {
                Scanner reader = new Scanner(currentConfig);
                while (reader.hasNextLine()) {
                    name = reader.nextLine();
                }
                reader.close();
            }
        } catch (Exception ignored) {
        }
        return name;
    }

    public void saveSettings(Module feature) throws IOException {
        Path outputFile;
        File directory = new File(config + getDirectory(feature));
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!Files.exists(outputFile = Paths.get(config + getDirectory(feature) + feature.getName() + ".json"))) {
            Files.createFile(outputFile);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //String json = gson.toJson(writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
        //writer.write(json);
        writer.close();
    }

    public static void setValueFromJson(Setting setting, JsonElement element) {
        if (setting.isColorSetting()) {
            setting.setColor(new Color(element.getAsInt(), true));
        }
        switch (setting.getType()) {
            case "Boolean": {
                setting.setValue(element.getAsBoolean());
                break;
            }
            case "Double": {
                setting.setValue(element.getAsDouble());
                break;
            }
            case "Float": {
                setting.setValue(element.getAsFloat());
                break;
            }
            case "Integer": {
                setting.setValue(element.getAsInt());
                break;
            }
            case "String": {
                String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                break;
            }
            case "Bind": {
                setting.setValue(new Bind.BindConverter().doBackward(element));
                break;
            }
            case "Enum": {
                try {
                    EnumSetting converter = new EnumSetting(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue(value == null ? setting.getDefaultValue() : value);
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }

    public void init() {
        assert Mint.moduleManager != null;
        features.addAll(Mint.moduleManager.moduleList);
      //  features.add(Mint.friendManager);
        String name = loadCurrentConfig();
        loadConfig(name);
        createFragFile();
    }

    /** private void loadSettings(Module feature) throws IOException {
     String featureName = config + getDirectory(feature) + feature.getName() + ".json";
     Path featurePath = Paths.get(featureName);
     if (!Files.exists(featurePath))
     return;
     loadPath(featurePath, feature);
     } **/

    /** private void loadPath(Path path, Module feature) throws IOException {
     InputStream stream = Files.newInputStream(path);
     try {
     ConfigManager.loadFile(new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
     } catch (IllegalStateException e) {
     ConfigManager.loadFile(new JsonObject(), feature);
     }
     stream.close();
     } **/

    /** private static void loadFile(JsonObject input, Module feature) {
     for (Map.Entry<String, JsonElement> entry : input.entrySet()) {
     String settingName = entry.getKey();
     JsonElement element = entry.getValue();
     try {
     assert Mint.friendManager != null;
     Mint.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
     } catch (Exception ignored) {
     }
     for (Setting setting : feature.getSettings()) {
     if (!settingName.equals(setting.getName()))
     continue;
     try {
     ConfigManager.setValueFromJson(setting, element);
     } catch (Exception ignored) {
     }
     }
     }
     } **/

    /** public JsonObject writeSettings(Module feature) {
     JsonObject object = new JsonObject();
     JsonParser jp = new JsonParser();
     for (Setting setting : feature.getSettings()) {
     if (setting.isEnumSetting()) {
     EnumSetting converter = new EnumSetting(((Enum) setting.getValue()).getClass());
     object.add(setting.getName(), converter.doForward((Enum) setting.getValue()));
     continue;
     }
     if (setting.getValue() instanceof String) {
     String str = (String) setting.getValue();
     setting.setValue(str.replace(" ", "_"));
     continue;
     }
     if (setting.isColorSetting()) {
     object.add(setting.getName(), jp.parse(setting.getColorAsString()));
     }
     try {
     object.add(setting.getName(), jp.parse(setting.getValueAsString()));
     } catch (Exception ignored) {
     }
     }
     return object;
     } **/

    public String getDirectory(Module feature) {
        String directory = "";
        if (feature instanceof Module) {
            directory = directory + "/";
        }
        return directory;
    }

    public boolean configExists(String name) {
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("mint").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        return files.contains(new File("mint/" + name + "/"));
    }
}