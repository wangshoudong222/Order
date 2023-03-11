package com.julihe.orderPad.model.request

import com.julihe.orderPad.model.result.MealMenu

/**
 * 入参，点餐模式或者学校餐厅窗口信息两者必传其一
 */
data class MealOrder (
    /**
     * 零点明细
     */
    val mealOrderDetails: List<MealMenu>?,

    /**
     * 餐次编码
     */
    val mealTableCode: String?,

    /**
     * 餐次名称
     */
    val mealTableName: String?,

    /**
     * 学生id
     */
    val studentId: String?
)
