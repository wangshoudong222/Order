package com.julihe.orderPad.ui.order

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.julihe.orderPad.R
import com.julihe.orderPad.util.ToastUtil
import com.julihe.orderPad.view.BaseDialog
import com.julihe.orderPad.view.HalfCenterDialogHandler
import com.julihe.orderPad.view.HalfDialogHandler

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