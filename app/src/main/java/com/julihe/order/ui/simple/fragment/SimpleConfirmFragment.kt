package com.julihe.order.ui.simple.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.julihe.order.R
import com.julihe.order.databinding.FragmentSimpleConfirmBinding
import com.julihe.order.databinding.FragmentSingleConfirmBinding
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.model.result.MealMenu
import com.julihe.order.ui.order.SingleViewModel
import com.julihe.order.ui.simple.SimpleViewModel
import com.julihe.order.util.LogUtil
import com.julihe.order.util.ToastUtil
import com.julihe.order.view.ConfirmAdapter

/**
 * 提交订单
 */
class SimpleConfirmFragment : Fragment() {

    private lateinit var binding : FragmentSimpleConfirmBinding
    private lateinit var viewModel: SimpleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSimpleConfirmBinding.inflate(layoutInflater)
        initView()
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SimpleViewModel::class.java)
        viewModel.input.observe(activity!!) {
            binding.total.text = it
        }
    }

    private fun initView() {
        binding.btnCancel.setOnClickListener {
            ToastUtil.show("取消购餐")
            viewModel.checkState(COMMIT_STATE.REORDER)
        }

        binding.btnCommit.setOnClickListener {
            viewModel.doScan(true)
        }
    }


    companion object {
        fun newInstance() = SimpleConfirmFragment()

        const val TAG = "SimpleConfirmFragment"
    }

}