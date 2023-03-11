package com.julihe.orderPad.ui.order.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.julihe.orderPad.databinding.FragmentSinglOrderBinding
import com.julihe.orderPad.ui.order.SingleViewModel
import com.julihe.orderPad.ui.order.presentation.SinglePresentation
import com.julihe.orderPad.util.LogUtil

/**
 * 等待点餐
 */
class SingleOrderFragment : Fragment() {

    private lateinit var binding : FragmentSinglOrderBinding
    private lateinit var viewModel: SingleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSinglOrderBinding.inflate(layoutInflater)
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SingleViewModel::class.java)

        viewModel.currentMeal.observe(activity!!) {
            binding.meal.text = it?.mealTableName
            binding.time.text = it?.mealStartTime + "~" + it?.mealEndTime
        }

        viewModel.config.observe(activity!!) {
            if (it != null) {
                binding.setCanteen.text = it.kitchenName
                binding.setWindow1.text = it.windowName
            }
        }
    }

    private fun initView() {

    }

    companion object {
        fun newInstance() = SingleOrderFragment()
    }

}