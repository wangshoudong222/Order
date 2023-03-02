package com.yun.orderPad.util

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
}