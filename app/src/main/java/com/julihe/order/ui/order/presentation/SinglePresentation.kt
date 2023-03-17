package com.julihe.order.ui.order.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.julihe.order.databinding.ActivityPSingleBinding
import com.julihe.order.event.ConfirmEvent
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.model.result.MealMenu
import com.julihe.order.ui.order.SingleActivity
import com.julihe.order.ui.order.SingleViewModel
import com.julihe.order.util.CommonUtils
import com.julihe.order.util.LogUtil
import com.julihe.order.util.MainThreadHandler
import com.julihe.order.util.ToastUtil
import com.julihe.order.view.OrderAdapter
import com.julihe.order.view.SpaceItemDecoration
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import kotlin.reflect.jvm.internal.impl.resolve.VisibilityUtilKt

class SinglePresentation(outerContext: Context?, display: Display?, ) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPSingleBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var activity: SingleActivity
    private var adapter:OrderAdapter? = null
    private var focusIndex = 0
    private var codeInput = StringBuilder()
    private var isInit = true

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

        viewModel.config.observe(activity) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.pSingleTitle.titleName.text = it.schoolName + " " + it.windowName
            }
        }

        viewModel.listMenu.observe(activity) {
            if (it != null && it.isNotEmpty()) {
                LogUtil.d(TAG, "SinglePresentation listMenu变化")
                setAdapter(it)
            }
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
                }

                COMMIT_STATE.REORDER -> {
                    LogUtil.d(TAG,"重新点餐")
                }

                COMMIT_STATE.COMMITTING -> {
                    LogUtil.d(TAG,"正在提交")
                    showTip("等待支付", false)
                }

                COMMIT_STATE.SCANNING -> {
                    LogUtil.d(TAG,"正在扫脸")
                    showTip("正在扫脸", false)
                }

                COMMIT_STATE.SCAN_ERROR -> {
                    LogUtil.d(TAG,"扫脸失败")
                    showTip("扫脸失败", true)
                }

                COMMIT_STATE.SUCCESS -> {
                }

                COMMIT_STATE.ERROR -> {
                }

                else -> {
                }
            }
        }
    }

    private fun showTip(string: String?, cancel: Boolean) {
        MainThreadHandler.removeCallbacks(TAG_TIPS)
        binding.tvInfo.text = string
        binding.tvInfo.visibility = View.VISIBLE
        if (cancel) {
            MainThreadHandler.postDelayed(TAG_TIPS, {
                binding.tvInfo.visibility = View.GONE
            },5000)
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

    private fun setAdapter(mealMenus: List<MealMenu>) {
        adapter = OrderAdapter(context, mealMenus)
        binding.orderRv.adapter = adapter
        focusIndex = 0
        binding.orderRv.scrollToPosition(focusIndex)
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

        val manager = GridLayoutManager(context,2)
        val spaceItemDecoration =
            SpaceItemDecoration(CommonUtils.dp2px(
                context,
                30f))
        manager.orientation = RecyclerView.VERTICAL
        binding.orderRv.layoutManager = manager
        binding.orderRv.addItemDecoration(spaceItemDecoration)
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
                if (codeInput.isNotEmpty()) {
                    codeInput.deleteCharAt(codeInput.lastIndex)
                    return true
                } else {
                    viewModel.listMenu.value?.get(focusIndex)?.let {
                        if (it.quantity!= null && it.quantity!! <= 0) {
                            it.quantity = 0
                        } else {
                            it.quantity = it.quantity?.minus(1)
                            viewModel.setTotal(viewModel.total.value?.minus(it.price!!))
                            viewModel.setTotalMeal(viewModel.totalMeals.value?.minus(1))
                            if (it.quantity!! == 0L) {
                                it.checked = false
                            }
                        }
                        adapter?.notifyItemChanged(focusIndex)
                    }
                }
                return true
            }
            // 取消键
            KeyEvent.KEYCODE_ESCAPE -> {
                viewModel.listMenu.value?.let {
                    it.forEach { menu->
                        menu.checked = false
                        menu.fouces = false
                        menu.quantity = 0
                    }
                    focusIndex = 0
                    it[focusIndex].fouces = true
                }
                if (codeInput.isNotEmpty()) {
                    codeInput.clear()
                }

                binding.orderRv.scrollToPosition(focusIndex)
                adapter?.notifyDataSetChanged()
                viewModel.checkState(COMMIT_STATE.REORDER)
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
        const val TAG_TIPS = "TAG_TIPS"
    }
}