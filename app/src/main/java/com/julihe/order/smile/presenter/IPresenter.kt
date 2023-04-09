package com.julihe.order.smile.presenter

import com.julihe.order.smile.SmileManager

/**
 * 扫脸
 */
interface IScanFacePresenter {
    fun scanFace(listener: SmileManager.OnScanFaceResultListener?, num: String): Boolean

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