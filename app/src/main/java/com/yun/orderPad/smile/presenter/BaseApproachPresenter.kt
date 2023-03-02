package com.yun.orderPad.smile.presenter

import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.alipay.zoloz.smile2pay.ZolozConfig
import com.alipay.zoloz.smile2pay.detect.DetectCallback
import com.alipay.zoloz.smile2pay.detect.FaceAttr
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.yun.orderPad.smile.SmileManager

abstract class BaseApproachPresenter : BaseScanFacePresenter() , IApproachScanFacePresenter {

    private val approachListener = object : DetectCallback(){

        override fun onTriggerEvent(
            eventCode: Int,
            trigger: Boolean,
            extInfo: MutableMap<String, Any>?
        ) {
            Log.d(TAG,"approachListener -> onTriggerEvent : $trigger")
            when {
                // 进场
                trigger -> {
                    onTrigger()
                }
                // 退场
                else -> {
                }
            }
        }

        override fun onFaceVerify(smile2PayResponse: Smile2PayResponse?) {
            Log.d(TAG,"approachListener -> onFaceVerify")
            parseSmile2PayResponse(smile2PayResponse)
        }

        override fun onFaceTrack(
            faceAttrs: MutableList<FaceAttr>?,
            extInfo: MutableMap<String, Any>?
        ) {
            Log.d(TAG,"approachListener -> onFaceTrack")
        }

        override fun onFaceDetect(
            faceDetected: Boolean,
            newFace: Boolean,
            extInfo: MutableMap<String, Any>?
        ) {
            Log.w(TAG, "approachListener -> onFaceDetect : $faceDetected | $newFace")
        }

        override fun onError(p0: String?, p1: String?) {
            Log.w(TAG, "approachListener -> onError : $p0 | $p1")
        }

    }

    abstract fun onTrigger()

    override fun scanFace(listener: SmileManager.OnScanFaceResultListener?): Boolean {
        return if(super.scanFace(listener)){
            // 开启进场检测
            Log.d(TAG,"scanFace : approach")
            approach(approachParams())
            true

        } else false
    }

    override fun approach(params: HashMap<String, Any>) {
        zoloz.detect(params, approachListener)
    }

    private  fun approachParams(): HashMap<String, Any> {

        val configInfo = JSONObject()
        // 必填项，模式
        configInfo[ZolozConfig.KEY_MODE_FACE_MODE] = ZolozConfig.FaceMode.ENTRANCE
        configInfo[ZolozConfig.KEY_TASK_FLOW_TOYGER_POWER_MODE] = ZolozConfig.PowerMode.POWER_MODE_LOW

        val zolozConfig: HashMap<String, Any> = HashMap(3)
        zolozConfig[ZolozConfig.KEY_ZOLOZ_CONFIG] = configInfo.toJSONString()

        // 必填项
        zolozConfig[ZolozConfig.LADYBIRD_ONLY_DETECT_ENTRY] = true
        zolozConfig[ZolozConfig.KEY_LADYBIRD_ENABLE] = true
        // Smile透传键盘事件
        zolozConfig["keyboardEventEnabled"] = keyboardEventEnabled

        return zolozConfig
    }



}