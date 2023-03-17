package com.julihe.order.ui.simple.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.julihe.order.databinding.FragmentSimpleOrderBinding
import com.julihe.order.ui.simple.SimpleViewModel

/**
 * 等待点餐
 */
class SimpleOrderFragment : Fragment() {

    private lateinit var binding : FragmentSimpleOrderBinding
    private lateinit var viewModel: SimpleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSimpleOrderBinding.inflate(layoutInflater)
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SimpleViewModel::class.java)

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
        fun newInstance() = SimpleOrderFragment()
    }

}