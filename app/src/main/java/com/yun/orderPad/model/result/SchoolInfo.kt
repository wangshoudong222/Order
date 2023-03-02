package com.yun.orderPad.model.result

import com.contrarywind.interfaces.IPickerViewData

data class SchoolInfo(

    val schoolId: String,

    /**
     * 学校名称
     */
    val schoolName: String
): IPickerViewData {
    override fun getPickerViewText(): String {
        return schoolName
    }
}

