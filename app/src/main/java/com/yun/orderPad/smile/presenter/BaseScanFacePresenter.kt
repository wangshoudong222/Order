package com.yun.orderPad.smile.presenter

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.CallSuper
import com.alibaba.fastjson.JSON
import com.alipay.zoloz.smile2pay.Zoloz
import com.alipay.zoloz.smile2pay.ZolozConfig
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.alipay.zoloz.smile2pay.verify.VerifyCallback
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.HashMap

abstract class BaseScanFacePresenter : VerifyCallback() , IScanFacePresenter {
    protected val TAG = this.javaClass.name

    private lateinit var mScanListener : com.yun.orderPad.smile.SmileManager.OnScanFaceResultListener
    private var mIsStartScan = AtomicBoolean(false)
    protected var keyboardEventEnabled = false


    abstract val zoloz : Zoloz

    @CallSuper
    open fun scanParams() : HashMap<String,Any> {
        val zolozConfig = java.util.HashMap<String, Any>()
        // Smile透传键盘事件
        zolozConfig["keyboardEventEnabled"] = keyboardEventEnabled
        return zolozConfig
    }

    @CallSuper
    override fun scanFace(listener: com.yun.orderPad.smile.SmileManager.OnScanFaceResultListener?) : Boolean {
        if(mIsStartScan.compareAndSet(true,true)){
            Log.w(TAG,"already start scan , return")
            return false
        }
        if(listener != null){
            mScanListener = listener
        }
        return true
    }



    override fun onResponse(p0: Smile2PayResponse?) {
        parseSmile2PayResponse(p0)
    }

    protected fun parseSmile2PayResponse(p0: Smile2PayResponse?){
        Log.i(TAG,"scan face onResponse : ${JSON.toJSONString(p0 ?: "Smile2PayResponse is null")}")
        p0?.apply {
            when(code){
                // 成功
                Smile2PayResponse.CODE_SUCCESS -> {
                    mScanListener.onVerifyResult(true,faceToken,alipayUid)
                }
                // 失败
                else -> {
                    when(subCode){
                        "Z6016" -> {
                            Log.w(TAG, "刷脸退出，退出原因：调用exit command ！")
                        }
                        "Z1064" -> {
                            Log.w(TAG, "刷脸退出，退出原因：刷脸程序被压后台了(Z1064),可能是刷脸页面刷卡导致！")
                        }
                        else -> mScanListener.onVerifyResult(false,subCode,subMsg)
                    }

                }
            }
            return
        }

        mScanListener.onVerifyResult(false,"Smile2PayResponse is null","Smile2PayResponse is null")
    }

    override fun continueScan() {
        Log.d(TAG,"continueScan")
        zoloz.command(mapOf(Pair(ZolozConfig.KEY_COMMAND_CODE,ZolozConfig.CommandCode.DETECT_CONTINUE)))
    }

    override fun exitScan() {
        Log.d(TAG,"exitScan")
        zoloz.command(mapOf(Pair(ZolozConfig.KEY_COMMAND_CODE, ZolozConfig.CommandCode.EXIT)))
        mIsStartScan.compareAndSet(true,false)
    }


    @SuppressLint("SimpleDateFormat")
    private fun formatTime(time : Long) : String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(Date(time))
    }

    override fun destroy() {
        Log.d(TAG,"destroy")
        exitScan()
    }

}