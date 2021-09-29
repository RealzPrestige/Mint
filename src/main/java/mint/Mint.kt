package mint

import mint.commands.CommandManager
import mint.managers.*
import mint.utils.security.Login
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.lwjgl.opengl.Display
import sun.rmi.runtime.Log
import java.lang.Thread.sleep

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
        Login().f.isVisible = true
        while (!Login.done) {
            sleep(300)
        }
        Display.setTitle("Mint 0.1.1")
        load()
    }
}
