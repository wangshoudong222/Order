package com.julihe.order.smile.presenter

import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alipay.zoloz.smile2pay.Zoloz
import com.alipay.zoloz.smile2pay.ZolozConfig
import com.julihe.order.smile.SmileManager

/**
 * 普通刷脸
 */
class NormalScanFacePresenter(override val zoloz: Zoloz) : BaseScanFacePresenter() {

    override fun scanFace(
        listener: SmileManager.OnScanFaceResultListener?
    ): Boolean {
        return if(super.scanFace(listener)){
            zoloz.verify(scanParams(),this)
            true
        } else false
    }



    override fun scanParams(): HashMap<String, Any> {
        val zolozConfig = super.scanParams()

        zolozConfig[ZolozConfig.KEY_CAPTURE_UI_MODE] = ZolozConfig.CaptureUIMode.CLICK

        // Config
        val configInfo = JSONObject()
        // 必填项，模式
        configInfo[ZolozConfig.KEY_MODE_FACE_MODE] = ZolozConfig.FaceMode.FACEPAY
        // 必填项，取值：0～1000mm，建议值：750mm
        configInfo[ZolozConfig.KEY_ALGORITHM_MAX_DETECT_DISTANCE] = 750
        
        // UI
        val uiJson = JSONObject()
        uiJson[ZolozConfig.KEY_UI_PAY_AMOUNT] = "0.02"

        zolozConfig[ZolozConfig.KEY_ZOLOZ_CONFIG] = configInfo.toJSONString()
        zolozConfig[ZolozConfig.KEY_UI_CONFIG] = uiJson.toJSONString()

        Log.i(TAG,"scanParams : ${JSON.toJSONString(zolozConfig)}")
        return zolozConfig
    }


}