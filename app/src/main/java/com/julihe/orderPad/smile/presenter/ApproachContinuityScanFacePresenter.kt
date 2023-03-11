package com.julihe.orderPad.smile.presenter

import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.alipay.zoloz.smile2pay.Zoloz
import com.alipay.zoloz.smile2pay.ZolozConfig
import com.alipay.zoloz.smile2pay.ZolozConfig.FaceMode
import com.alipay.zoloz.smile2pay.ZolozConfig.PowerMode
import com.alipay.zoloz.smile2pay.detect.DetectCallback
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean


// 进场检测 - 连续扫脸
class ApproachContinuityScanFacePresenter(override val zoloz: Zoloz) : BaseApproachPresenter() {

    private var continueOutTimeJob : Job? = null
    private var continueScanCount = 0

    val isExit = AtomicBoolean(false)

    override fun onTrigger() {
        continueScanCount = 0
        zoloz.detect(scanParams(),object : DetectCallback(){

            override fun onFaceVerify(smile2PayResponse: Smile2PayResponse?) {
                parseSmile2PayResponse(smile2PayResponse)
                continueScanCount ++

                continueOutTimeJob?.cancel()
                continueOutTimeJob = null
                Log.d(TAG,"onTrigger -> onFaceVerify : $continueScanCount | cancel job")

                // 等待三秒 继续刷脸命令
                Thread.sleep(2000)
                if(!isExit.getAndSet(false)){
                    continueScan()
                    Log.i(TAG,"sleep 2s and [continue] smile")
                    continueOutTimeJob = createJob(1)
                }

            }

            override fun onError(p0: String?, p1: String?) {
                Log.e(TAG,"onFaceVerify.onError : p0 = $p0 | p1 = $p1")
                if(continueOutTimeJob == null){
                    // 识别出错，则执行无人脸倒计时10S退出
                    continueOutTimeJob = createJob(2)
                }
            }

        })
       continueOutTimeJob = createJob(3)
    }

    private fun createJob(type : Int) = CoroutineScope(Dispatchers.IO).launch {
        Log.d(TAG,"onTrigger -> createJob : $type")
        // 延时10秒，未识别到人脸退出刷脸，重启进场检测
        delay(10 * 1000)
        isExit.getAndSet(true)
        // 退出刷脸
        exitScan()
        Log.i(TAG,"delay 10s and [exit] smile")
        // 重启进场检测
        //zoloz.detect(null,null,null)
        delay(2 * 1000)
        scanFace(null)
        Log.i(TAG,"delay 2s and release [detect] smile")

    }

    override fun scanParams(): HashMap<String, Any> {
        val zolozConfig = super.scanParams()

        val configInfo = JSONObject()
        // 必填项，模式
        configInfo[ZolozConfig.KEY_MODE_FACE_MODE] = FaceMode.FACEPAY
        configInfo[ZolozConfig.KEY_TASK_FLOW_TOYGER_POWER_MODE] = PowerMode.POWER_MODE_LOW
        // 是否连续识别，设置为false时，需要主动调用commnd接口继续刷脸
        // 与KEY_UI_COUNT_DOWN_TIME配合使用
        // false：KEY_UI_COUNT_DOWN_TIME设置成-1，调用方显示结果页，并控制刷脸是否继续
        // true：KEY_UI_COUNT_DOWN_TIME设置成大于0，smile自己显示结果页，展示完自动继续刷脸
        configInfo[ZolozConfig.KEY_TASK_FLOW_NEED_AUTO_RECOGNIZE] = true

        zolozConfig[ZolozConfig.KEY_ZOLOZ_CONFIG] = configInfo.toJSONString()

        // 设置UI config
        val uiInfo = JSONObject()
        uiInfo[ZolozConfig.KEY_UI_COUNT_DOWN_TIME] = 2  //<--确认页面是否显示，可选
        uiInfo[ZolozConfig.KEY_UI_CONFIRM_TEXT] = "确认支付"  //<--确认支付按钮
        uiInfo[ZolozConfig.KEY_UI_PAY_AMOUNT] = "0.01"  //<--确认支付按钮

        zolozConfig[ZolozConfig.KEY_UI_CONFIG] = uiInfo.toJSONString()

        return zolozConfig

    }


}