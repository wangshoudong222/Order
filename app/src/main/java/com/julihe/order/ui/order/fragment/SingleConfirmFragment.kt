package com.julihe.order.ui.order.fragment

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
import com.julihe.order.databinding.FragmentSingleConfirmBinding
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.model.result.MealMenu
import com.julihe.order.ui.order.SingleViewModel
import com.julihe.order.util.LogUtil
import com.julihe.order.util.ToastUtil
import com.julihe.order.view.ConfirmAdapter

/**
 * 提交订单
 */
class SingleConfirmFragment : Fragment() {

    private lateinit var binding : FragmentSingleConfirmBinding
    private lateinit var viewModel: SingleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSingleConfirmBinding.inflate(layoutInflater)
        initView()
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(activity!!).get(SingleViewModel::class.java)
        viewModel.totalMeals.observe(activity!!){
            LogUtil.d(TAG,"获取总餐为:${it.toString()}")
            binding.tvNum.text = it?.toString()
        }

        viewModel.total.observe(activity!!){
            LogUtil.d(TAG,"获取总数为:${it.toString()}")
            binding.total.text = it?.toString()
        }

        viewModel.confirmOrder.observe(activity!!) {
            if (it != null) {
                setAdapter(it)
            }
        }
    }

    private fun initView() {
        initRv()
//        var num:Long = 0
//        var total: BigDecimal = BigDecimal.ZERO
//        viewModel.confirmOrder.value?.forEach {menu->
//            menu.quantity?.let { count->
//                num += count
//                total = total.add(menu.price?.multiply(BigDecimal(count)))
//            }
//        }
//
//        binding.tvNum.text = num.toString()
//        binding.total.text = total.toString()

        binding.btnCancel.setOnClickListener {
            ToastUtil.show("取消购餐")
            viewModel.checkState(COMMIT_STATE.REORDER)
        }

        binding.btnCommit.setOnClickListener {
            viewModel.doScan(true)
        }
    }

    private fun setAdapter(menus: List<MealMenu>?) {
        val adapter = ConfirmAdapter(context, menus)
        binding.rv.adapter = adapter
    }

    private fun initRv() {
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
        dividerItemDecoration.setDrawable(activity!!.getDrawable(R.drawable.item_white)!!)
        binding.rv.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rv.addItemDecoration(dividerItemDecoration)
    }

    companion object {
        fun newInstance() = SingleConfirmFragment()

        const val TAG = "SingleConfirmFragment"
    }

}