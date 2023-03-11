package com.julihe.orderPad.model.request
import com.beust.klaxon.*


private val klaxon = Klaxon()

data class SchoolRequest (
    /**
     * 学校id，number为Long类型
     */
    val orderMode: Double,

    /**
     * 学校名称
     */
    val schoolName: String
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<SchoolRequest>(json)
    }
}