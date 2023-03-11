package com.julihe.orderPad

import android.app.ActivityManager
import android.app.Application
import android.os.Process
import com.julihe.orderPad.util.CommonUtils
import com.julihe.orderPad.util.sp.SpUtil
import java.lang.reflect.InvocationTargetException

class OrderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BaseContext.instance.init(this)
        initSn()
    }

    private fun initSn() {
        val sn = CommonUtils.getProperties("ro.serialno", "")
        SpUtil.deviceId(sn)
    }

    companion object {

    }

}
