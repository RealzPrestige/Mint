package mint.modules.visual

import mint.clickgui.setting.Setting
import mint.modules.Module

/**
 * @author kambing
 * @since 20/9/21
 */

object Fullbright: Module("Fullbright", Category.VISUAL,"First Kotlin Module") {
    var setting: Setting<Float>
    init {
        setting = register(Setting("Setting", 4.0f,1.0f,100.0f)) as Setting<Float>
    }

    override fun onEnable() {
        mc.gameSettings.gammaSetting = setting.getValue()
    }

    override fun onDisable() {
        mc.gameSettings.gammaSetting = 1f
    }
}
