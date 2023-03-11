package com.julihe.orderPad.model.request

/**
 * 入参，点餐模式或者学校餐厅窗口信息两者必传其一
 */
data class ConfigInfo (
    /**
     * 点餐模式
     */
    val orderMode: String?,

    /**
     * 点餐模式名称
     */
    val orderModeName: String?,
    /**
     * 学校id
     */
    val schoolId: String?,
    /**
     * 学校名称
     */
    val schoolName: String?,

    val kitchenId: String?,

    /**
     * 食堂名称
     */
    val kitchenName: String?,

    /**
     * 窗口id，同上
     */
    val windowId: String?,

    /**
     * 窗口名称
     */
    val windowName: String?
)