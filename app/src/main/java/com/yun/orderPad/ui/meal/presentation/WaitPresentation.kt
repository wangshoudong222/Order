package com.yun.orderPad.ui.meal.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yun.orderPad.databinding.ActivityPSingleBinding
import com.yun.orderPad.event.ConfirmEvent
import com.yun.orderPad.model.COMMIT_STATE
import com.yun.orderPad.ui.order.ErrorPop1
import com.yun.orderPad.ui.order.SingleActivity
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.util.CommonUtils
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.MainThreadHandler
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.view.OrderAdapter
import com.yun.orderPad.view.SpaceItemDecoration
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal

class WaitPresentation(outerContext: Context?, display: Display?, ) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPSingleBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var activity: SingleActivity
    private var adapter:OrderAdapter? = null
    private var pop:ErrorPop1? = null
    private var focusIndex = 0
    private var codeInput = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPSingleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        activity = ownerActivity as SingleActivity
        viewModel = ViewModelProvider(activity).get(SingleViewModel::class.java)
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = OrderAdapter(context, viewModel.listMenu.value)
            val manager = GridLayoutManager(context,2)
            val spaceItemDecoration = SpaceItemDecoration(CommonUtils.dp2px(context, 30f))
            manager.orientation = RecyclerView.VERTICAL
            binding.orderRv.layoutManager = manager
            binding.orderRv.addItemDecoration(spaceItemDecoration)
            binding.orderRv.adapter = adapter
        }
    }

    private fun initView() {
        binding.btnCancel.setOnClickListener {
            ToastUtil.show("全部取消")
        }

        binding.btnCommit.setOnClickListener {
            val list = viewModel.listMenu.value?.filter { it.quantity!! > 0 }
            if (list?.isEmpty() == true) {
                ToastUtil.show("暂无选择餐点信息，请选择餐点后再提交")
            } else {
                viewModel.confirmOrder(list)
                viewModel.checkState(COMMIT_STATE.COMMITTING)
                EventBus.getDefault().post(ConfirmEvent())
            }
        }
    }

    companion object {
        const val TAG = "SinglePresentation"
    }
}