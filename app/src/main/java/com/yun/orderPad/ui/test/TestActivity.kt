package com.yun.orderPad.ui.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yun.orderPad.R
import com.yun.orderPad.ui.test.ui.main.TestFragment

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TestFragment.newInstance())
                .commitNow()
        }
    }
}