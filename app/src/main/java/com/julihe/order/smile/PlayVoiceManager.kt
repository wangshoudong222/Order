package com.julihe.order.smile

import com.alipay.iot.sdk.APIManager

object PlayVoiceManager {

    // 支付成功
    const val VOICE_SUCCESS = "f22"
    const val VOICE_ERROR = "f23"
    const val VOICE_SCAN_FACE = "start_scan_face"

    fun playVoice(type: String) {
        APIManager.getInstance().voiceAPI.play(type)
    }
}