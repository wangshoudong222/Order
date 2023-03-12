package com.julihe.order.view

import android.view.Gravity
import android.view.WindowManager

class HalfDialogHandler : BaseDialog.SimpleHandler() {
    override fun getWidth(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    override fun getHeight(): Int {
        return 800
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }
}