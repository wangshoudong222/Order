package com.julihe.orderPad.ui.meal.dialog

import android.app.ActionBar.LayoutParams
import android.view.Gravity
import com.julihe.orderPad.view.BaseDialog

class SuccessCenterDialogHandler : BaseDialog.SimpleHandler() {
    override fun getWidth(): Int {
        return LayoutParams.MATCH_PARENT
    }

    override fun getHeight(): Int {
        return 1200
    }

    override fun getGravity(): Int {
        return Gravity.BOTTOM
    }

    override fun isCancelable(): Boolean {
        return true
    }

    override fun isOutCancelable(): Boolean {
        return false
    }
}