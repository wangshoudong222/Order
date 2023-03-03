package com.yun.orderPad.model.request

data class MealOrderDetail (
    /**
     * 餐点id
     */
    val dishSkuId: String?,
    /**
     * 餐点名称
     */
    val dishSkuName: String?,

    /**
     * 价格，BigDecimal
     */
    val price: Double?,

    /**
     * 数量
     */
    val quantity: Long?
)
