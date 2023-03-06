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
import com.yun.orderPad.databinding.FragmentSingleConfirmBinding
import com.yun.orderPad.ui.meal.SetMealViewModel

class MealConfirmFragment : Fragment() {

    private lateinit var binding : FragmentSingleConfirmBinding
    private lateinit var viewModel: SetMealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSingleConfirmBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SetMealViewModel::class.java)


    }

    private fun initView() {

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MealConfirmFragment().apply {}
    }
}