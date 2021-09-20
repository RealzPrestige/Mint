package mint.modules.visual

import mint.modules.Module

/**
 * @author kambing
 * @since 20/9/21
 */

object Fullbright: Module("Fullbright", Category.VISUAL,"First Kotlin Module") {
    override fun onEnable() {
        mc.gameSettings.gammaSetting = 100f
    }

    override fun onDisable() {
        mc.gameSettings.gammaSetting = 1f
    }
}
