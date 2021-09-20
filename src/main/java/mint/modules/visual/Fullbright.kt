package mint.modules.visual

import mint.clickgui.setting.KotlinSetting
import mint.clickgui.setting.Setting
import mint.modules.Module

/**
 * @author kambing
 * @since 20/9/21
 */

object Fullbright: Module("Fullbright", Category.VISUAL,"First Kotlin Module") {
    val test by KotlinSetting((Setting<Boolean>("Test", true)))
    //so this works but it doesnt register it to clickgui

    override fun onEnable() {
        if (test)
        mc.gameSettings.gammaSetting = 100f
        else
        mc.gameSettings.gammaSetting = 1f
    }

    override fun onDisable() {
        mc.gameSettings.gammaSetting = 1f
    }
}
