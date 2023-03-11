package com.julihe.orderPad.ui.meal.presentation

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.KeyEvent
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.julihe.orderPad.R
import com.julihe.orderPad.databinding.ActivityPMealConfirmBinding
import com.julihe.orderPad.model.result.OrderInfo
import com.julihe.orderPad.ui.meal.SetMealActivity
import com.julihe.orderPad.ui.meal.SetMealViewModel
import com.julihe.orderPad.ui.meal.adapter.PMealDataAdapter
import com.julihe.orderPad.util.LogUtil

/**
 * 取餐完成或取餐失败副屏
 */
class MealConfirmPresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPMealConfirmBinding
    private lateinit var viewModel: SetMealViewModel
    private lateinit var activity: SetMealActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPMealConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initView()
    }

    private fun initView() {
        binding.btnGo.setOnClickListener {
            viewModel.confirmPickUp()
        }

        intiRv()
    }

    private fun intiRv() {
        val manager = LinearLayoutManager(context)
        manager.orientation = RecyclerView.VERTICAL
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayout.VERTICAL)
        dividerItemDecoration.setDrawable(context.getDrawable(R.drawable.item_white)!!)
        binding.orderRv.layoutManager = manager
        binding.orderRv.addItemDecoration(dividerItemDecoration)
    }

    private fun initViewModel() {
        activity = ownerActivity as SetMealActivity
        viewModel = ViewModelProvider(activity).get(SetMealViewModel::class.java)

        viewModel.config.observe(activity) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.pConfirmTitle.titleName.text = it.schoolName + " " + it.windowName
            }
        }

        viewModel.selfOrder.observe(activity) {
            if(it != null && it.isNotEmpty()) {
                setAdapter(it)
            }
        }

        viewModel.student.observe(activity){
            it?.let {
                val infoText = it.numberOfClassName + " " + it.gradeName + " " + it.className + " "  +it.studentNo
                Glide.with(activity).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop())).placeholder(R.drawable.head_normal).into(binding.head)
                binding.name.text = it.studentName
                binding.school.text = it.schoolName
                binding.info.text = infoText
            }
            if (it == null) {
                dy =0
                nestedScrollViewTop = 0
                scrollY()
            }
        }
    }

    private fun setAdapter(orderInfos: List<OrderInfo>) {
        val adapter = PMealDataAdapter(activity, orderInfos)
        binding.orderRv.adapter = adapter
    }

    var nestedScrollViewTop = 0
    var dy =0

    private fun scrollY() {
        if (nestedScrollViewTop == 0) {
            val intArray = IntArray(2)
            binding.ns.getLocationOnScreen(intArray)
            nestedScrollViewTop = intArray[1]
        }
        val distance: Int = dy - nestedScrollViewTop //必须算上nestedScrollView本身与屏幕的距离
        binding.ns.fling(distance) //添加上这句滑动才有效
        binding.ns.smoothScrollBy(0, distance)
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
                viewModel.cleanState()
                return true
            }

            // 下
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                dy += 100
                scrollY()
                LogUtil.d(TAG,"KEYCODE_DPAD_DOWN" +dy)
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

    companion object {
        const val TAG = "MealConfirmPresentation"
    }
}