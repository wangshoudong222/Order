package com.julihe.order.model.result

import com.contrarywind.interfaces.IPickerViewData
import java.math.BigDecimal

data class SchoolInfo(

    val schoolId: String,

    /**
     * 学校名称
     */
    val schoolName: String,

    /**
     * 学校限额
     */
    val limitAmount: BigDecimal
): IPickerViewData {
    override fun getPickerViewText(): String {
        return schoolName
    }
}

