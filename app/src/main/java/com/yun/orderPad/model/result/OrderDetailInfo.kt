package com.yun.orderPad.model.result

import java.math.BigDecimal

data class OrderDetailInfo (
    /**
     * 餐点名称
     */
    val dishSkuName: String,

    /**
     * 清真标识
     */
    val halalFlag: String,

    /**
     * 是否本窗口
     */
    val isSelfWindow: Boolean,

    /**
     * 是否待取
     */
    val isWaitingPickUp: Boolean,

    /**
     * 餐次名称
     */
    val mealTableName: String,

    /**
     * 价格，
     */
    val price: BigDecimal,

    /**
     * 数量
     */
    val quantity: Long,

    /**
     * 状态名称
     */
    val stateName: String,

    /**
     * 窗口名称
     */
    val windowName: String
)