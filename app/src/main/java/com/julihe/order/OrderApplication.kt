package com.julihe.order

import android.app.Application
import android.util.Log
import com.alipay.iot.sdk.APIManager
import com.julihe.order.util.CommonUtils
import com.julihe.order.util.LogUtil
import com.julihe.order.util.sp.SpUtil

class OrderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BaseContext.instance.init(this)
        initSn()
        initIot()
    }

    private fun initSn() {
        val sn = CommonUtils.getProperties("ro.serialno", "")
        SpUtil.deviceId(sn)

    }

    private fun initIot() {
        APIManager.getInstance().initialize(this,"2088541567616841") { p0 ->
            if (p0) {
                LogUtil.d(TAG, "IOT 初始化成功")
            } else {
                LogUtil.d(TAG, "IOT 初始化失败失败")
            }
        }
    }

    companion object {
        const val TAG = "OrderApplication"
    }

}
