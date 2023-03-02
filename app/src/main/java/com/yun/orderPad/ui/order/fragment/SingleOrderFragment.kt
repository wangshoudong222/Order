package com.yun.orderPad.ui.order.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.R
import com.yun.orderPad.databinding.ActivitySingleBinding
import com.yun.orderPad.databinding.FragmentSinglOrderBinding
import com.yun.orderPad.ui.bind.BindActivity
import com.yun.orderPad.ui.order.SingleOrderActivity
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.ui.setting.SettingsActivity
import com.yun.orderPad.ui.test.ui.main.TestFragment
import com.yun.orderPad.util.ToastUtil

class SingleOrderFragment : Fragment() {

    private lateinit var binding : FragmentSinglOrderBinding
    private lateinit var viewModel: SingleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSinglOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SingleViewModel::class.java)
        viewModel.getCurrentMeal()
        viewModel.getConfig()

        viewModel.currentMeal.observe(this) {
            binding.meal.text = it?.mealTableName
            binding.time.text = it?.mealStartTime + "~" + it?.mealEndTime
        }
        viewModel.config.observe(this) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.setCanteen.text = it.kitchenName
                binding.setWindow1.text = it.windowName
            }
        }

        viewModel.commit.observe(this) {

        }
    }

    private fun initView() {

    }

    companion object {
        fun newInstance() = SingleOrderFragment()
    }

}