package com.julihe.order.view

import android.view.WindowManager

class HalfCenterDialogHandler : BaseDialog.SimpleHandler() {
    override fun getWidth(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    override fun getHeight(): Int {
        return 800
    }

}