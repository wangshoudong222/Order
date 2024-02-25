package com.julihe.order.ui.simple

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.julihe.order.R
import com.julihe.order.view.BaseDialog
import com.julihe.order.view.FullDialogCancelHandler
import com.julihe.order.view.FullDialogHandler
import com.julihe.order.view.HalfCenterDialogHandler
import kotlinx.coroutines.flow.asFlow
import org.w3c.dom.Text

class LimitPop(val msg: String) : BaseDialog() {

    private var myHandler: FullDialogCancelHandler? = null
    private var msgTv: TextView? = null
    private var btnCancel: Button? = null
    private var btnCommit: Button? = null
    lateinit var mListener: (string: String) -> Unit

    override fun getLayoutRes(): Int {
        return R.layout.layout_dialog_limit
    }

    override fun bindView(contentView: View?) {
        msgTv = contentView?.findViewById(R.id.tv_limit)
        btnCancel = contentView?.findViewById(R.id.btn_cancel)
        btnCommit = contentView?.findViewById(R.id.btn_commit)
        btnCancel?.setOnClickListener {
            mListener.invoke(CANCEL)
        }
        btnCommit?.setOnClickListener {
            mListener.invoke(COMMIT)
        }
        msgTv?.text = msg
    }

    override fun getHandler(): SimpleHandler {
        @Synchronized
        if (myHandler == null) {
            myHandler = FullDialogCancelHandler()

        }
        return myHandler as FullDialogCancelHandler
    }


    fun setListener(listener: (string: String) -> Unit) {
        this.mListener = listener
    }

    companion object {
        const val TAG = "LimitPop"
        const val CANCEL = "CANCEL"
        const val COMMIT = "COMMIT"
    }
}