package com.yun.orderPad.ui.order.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.R
import com.yun.orderPad.databinding.FragmentSinglOrderBinding
import com.yun.orderPad.databinding.FragmentSingleConfirmBinding
import com.yun.orderPad.ui.order.SingleViewModel

class SingleConfirmFragment : Fragment() {

    private lateinit var binding : FragmentSingleConfirmBinding
    private lateinit var viewModel: SingleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSingleConfirmBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SingleViewModel::class.java)

    }

    private fun initView() {

    }

    companion object {
        fun newInstance() = SingleConfirmFragment()
    }

}