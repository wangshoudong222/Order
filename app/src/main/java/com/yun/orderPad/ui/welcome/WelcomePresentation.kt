package com.yun.orderPad.ui.welcome

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.yun.orderPad.R

class WelcomePresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p_welcome)
    }

}