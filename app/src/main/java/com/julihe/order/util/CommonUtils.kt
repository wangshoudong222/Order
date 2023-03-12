package com.julihe.order.util

import android.content.Context
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
}