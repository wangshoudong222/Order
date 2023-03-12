package com.julihe.order

import android.app.Application
import com.julihe.order.util.CommonUtils
import com.julihe.order.util.sp.SpUtil

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
