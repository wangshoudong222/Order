package com.yun.orderPad.ui.order.fragment

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
import com.yun.orderPad.R
import com.yun.orderPad.databinding.FragmentSingleConfirmBinding
import com.yun.orderPad.event.CancelEvent
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.util.CommonUtils
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.view.ConfirmAdapter
import com.yun.orderPad.view.SpaceItemDecoration
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal

/**
 * 提交订单
 */
class SingleConfirmFragment : Fragment() {

    private lateinit var binding : FragmentSingleConfirmBinding
    private lateinit var viewModel: SingleViewModel
    private var adapter: ConfirmAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSingleConfirmBinding.inflate(layoutInflater)
        initView()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SingleViewModel::class.java)
    }

    private fun initView() {
        initAdapter()
        var num:Long = 0
        var total: BigDecimal = BigDecimal.ZERO
        viewModel.confirmOrder.value?.forEach {menu->
            menu.quantity?.let { count->
                num += count
                total = total.add(menu.price?.multiply(BigDecimal(count)))
            }
        }

        binding.tvNum.text = num.toString()
        binding.total.text = total.toString()

        binding.btnCancel.setOnClickListener {
            ToastUtil.show("取消购餐")
            EventBus.getDefault().post(CancelEvent())
        }

        binding.btnCommit.setOnClickListener {
            viewModel.doScan(true)
        }
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = ConfirmAdapter(activity!!, viewModel.confirmOrder.value)
            val dividerItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
            dividerItemDecoration.setDrawable(activity!!.getDrawable(R.drawable.item_white)!!)
            binding.rv.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            binding.rv.addItemDecoration(dividerItemDecoration)
            binding.rv.adapter = adapter
        }
    }

    companion object {
        fun newInstance() = SingleConfirmFragment()
    }

}