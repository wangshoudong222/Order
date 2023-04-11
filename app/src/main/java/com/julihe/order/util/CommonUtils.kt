package com.julihe.order.util

import android.content.Context
import android.view.KeyEvent
import java.lang.reflect.InvocationTargetException
import java.text.ParseException
import java.text.SimpleDateFormat

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


    /**
     * @param timeMillis 时间戳字符串
     * @return yyyy-MM-dd HH:mm:ss
     */
    fun formatToDate(timeMillis: Long): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return simpleDateFormat.format(timeMillis)
    }

    /**
     * @param timestamp 时间戳字符串
     * @return yyyy-MM-dd HH:mm:ss
     */
    fun formatToDateTime(timestamp: String?): Long {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            return simpleDateFormat.parse(timestamp).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getKeyIntercept(code: Int): Boolean {
        return KEY_LIST.contains(code)
    }

    private val KEY_LIST = mutableListOf( KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_ENTER,
        KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_ESCAPE,
        KeyEvent.KEYCODE_NUMPAD_DOT, KeyEvent.KEYCODE_NUMPAD_ADD,
        KeyEvent.KEYCODE_NUMPAD_1, KeyEvent.KEYCODE_NUMPAD_2,
        KeyEvent.KEYCODE_NUMPAD_3, KeyEvent.KEYCODE_NUMPAD_4,
        KeyEvent.KEYCODE_NUMPAD_5, KeyEvent.KEYCODE_NUMPAD_6,
        KeyEvent.KEYCODE_NUMPAD_7, KeyEvent.KEYCODE_NUMPAD_8,
        KeyEvent.KEYCODE_NUMPAD_9, KeyEvent.KEYCODE_NUMPAD_0)
}