package com.yun.orderPad.smile.presenter

import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.alipay.zoloz.smile2pay.Zoloz
import com.alipay.zoloz.smile2pay.ZolozConfig
import com.alipay.zoloz.smile2pay.ZolozConfig.FaceMode
import com.alipay.zoloz.smile2pay.ZolozConfig.PowerMode


/**
 * 进场检测 - 单次
 *
 */
class ApproachSingleScanFacePresenter(override val zoloz: Zoloz) : BaseApproachPresenter() {

    override fun onTrigger(){
        // 执行单次刷脸
        Log.i(TAG,"onTrigger : verify")
        zoloz.verify(scanParams(),this)
    }

    override fun scanParams(): HashMap<String, Any> {
        val zolozConfig = super.scanParams()

        val configInfo = JSONObject()
        // 必填项，模式
        configInfo.put(ZolozConfig.KEY_MODE_FACE_MODE, FaceMode.FACEPAY)
        configInfo.put(ZolozConfig.KEY_TASK_FLOW_TOYGER_POWER_MODE, PowerMode.POWER_MODE_LOW)

        zolozConfig[ZolozConfig.KEY_ZOLOZ_CONFIG] = configInfo.toJSONString()

        // 设置UI config
        val uiInfo = JSONObject()
        uiInfo[ZolozConfig.KEY_UI_COUNT_DOWN_TIME] = 3  //<--确认页面是否显示，可选

        zolozConfig[ZolozConfig.KEY_UI_CONFIG] = uiInfo.toJSONString()

        return zolozConfig
    }


}