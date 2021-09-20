package mint.clickgui.setting

import kotlin.reflect.KProperty

/**
 * @author cookiedragon234 27/Mar/2020
 */

class KotlinSetting<F: Any, out T: Setting<F>>(val value: T) {
    fun getActualValue(): T = value

    operator fun getValue(thisRef: Any?, property: KProperty<*>): F {
        return value.value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newVal: F) {
        value.value = newVal
    }
}