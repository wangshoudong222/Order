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

class SinglePresentation(outerContext: Context?, display: Display?, ) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPSingleBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var activity: SingleActivity
    private var adapter:OrderAdapter? = null
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

        viewModel.currentMeal.observe(activity) {
            if (it != null) {
                viewModel.getMealMenuList()
            }
        }

        viewModel.listMenu.observe(activity) {
            initShow()
            initAdapter()
        }

        viewModel.total.observe(activity) {
            binding.tvCount.text = it.toString()
        }

        viewModel.totalMeals.observe(activity) {
            binding.tvSel.text = it.toString()
        }

        viewModel.state.observe(activity) {
            when(it) {
                COMMIT_STATE.ORDER -> {
                    LogUtil.d(TAG,"正在点餐")
                    binding.pb.visibility = View.GONE
                }

                COMMIT_STATE.REORDER -> {
                    LogUtil.d(TAG,"重新点餐")
                    binding.pb.visibility = View.GONE
                }

                COMMIT_STATE.COMMITTING -> {
                    LogUtil.d(TAG,"正在提交")
                    binding.pb.visibility = View.VISIBLE
                }

                COMMIT_STATE.SCANNING -> {
                    LogUtil.d(SingleActivity.TAG,"正在扫脸")
                }

                COMMIT_STATE.SUCCESS -> {
                    ToastUtil.show("取餐成功")
                    binding.pb.visibility = View.GONE
                }

                COMMIT_STATE.ERROR -> {
                    binding.pb.visibility = View.GONE
                    ToastUtil.show("取餐失败")
                }

                else -> {
                    binding.pb.visibility = View.GONE
                }
            }
        }

    }

    private fun initShow() {
        viewModel.listMenu.value?.get(0)?.fouces = true
        viewModel.listMenu.value?.forEachIndexed { index, mealMenu ->
            mealMenu.checked = false
            mealMenu.quantity = 0
            mealMenu.fouces = index == 0
        }
        viewModel.setTotal(BigDecimal.ZERO)
        viewModel.setTotalMeal(0)
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
    @Synchronized
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG,"keyCode: $keyCode")
        when (keyCode) {
            // 确认键
            KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                binding.btnCommit.performClick()
                return true
            }
            // 删除键
            KeyEvent.KEYCODE_DEL -> {
                viewModel.listMenu.value?.let {
                    it[focusIndex].checked = false
                }
                if (codeInput.isNotEmpty()) {
                    codeInput.deleteCharAt(codeInput.lastIndex)
                }
                return true
            }
            // 取消键
            KeyEvent.KEYCODE_ESCAPE -> {
                viewModel.listMenu.value?.let {
                    it.forEach { menu->
                        menu.checked = false
                        menu.fouces = false
                    }
                    focusIndex = 0
                    it[focusIndex].fouces = true
                }
                if (codeInput.isNotEmpty()) {
                    codeInput.clear()
                }
                binding.orderRv.scrollToPosition(focusIndex)
                return true
            }
            // 设置键
            KeyEvent.KEYCODE_F1 -> {
                return true
            }
            // 功能键
            KeyEvent.KEYCODE_F2 -> {
                return true
            }
            // 下
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                viewModel.listMenu.value?.let {
                    it[focusIndex].fouces = false
                    adapter?.notifyItemChanged(focusIndex++)
                    if (focusIndex >= it.size) {
                        focusIndex = 0
                    }
                    it[focusIndex].fouces = true
                    adapter?.notifyItemChanged(focusIndex)
                    binding.orderRv.scrollToPosition(focusIndex)
                }
                return true
            }
            // 上
            KeyEvent.KEYCODE_DPAD_UP -> {
                viewModel.listMenu.value?.let {
                    it[focusIndex].fouces = false
                    adapter?.notifyItemChanged(focusIndex--)
                    if (focusIndex < 0) {
                        focusIndex = 0
                    }
                    it[focusIndex].fouces = true
                    adapter?.notifyItemChanged(focusIndex)
                    binding.orderRv.scrollToPosition(focusIndex)
                }
                return true
            }
            // 点
            KeyEvent.KEYCODE_NUMPAD_DOT -> {
                return true
            }
            // 加
            KeyEvent.KEYCODE_NUMPAD_ADD -> {
                viewModel.listMenu.value?.get(focusIndex)?.fouces = false
                adapter?.notifyItemChanged(focusIndex)
                val index = viewModel.getMenuByCode(codeInput.toString())
                if (index != -1) {
                    focusIndex = index
                }
                viewModel.listMenu.value?.get(focusIndex)?.let {
                    it.checked = true
                    it.quantity = it.quantity?.plus(1)
                    it.fouces = true
                    viewModel.setTotal(viewModel.total.value?.add(it.price))
                    viewModel.setTotalMeal(viewModel.totalMeals.value?.plus(1))
                }
                adapter?.notifyItemChanged(focusIndex)
                binding.orderRv.scrollToPosition(focusIndex)

                codeInput.clear()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_1 -> {
                codeInput.append("1")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_2 -> {
                codeInput.append("2")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_3 -> {
                codeInput.append("3")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_4 -> {
                codeInput.append("4")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_5 -> {
                codeInput.append("5")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_6 -> {
                codeInput.append("6")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_7 -> {
                codeInput.append("7")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_8 -> {
                codeInput.append("8")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_9 -> {
                codeInput.append("9")
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_0 -> {
                codeInput.append("0")
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        const val TAG = "SinglePresentation"
    }
}