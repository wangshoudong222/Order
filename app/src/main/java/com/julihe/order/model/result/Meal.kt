package com.julihe.order.model.result
data class Meal (
    /**
     * 餐次结束时间
     */
    val mealEndTime: String,

    /**
     * 餐次开始时间
     */
    val mealStartTime: String,

    /**
     * 餐次名称
     */
    val mealTableName: String,

    /**
     * 餐次编码
     */
    val mealTableCode: String
)