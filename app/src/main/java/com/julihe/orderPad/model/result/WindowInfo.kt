package com.julihe.orderPad.model.result

import com.contrarywind.interfaces.IPickerViewData

data class WindowInfo(

    val windowId: String,

    /**
     * 窗口名称
     */
    val windowName: String
): IPickerViewData {
    override fun getPickerViewText(): String {
        return windowName
    }
}

