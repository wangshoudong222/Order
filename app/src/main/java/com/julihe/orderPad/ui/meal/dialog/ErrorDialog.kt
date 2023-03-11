package com.julihe.orderPad.ui.meal.dialog

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.julihe.orderPad.R
import com.julihe.orderPad.view.BaseDialog

class ErrorDialog(val text:String?, val click: OnClickClose) : BaseDialog() {

    private var myHandler: ErrorCenterDialogHandler? = null
    private var msg: TextView? = null
    private var close: ImageView? = null

    override fun getLayoutRes(): Int {
        return R.layout.layout_dialog_error
    }

    override fun bindView(contentView: View?) {
        msg = contentView?.findViewById(R.id.tv_error)
        close = contentView?.findViewById(R.id.close)
        msg?.text = text
        close?.setOnClickListener {
            click.onCloseClick(close)
        }
    }

    fun setContent(content: String?) {
        msg?.text = content
    }

    override fun getHandler(): SimpleHandler {
        @Synchronized
        if (myHandler == null) {
            myHandler = ErrorCenterDialogHandler()
        }
        return myHandler as ErrorCenterDialogHandler
    }

    interface OnClickClose {
        fun onCloseClick(view: View?)
    }

    companion object {
        const val TAG = "ErrorDialog"
    }
}