package com.julihe.order.view

import android.view.Gravity
import android.view.WindowManager

class FullDialogCancelHandler : BaseDialog.SimpleHandler() {
    override fun getWidth(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    override fun getHeight(): Int {
        return 1200
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    override fun isCancelable(): Boolean {
        return false
    }

    override fun isOutCancelable(): Boolean {
        return false
    }
}