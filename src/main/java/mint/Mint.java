package mint;

import mint.commands.CommandManager;
import mint.managers.*;
import mint.managers.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.opengl.Display;

@Mod(modid = "mint", name = "Mint", version = "0.1.1")
public class Mint {
    public Minecraft mc = Minecraft.getMinecraft();
    public static Mint INSTANCE = new Mint();
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static MessageManager messageManager;
    public static ColorManager colorManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    private static boolean unloaded;

    static {
        unloaded = false;
    }

    public static void load() {
        unloaded = false;
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        messageManager = new MessageManager();
        eventManager = new EventManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        configManager = new ConfigManager();
        moduleManager.init();
        eventManager.init();
        textManager.init();
        configManager.init();
        moduleManager.onLoad();
    }
    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(Mint.configManager.config.replaceFirst("mint/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("Mint 0.1.1");
        Mint.load();
    }
}

