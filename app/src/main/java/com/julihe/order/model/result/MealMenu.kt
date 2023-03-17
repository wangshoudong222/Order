package com.julihe.order.model.result

import java.math.BigDecimal

data class MealMenu(
    /**
     * 餐点url
     */
    val dishPicUrl: String?,

    /**
     * 餐点id
     */
    val dishSkuId: String?,
    /**
     * 快捷点餐CODE
     */
    val dishCode: String?,

    /**
     * 餐点名称
     */
    val dishSkuName: String?,

    /**
     * 是否清真
     */
    val isHalal: Boolean?,

    /**
     * 餐次名称
     */
    val mealTableName: String?,

    /**
     * 价格，BigDecimal
     */
    var price: BigDecimal?,

    var checked: Boolean?,

    var fouces: Boolean?,

    var quantity: Long? = 0
)