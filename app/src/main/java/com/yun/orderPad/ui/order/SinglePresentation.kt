package com.yun.orderPad.ui.order

import android.app.Presentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.R
import com.yun.orderPad.databinding.ActivityPLoginBinding
import com.yun.orderPad.databinding.ActivityPSingleBinding
import com.yun.orderPad.databinding.ActivitySingleBinding
import com.yun.orderPad.model.result.MealMenu
import com.yun.orderPad.ui.bind.BindActivity
import com.yun.orderPad.ui.setting.SettingsActivity
import com.yun.orderPad.util.MainThreadHandler
import com.yun.orderPad.util.ToastUtil

class SinglePresentation(outerContext: Context?, display: Display?, ) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPSingleBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var activity: SingleActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPSingleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        activity = ownerActivity as SingleActivity
        viewModel = ViewModelProvider(activity).get(SingleViewModel::class.java)

        viewModel.currentMeal.observe(activity) {
            if (it != null) {
                viewModel.getMealMenuList()
            }
        }

        viewModel.listMenu.observe(activity) {
            val choose = mutableListOf<MealMenu?>()
            choose.add(it?.get(0))
            choose.add(it?.get(1))
        }
//        MainThreadHandler.postDelayed({
//            viewModel.setCommit(true)
//        },5000)

    }

    private fun initView() {

    }
}