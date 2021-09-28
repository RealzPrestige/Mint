package mint

import net.minecraft.client.Minecraft
import mint.Mint
import mint.commands.CommandManager
import mint.managers.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.lwjgl.opengl.Display

@Mod(modid = "mint", name = "Mint", version = "0.1.1")
class Mint {
    @JvmField
    var mc = Minecraft.getMinecraft()

    companion object {
        @JvmField
        var INSTANCE = Mint()
        @JvmField
        var commandManager: CommandManager? = null
        @JvmField
        var friendManager: FriendManager? = null
        @JvmField
        var moduleManager: ModuleManager? = null
        var colorManager: ColorManager? = null
        var fileManager: FileManager? = null
        @JvmField
        var configManager: ConfigManager? = null
        var eventManager: EventManager? = null
        @JvmField
        var textManager: TextManager? = null
        private var unloaded = false
        fun load() {
            unloaded = false
            textManager = TextManager()
            commandManager = CommandManager()
            friendManager = FriendManager()
            moduleManager = ModuleManager()
            eventManager = EventManager()
            fileManager = FileManager()
            colorManager = ColorManager()
            configManager = ConfigManager()
            moduleManager!!.init()
            eventManager!!.init()
            textManager!!.init()
            configManager!!.init()
            moduleManager!!.onLoad()
        }

        @JvmStatic
        fun onUnload() {
            if (!unloaded) {
                eventManager!!.onUnload()
                moduleManager!!.onUnload()
                configManager!!.saveConfig(configManager!!.config.replaceFirst("mint/".toRegex(), ""))
                moduleManager!!.onUnloadPost()
                unloaded = true
            }
        }

        init {
            unloaded = false
        }
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent?) {
        Display.setTitle("Mint 0.1.1")
        load()
    }
}