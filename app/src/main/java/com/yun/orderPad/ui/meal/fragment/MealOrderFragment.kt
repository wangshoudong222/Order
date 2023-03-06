package com.yun.orderPad.ui.meal.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.R
import com.yun.orderPad.databinding.FragmentMealOrderBinding
import com.yun.orderPad.databinding.FragmentSinglOrderBinding
import com.yun.orderPad.ui.meal.SetMealViewModel
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.ui.order.fragment.SingleOrderFragment

class MealOrderFragment : Fragment() {

    private lateinit var binding : FragmentMealOrderBinding
    private lateinit var viewModel: SetMealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMealOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SetMealViewModel::class.java)

        viewModel.currentMeal.observe(activity!!) {
            binding.meal.text = it?.mealTableName
            binding.time.text = it?.mealStartTime + "~" + it?.mealEndTime
        }

        viewModel.config.observe(activity!!) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.setCanteen.text = it.kitchenName
                binding.setWindow1.text = it.windowName
            }
        }
    }

    private fun initView() {
        binding.btnScan.setOnClickListener {
            viewModel.doScan(true)
        }
    }

    companion object {
        fun newInstance() = MealOrderFragment()
    }

}