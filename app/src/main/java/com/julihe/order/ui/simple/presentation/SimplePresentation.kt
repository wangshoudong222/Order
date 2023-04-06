package com.julihe.order.ui.simple.presentation

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
import com.julihe.order.databinding.ActivityPSimpleBinding
import com.julihe.order.databinding.ActivityPSingleBinding
import com.julihe.order.event.ConfirmEvent
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.model.result.MealMenu
import com.julihe.order.ui.order.SingleActivity
import com.julihe.order.ui.order.SingleViewModel
import com.julihe.order.ui.simple.SimpleActivity
import com.julihe.order.ui.simple.SimpleViewModel
import com.julihe.order.util.CommonUtils
import com.julihe.order.util.LogUtil
import com.julihe.order.util.MainThreadHandler
import com.julihe.order.util.ToastUtil
import com.julihe.order.view.OrderAdapter
import com.julihe.order.view.SpaceItemDecoration
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.text.DecimalFormat

class SimplePresentation(outerContext: Context?, display: Display?, ) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPSimpleBinding
    private lateinit var viewModel: SimpleViewModel
    private lateinit var activity: SimpleActivity
    private var codeInput = StringBuilder()
    private var preInput: Double = 0.00
    private var format = DecimalFormat("0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPSimpleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        activity = ownerActivity as SimpleActivity
        viewModel = ViewModelProvider(activity).get(SimpleViewModel::class.java)
        viewModel.setInput("0")
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2
        viewModel.config.observe(activity) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.pSingleTitle.titleName.text = it.schoolName + " " + it.windowName
                binding.setCanteen.text = it.kitchenName + " " + it.windowName
            }
        }

        viewModel.currentMeal.observe(activity) {
            binding.meal.text = it?.mealTableName
            binding.time.text = it?.mealStartTime + "~" + it?.mealEndTime
        }

        viewModel.input.observe(activity) {
            binding.input.text = it
        }

        viewModel.state.observe(activity) {
            when(it) {
                COMMIT_STATE.COMMITTING -> {
                    binding.tvInfo.visibility = View.VISIBLE
                    binding.meal.visibility = View.GONE
                    binding.timeLl.visibility = View.GONE
                }

                COMMIT_STATE.SCANNING -> {
                    binding.tvInfo.visibility = View.VISIBLE
                    binding.meal.visibility = View.GONE
                    binding.timeLl.visibility = View.GONE
                }

                COMMIT_STATE.REORDER -> {
                    binding.tvInfo.visibility = View.GONE
                    binding.meal.visibility = View.VISIBLE
                    binding.timeLl.visibility = View.VISIBLE
                    reOrder()
                }

                else -> {
                    binding.tvInfo.visibility = View.GONE
                    binding.meal.visibility = View.VISIBLE
                    binding.timeLl.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initView() {

    }

    @Synchronized
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        LogUtil.d(TAG,"keyCode: $keyCode")
        if (viewModel.state.value == COMMIT_STATE.COMMITTING) {
            if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                reOrder()
                return true
            }

            return super.onKeyDown(keyCode, event)
        }
        when (keyCode) {
            // 确认键
            KeyEvent.KEYCODE_NUMPAD_ENTER,KeyEvent.KEYCODE_ENTER -> {
                add()
                viewModel.confirmOrder()
                viewModel.checkState(COMMIT_STATE.COMMITTING)
            }
            // 删除键
            KeyEvent.KEYCODE_DEL -> {
                dle()
                return true
            }
            // 取消键
            KeyEvent.KEYCODE_ESCAPE -> {
                reOrder()
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

                return true
            }
            // 上
            KeyEvent.KEYCODE_DPAD_UP -> {

                return true
            }
            // 点
            KeyEvent.KEYCODE_NUMPAD_DOT -> {
                if (!codeInput.toString().contains(".")) {
                    if (codeInput.isEmpty()) {
                        codeInput.append("0")
                    }
                    codeInput.append(".")
                    sync()
                }
                return true
            }
            // 加
            KeyEvent.KEYCODE_NUMPAD_ADD -> {
                add()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_1 -> {
                codeInput.append("1")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_2 -> {
                codeInput.append("2")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_3 -> {
                codeInput.append("3")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_4 -> {
                codeInput.append("4")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_5 -> {
                codeInput.append("5")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_6 -> {
                codeInput.append("6")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_7 -> {
                codeInput.append("7")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_8 -> {
                codeInput.append("8")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_9 -> {
                codeInput.append("9")
                sync()
                return true
            }
            KeyEvent.KEYCODE_NUMPAD_0 -> {
                codeInput.append("0")
                sync()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun dle() {
        if (codeInput.toString().isNotEmpty()) {
            codeInput.deleteCharAt(codeInput.length-1)
        }
    }

    private fun sync() {
        val s = codeInput.toString().format(format)
        viewModel.setInput(s)
    }

    private fun add() {
        if (codeInput.toString().isNotEmpty()) {
            val cur = codeInput.toString().format(format).toDouble()
            codeInput.clear()
            LogUtil.d(TAG, "cur:$cur")
            preInput = format.format(preInput.plus(cur)).toDouble()
            viewModel.setInput(preInput.toString())
            LogUtil.d(TAG, "preInput:$preInput")
        } else {
            viewModel.setInput(preInput.toString())
        }
    }

    private fun reOrder() {
        preInput = 0.00
        viewModel.setInput("0")
        if (codeInput.isNotEmpty()) {
            codeInput.clear()
        }
    }

    companion object {
        const val TAG = "SinglePresentation"
    }
}