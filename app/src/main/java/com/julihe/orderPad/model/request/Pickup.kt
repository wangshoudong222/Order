package com.julihe.orderPad.model.request
import com.beust.klaxon.*

private val klaxon = Klaxon()

/**
 * 入参，点餐模式或者学校餐厅窗口信息两者必传其一
 */
data class Pickup (
    /**
     * 订单集合
     */
    val orderNoList: List<String?>?,

    /**
     * 学生id
     */
    val studentId: String?
)