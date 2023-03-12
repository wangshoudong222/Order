package com.julihe.order.ui.welcome

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.widget.TextView
import com.julihe.order.BuildConfig
import com.julihe.order.R

class WelcomePresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p_welcome)
        val version: TextView = findViewById(R.id.version)
        version.text = BuildConfig.VERSION_NAME
    }

}