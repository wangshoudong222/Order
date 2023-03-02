package com.yun.orderPad.util

import android.widget.Toast
import com.yun.orderPad.BaseContext

object ToastUtil {
    fun show(text: String?) {
        Toast.makeText(BaseContext.instance.getContext(),text,Toast.LENGTH_LONG).show()
    }
}