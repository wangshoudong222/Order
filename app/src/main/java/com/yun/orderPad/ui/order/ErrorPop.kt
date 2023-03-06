package com.yun.orderPad.ui.order

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.yun.orderPad.R
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.view.BaseDialog
import com.yun.orderPad.view.HalfCenterDialogHandler
import com.yun.orderPad.view.HalfDialogHandler

class ErrorPop(val text:String?) : BaseDialog() {

    private var myHandler: HalfCenterDialogHandler? = null

    override fun getLayoutRes(): Int {
        return R.layout.layout_error_pop
    }

    override fun bindView(contentView: View?) {
        val msg = contentView?.findViewById<TextView>(R.id.msg)
        msg?.text = text
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