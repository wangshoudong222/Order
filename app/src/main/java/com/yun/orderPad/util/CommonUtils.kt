package com.yun.orderPad.util

import android.content.Context
import java.lang.reflect.InvocationTargetException

object CommonUtils {

    fun getProperties(name: String, def: String): String? {
        try {
            val SystemProperties = Class.forName("android.os.SystemProperties")
            val get = SystemProperties.getDeclaredMethod("get", String::class.java, String::class.java)
            return get.invoke(null, name, def) as String
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return def
    }

    fun dp2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
}