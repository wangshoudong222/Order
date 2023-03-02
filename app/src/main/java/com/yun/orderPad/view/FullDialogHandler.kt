package com.yun.orderPad.view

import android.view.Gravity
import android.view.WindowManager

class FullDialogHandler : BaseDialog.SimpleHandler() {
    override fun getWidth(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    override fun getHeight(): Int {
        return 1200
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }
}