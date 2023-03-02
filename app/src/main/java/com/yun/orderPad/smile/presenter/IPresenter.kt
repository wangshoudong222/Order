package com.yun.orderPad.smile.presenter

/**
 * 扫脸
 */
interface IScanFacePresenter {
    fun scanFace(listener: com.yun.orderPad.smile.SmileManager.OnScanFaceResultListener?): Boolean

    fun continueScan()

    fun exitScan()

    fun destroy()

}

/**
 * 进场检测
 */
interface IApproachScanFacePresenter {
    fun approach(params: HashMap<String, Any>)
}

/**
 * 进场检测-连续扫脸
 */
interface IContinuityApproachScanFacePresenter {
    fun continuityScan(params: HashMap<String, Any>)
}