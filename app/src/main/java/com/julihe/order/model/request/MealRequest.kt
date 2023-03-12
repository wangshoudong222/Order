package com.julihe.order.model.request

data class MealRequest (
    /**
     * 餐次编码
     */
    val mealTableCode: String?,

    /**
     * 学生id
     */
    val studentId: String?
)