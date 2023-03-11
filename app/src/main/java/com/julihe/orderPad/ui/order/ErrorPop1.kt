package com.julihe.orderPad.ui.order

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.julihe.orderPad.R
import com.julihe.orderPad.util.ToastUtil
import com.julihe.orderPad.view.BaseDialog
import com.julihe.orderPad.view.HalfCenterDialogHandler
import com.julihe.orderPad.view.HalfDialogHandler

class ErrorPop1: PopupWindow {

    var text:String? = null
    var activity:Activity? = null

    constructor(text:String?,activity: Activity) {
        this.text = text
        this.activity = activity
        initPop()
    }

    private fun initPop() {
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_error_pop,null)
        val msg = view?.findViewById<TextView>(R.id.msg)
        msg?.text = text
    }

}