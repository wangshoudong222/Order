package com.yun.orderPad.ui.order.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yun.orderPad.databinding.ActivityPConfirmBinding
import com.yun.orderPad.databinding.ActivityPSingleBinding
import com.yun.orderPad.event.ConfirmEvent
import com.yun.orderPad.model.COMMIT_STATE
import com.yun.orderPad.ui.order.SingleActivity
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.util.CommonUtils
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.view.OrderAdapter
import com.yun.orderPad.view.SpaceItemDecoration
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal

class ConfirmPresentation(outerContext: Context?, display: Display?, ) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPConfirmBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var activity: SingleActivity
    private var adapter:OrderAdapter? = null
    private var focusIndex = 0
    private var codeInput = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
    }

    private fun initViewModel() {
        activity = ownerActivity as SingleActivity
        viewModel = ViewModelProvider(activity).get(SingleViewModel::class.java)

        viewModel.currentMeal.observe(activity) {
            if (it != null) {
                viewModel.getMealMenuList()
            }
        }

        viewModel.listMenu.observe(activity) {
            initAdapter()
        }

        viewModel.total.observe(activity) {

        }

        viewModel.totalMeals.observe(activity) {

        }

        viewModel.state.observe(activity) {
            when(it) {
                COMMIT_STATE.ORDER -> {
                    LogUtil.d(TAG,"正在点餐")
                }

                COMMIT_STATE.REORDER -> {
                    LogUtil.d(TAG,"重新点餐")
                }

                COMMIT_STATE.COMMITTING -> {
                    LogUtil.d(TAG,"正在提交")
                }

                COMMIT_STATE.SCANNING -> {
                    LogUtil.d(TAG,"正在扫脸")
                }

                COMMIT_STATE.SUCCESS -> {
                    ToastUtil.show("取餐成功")
                }

                COMMIT_STATE.ERROR -> {
                }

                else -> {

                }
            }
        }

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

    companion object {
        const val TAG = "ConfirmPresentation"
    }
}