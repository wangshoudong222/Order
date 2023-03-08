package com.yun.orderPad.ui.order.presentation

import android.app.Presentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.yun.orderPad.R
import com.yun.orderPad.databinding.ActivityPConfirmBinding
import com.yun.orderPad.model.COMMIT_STATE
import com.yun.orderPad.ui.bind.BindActivity
import com.yun.orderPad.ui.meal.presentation.MealConfirmPresentation
import com.yun.orderPad.ui.order.SingleActivity
import com.yun.orderPad.ui.order.SingleViewModel
import com.yun.orderPad.util.CommonUtils
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.view.OrderShowAdapter
import com.yun.orderPad.view.SpaceItemDecoration

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

        viewModel.config.observe(activity) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.pConfirmTitle.titleName.text = it.schoolName + " " + it.windowName
            }
        }

        initAdapter()
        viewModel.state.value?.let {
            if (it == COMMIT_STATE.SUCCESS ) {
                LogUtil.d(TAG,"取餐成功")
                binding.bottomSuccess.visibility = View.VISIBLE
                binding.centerSuccess.visibility = View.VISIBLE
                binding.centerError.visibility = View.GONE

                binding.time.text = CommonUtils.formatToDate(System.currentTimeMillis())
                binding.payNum.text = viewModel.total.value.toString()

            } else if (it == COMMIT_STATE.ERROR){
                LogUtil.d(TAG,"取餐失败")
                binding.bottomSuccess.visibility = View.INVISIBLE
                binding.centerSuccess.visibility = View.GONE
                binding.centerError.visibility = View.VISIBLE

                binding.errorMsg.text = viewModel.errorMsg.value
            } else if (it == COMMIT_STATE.REORDER) {
                nestedScrollViewTop = 0
                dy = 0
            }
        }
    }

    private fun initViewModel() {
        activity = ownerActivity as SingleActivity
        viewModel = ViewModelProvider(activity).get(SingleViewModel::class.java)

        viewModel.student.observe(activity){
            it?.let {
                val infoText = it.numberOfClassName + " " + it.gradeName + " " + it.className + " "  +it.studentNo
                Glide.with(activity).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop())).placeholder(R.drawable.head_normal).into(binding.head)
                binding.name.text = it.studentName
                binding.school.text = it.schoolName
                binding.info.text = infoText
            }
        }
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

            // 下
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                dy += 100
                scrollY()
                return true
            }
            // 上
            KeyEvent.KEYCODE_DPAD_UP -> {
                dy -= 100
                if (dy < 0) dy =0
                scrollY()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    var nestedScrollViewTop = 0
    var dy =0

    private fun scrollY() {
        if (nestedScrollViewTop == 0) {
            val intArray = IntArray(2)
            binding.orderRv.getLocationOnScreen(intArray)
            nestedScrollViewTop = intArray[1]
        }
        val distance: Int = dy - nestedScrollViewTop //必须算上nestedScrollView本身与屏幕的距离
        binding.orderRv.fling(0, distance) //添加上这句滑动才有效
        binding.orderRv.smoothScrollBy(0, distance)
    }

    companion object {
        const val TAG = "ConfirmPresentation"
    }
}