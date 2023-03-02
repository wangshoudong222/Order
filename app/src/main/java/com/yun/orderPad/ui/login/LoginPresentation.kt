package com.yun.orderPad.ui.login

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.yun.orderPad.R

class LoginPresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p_login)
    }

}