package com.yun.orderPad

import android.app.ActivityManager
import android.app.Application
import android.os.Process

class OrderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BaseContext.instance.init(this)
    }

    /**
     * 获取当前进程名
     */
    private fun getCurrentProcessName(): String? {
        val pid = Process.myPid()
        var processName = ""
        val manager = applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                processName = process.processName
            }
        }
        return processName
    }

    companion object {

    }

}
