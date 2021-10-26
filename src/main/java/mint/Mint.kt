package mint

import mint.commands.CommandManager
import mint.managers.*
import mint.newgui.hud.HudComponentManager
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.Display

@Mod(modid = "mint", name = "Mint", version = "0.1.1")
class Mint {
    @JvmField
    var mc = Minecraft.getMinecraft()
    val LOGGER = LogManager.getLogger("Mint ExceptionCatcher: ")

    companion object {
        @kotlin.jvm.JvmField
        var eventManager: EventManager? = null

        @JvmField
        var INSTANCE = Mint()

        @JvmField
        var commandManager: CommandManager? = null

        @JvmField
        var friendManager: FriendManager? = null

        @JvmField
        var moduleManager: ModuleManager? = null

        @JvmField
        var configManager: ConfigManager? = null

        @JvmField
        var textManager: TextManager? = null

        @JvmField
        var serverManager: ServerManager? = null

        @JvmField
        var hudComponentManager: HudComponentManager? = null
        private var unloaded = false
        fun load() {
            unloaded = false
            textManager = TextManager()
            serverManager = ServerManager()
            commandManager = CommandManager()
            friendManager = FriendManager()
            moduleManager = ModuleManager()
            eventManager = EventManager()
            configManager = ConfigManager()
            hudComponentManager = HudComponentManager()
            moduleManager!!.init()
            eventManager!!.init()
            textManager!!.init()
            configManager!!.init()
            moduleManager!!.onLoad()
            hudComponentManager!!.load()
        }

        @JvmStatic
        fun onUnload() {
            if (!unloaded) {
                eventManager!!.onUnload()
                moduleManager!!.onUnload()
                configManager!!.saveConfig(configManager!!.config.replaceFirst("mint/".toRegex(), ""))
                moduleManager!!.onUnloadPost()
                hudComponentManager!!.unload()
                unloaded = true
            }
        }

        init {
            unloaded = false
        }
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent?) {
       // Login().frame.isVisible = true
       // while (!Login.done) {
       //     sleep(300)
       // }
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent?) {
        Display.setTitle("Mint 0.1.1")
        load()
    }
}
