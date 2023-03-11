package com.julihe.orderPad.model.request
import com.beust.klaxon.*

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