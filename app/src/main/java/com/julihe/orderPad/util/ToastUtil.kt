package com.julihe.orderPad.util

import android.widget.Toast
import com.julihe.orderPad.BaseContext

object ToastUtil {
    fun show(text: String?) {
        Toast.makeText(BaseContext.instance.getContext(),text,Toast.LENGTH_LONG).show()
    }
}