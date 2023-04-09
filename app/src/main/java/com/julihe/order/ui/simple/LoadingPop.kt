package com.julihe.order.ui.simple

import android.view.View
import android.widget.TextView
import com.julihe.order.R
import com.julihe.order.view.BaseDialog
import com.julihe.order.view.HalfCenterDialogHandler

class LoadingPop : BaseDialog() {

    private var myHandler: HalfCenterDialogHandler? = null

    override fun getLayoutRes(): Int {
        return R.layout.layout_loading
    }

    override fun bindView(contentView: View?) {
    }

    override fun getHandler(): SimpleHandler {
        @Synchronized
        if (myHandler == null) {
            myHandler = HalfCenterDialogHandler()
        }
        return myHandler as HalfCenterDialogHandler
    }

    companion object {
        const val TAG = "ErrorPop"
    }
}