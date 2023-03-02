package com.yun.orderPad.ui.bind

import android.view.View
import android.widget.Button
import com.yun.orderPad.R
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.view.BaseDialog
import com.yun.orderPad.view.HalfDialogHandler

class UnBindPop(var viewModel: BindViewModel) : BaseDialog() {

    private var cancel: Button? = null
    private var confirm:Button? = null

    private var myHandler: HalfDialogHandler? = null

    override fun getLayoutRes(): Int {
        return R.layout.layout_unbind_pop
    }

    override fun bindView(contentView: View?) {

        cancel = contentView?.findViewById(R.id.btn_cancel)
        confirm = contentView?.findViewById(R.id.btn_confirm)

        cancel?.setOnClickListener {
            ToastUtil.show("取消解绑操作")
            dismiss()
        }

        confirm?.setOnClickListener {
            viewModel.unBind()
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.unBindState.observe(this) {
            if (it == true) {
                ToastUtil.show("解绑成功")
                dismiss()
            } else {
                ToastUtil.show("解绑失败，请稍后重试")
            }
        }
    }

    override fun getHandler(): SimpleHandler {
        @Synchronized
        if (myHandler == null) {
            myHandler = HalfDialogHandler()
        }
        return myHandler as HalfDialogHandler
    }

    companion object {
        const val TAG = "SettingPop"
    }
}