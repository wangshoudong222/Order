package com.julihe.order.model.result

import com.contrarywind.interfaces.IPickerViewData

data class KitchenInfo(
    /**
     * 食堂id，number为Long类型
     */
    val kitchenId: String,

    /**
     * 食堂名称
     */
    val kitchenName: String
): IPickerViewData {
    override fun getPickerViewText(): String {
        return kitchenName
    }
}
