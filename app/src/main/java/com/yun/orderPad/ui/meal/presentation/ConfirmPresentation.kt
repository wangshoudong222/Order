package com.yun.orderPad.ui.meal.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yun.orderPad.R
import com.yun.orderPad.databinding.ActivityPConfirmBinding
import com.yun.orderPad.model.COMMIT_STATE
import com.yun.orderPad.ui.order.SingleActivity
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.util.CommonUtils
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.view.OrderShowAdapter

/**
 * 取餐完成或取餐失败副屏
 */
class ConfirmPresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPConfirmBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var activity: SingleActivity
    private var adapter: OrderShowAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initView()
    }

    private fun initView() {
        binding.btnGo.setOnClickListener {
            viewModel.checkState(COMMIT_STATE.REORDER)
        }

        initAdapter()
        viewModel.state.value?.let {
            if (it == COMMIT_STATE.SUCCESS ) {
                LogUtil.d(TAG,"取餐成功")
                binding.bottomSuccess.visibility = View.VISIBLE
                binding.centerSuccess.visibility = View.VISIBLE
                binding.centerError.visibility = View.GONE

                binding.time.text = CommonUtils.formatToDate(System.currentTimeMillis())
                binding.payNum.text = viewModel.total.toString()

            } else if (it == COMMIT_STATE.ERROR){
                LogUtil.d(TAG,"取餐失败")
                binding.bottomSuccess.visibility = View.INVISIBLE
                binding.centerSuccess.visibility = View.GONE
                binding.centerError.visibility = View.VISIBLE

                binding.errorMsg.text = viewModel.errorMsg.value
            }
        }
    }

    private fun initViewModel() {
        activity = ownerActivity as SingleActivity
        viewModel = ViewModelProvider(activity).get(SingleViewModel::class.java)
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = OrderShowAdapter(context, viewModel.confirmOrder.value)
            val manager = LinearLayoutManager(context)
            manager.orientation = RecyclerView.VERTICAL
            val dividerItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
            dividerItemDecoration.setDrawable(context.getDrawable(R.drawable.item_white)!!)
            binding.orderRv.layoutManager = manager
            binding.orderRv.addItemDecoration(dividerItemDecoration)
            binding.orderRv.adapter = adapter
        }
    }

    @Synchronized
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG,"keyCode: $keyCode")
        when (keyCode) {
            // 确认键
            KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                binding.btnGo.performClick()
                return true
            }

            // 取消键
            KeyEvent.KEYCODE_ESCAPE -> {
                binding.btnGo.performClick()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        const val TAG = "ConfirmPresentation"
    }
}