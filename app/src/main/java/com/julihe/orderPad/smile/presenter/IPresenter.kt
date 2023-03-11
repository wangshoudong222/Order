package com.julihe.orderPad.smile.presenter

import com.julihe.orderPad.smile.SmileManager

/**
 * 扫脸
 */
interface IScanFacePresenter {
    fun scanFace(listener: SmileManager.OnScanFaceResultListener?): Boolean

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