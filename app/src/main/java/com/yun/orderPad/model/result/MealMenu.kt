package com.yun.orderPad.model.result

data class MealMenu(
    /**
     * 餐点url
     */
    val dishPicUrl: String,

    /**
     * 餐点id
     */
    val dishSkuId: String,

    /**
     * 餐点名称
     */
    val dishSkuName: String,

    /**
     * 是否清真
     */
    val isHalal: Boolean,

    /**
     * 餐次名称
     */
    val mealTableName: String,

    /**
     * 价格，BigDecimal
     */
    val price: Double
)