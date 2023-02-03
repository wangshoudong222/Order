package com.yun.orderPad

import android.app.Application

class OrderApplication : Application() {

    var application: Application? = null

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}
