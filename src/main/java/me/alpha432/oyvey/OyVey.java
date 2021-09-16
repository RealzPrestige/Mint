package me.alpha432.oyvey;

import me.alpha432.oyvey.commands.CommandManager;
import me.alpha432.oyvey.managers.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = "oyvey", name = "OyVey", version = "0.0.3")
public class OyVey {
    public Minecraft mc = Minecraft.getMinecraft();
    public static OyVey INSTANCE = new OyVey();
    public static final String MODID = "oyvey";
    public static final String MODNAME = "OyVey";
    public static final String MODVER = "0.0.3";
    public static final Logger LOGGER = LogManager.getLogger("OyVey");
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
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
        packetManager = new PacketManager();
        eventManager = new EventManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        configManager = new ConfigManager();
        moduleManager.init();
        eventManager.init();
        textManager.init(true);
        moduleManager.onLoad();
    }

    public static void unload() {
        OyVey.onUnload();
        eventManager = null;
        friendManager = null;
        configManager = null;
        commandManager = null;
        colorManager = null;
        fileManager = null;
        moduleManager = null;
        textManager = null;
    }

    public static void onUnload() {
        if (!unloaded) {
            eventManager.onUnload();
            moduleManager.onUnload();
            configManager.saveConfig(OyVey.configManager.config.replaceFirst("oyvey/", ""));
            moduleManager.onUnloadPost();
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("OyVey v0.0.3");
        OyVey.load();
    }
}

