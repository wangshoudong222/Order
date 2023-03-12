package com.julihe.order.ui.meal.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import androidx.lifecycle.ViewModelProvider
import com.julihe.order.databinding.ActivityPMealWaitBinding
import com.julihe.order.ui.meal.SetMealActivity
import com.julihe.order.ui.order.SingleViewModel

class WaitPresentation(outerContext: Context?, display: Display?, ) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPMealWaitBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var activity: SetMealActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPMealWaitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
    }

    private fun initViewModel() {
        activity = ownerActivity as SetMealActivity
        viewModel = ViewModelProvider(activity).get(SingleViewModel::class.java)
        viewModel.config.observe(activity) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.pMealTitle.titleName.text = it.schoolName + " " + it.windowName
            }
        }
    }

    companion object {
        const val TAG = "WaitPresentation"
    }
}