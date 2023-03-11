package com.julihe.orderPad.ui.setting

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.julihe.orderPad.R

class SettingsPresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p_settings)
    }

}