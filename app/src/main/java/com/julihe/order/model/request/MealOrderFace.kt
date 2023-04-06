package com.julihe.order.model.request

import com.julihe.order.model.result.MealMenu

/**
 * 入参，点餐模式或者学校餐厅窗口信息两者必传其一
 */
data class MealOrderFace (
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
    val studentId: String?,
    /**
     * 刷脸token
     */
    val faceToken: String?,
    /**
     * 刷脸token加签
     */
    val faceTokenSign: String?,
    /**
     * 学校内标
     */
    val schoolCode: String?
)
