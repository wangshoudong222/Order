package com.julihe.order.model.result

import java.math.BigDecimal

data class OrderDetailInfo (
    /**
     * 餐点名称
     */
    val dishSkuName: String,

    /**
     * 清真标识
     */
    val isHalalFlag: Boolean,

    /**
     * 是否本窗口
     */
    val isSelfWindow: Boolean,

    /**
     * 是否待取
     */
    var isWaitingPickUp: Boolean,

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
    var stateName: String?,

    /**
     * 窗口名称
     */
    val windowName: String?,

    /**
     * 窗口名称
     */
    var confirmTime: String?
)