package com.julihe.order.util

import android.widget.Toast
import com.julihe.order.BaseContext

object ToastUtil {
    fun show(text: String?) {
        Toast.makeText(BaseContext.instance.getContext(),text,Toast.LENGTH_LONG).show()
    }
}