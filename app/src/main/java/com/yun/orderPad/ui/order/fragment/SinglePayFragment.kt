package com.yun.orderPad.ui.order.fragment

import android.content.Context
import android.content.Intent
import android.media.MediaRouter
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yun.orderPad.R
import com.yun.orderPad.databinding.ActivitySingleBinding
import com.yun.orderPad.databinding.FragmentPayBinding
import com.yun.orderPad.databinding.FragmentSinglOrderBinding
import com.yun.orderPad.ui.bind.BindActivity
import com.yun.orderPad.ui.order.SingleOrderActivity
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.ui.order.presentation.SinglePresentation
import com.yun.orderPad.ui.setting.SettingsActivity
import com.yun.orderPad.ui.test.ui.main.TestFragment
import com.yun.orderPad.util.CommonUtils
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.view.ConfirmAdapter
import com.yun.orderPad.view.OrderAdapter
import com.yun.orderPad.view.SpaceItemDecoration

class SinglePayFragment : Fragment() {

    private lateinit var binding : FragmentPayBinding
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
        binding = FragmentPayBinding.inflate(layoutInflater)
        return binding.root
    }
    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SingleViewModel::class.java)

    }

    private fun initView() {

    }

    companion object {
        fun newInstance() = SinglePayFragment()
    }

}