package com.julihe.order.ui.simple.presentation

import android.app.Presentation
import android.content.Context
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.julihe.order.R
import com.julihe.order.databinding.ActivityPConfirmBinding
import com.julihe.order.databinding.ActivityPSimplePayBinding
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.ui.order.SingleActivity
import com.julihe.order.ui.order.SingleViewModel
import com.julihe.order.ui.simple.SimpleActivity
import com.julihe.order.ui.simple.SimpleViewModel
import com.julihe.order.util.CommonUtils
import com.julihe.order.util.LogUtil
import com.julihe.order.view.OrderShowAdapter

/**
 * 取餐完成或取餐失败副屏
 */
class SimplePayPresentation(outerContext: Context?, display: Display?) :
    Presentation(outerContext, display) {

    private lateinit var binding : ActivityPSimplePayBinding
    private lateinit var viewModel: SimpleViewModel
    private lateinit var activity: SimpleActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPSimplePayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        initView()
    }

    private fun initView() {
        binding.btnGo.setOnClickListener {
            viewModel.checkState(COMMIT_STATE.REORDER)
        }
    }

    private fun initViewModel() {
        activity = ownerActivity as SimpleActivity
        viewModel = ViewModelProvider(activity).get(SimpleViewModel::class.java)

        viewModel.student.observe(activity){
            it?.let {
                val infoText = it.numberOfClassName + " " + it.gradeName + " " + it.className + " "  +it.studentNo
                Glide.with(activity).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop())).placeholder(R.drawable.head_normal).into(binding.head)
                binding.name.text = it.studentName
                binding.school.text = it.schoolName
                binding.info.text = infoText
            }
        }

        viewModel.config.observe(activity) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.pConfirmTitle.titleName.text = it.schoolName + " " + it.windowName
            }
        }

        viewModel.input.observe(activity){
            LogUtil.d(TAG,"获取总结账为:${it?.toFloat().toString()}")
            if (it != null) {
                binding.payNum.text = it.toFloat().toString()
            }
        }

        viewModel.state.value?.let {
            when (it) {
                COMMIT_STATE.SUCCESS -> {
                    LogUtil.d(TAG,"取餐成功")
                    binding.bottomSuccess.visibility = View.VISIBLE
                    binding.centerSuccess.visibility = View.VISIBLE
                    binding.centerError.visibility = View.GONE
                    binding.time.text = CommonUtils.formatToDate(System.currentTimeMillis())
                }
                COMMIT_STATE.ERROR -> {
                    LogUtil.d(TAG,"取餐失败")
                    binding.bottomSuccess.visibility = View.GONE
                    binding.centerSuccess.visibility = View.GONE
                    binding.centerError.visibility = View.VISIBLE
                    LogUtil.d(TAG, "errorMsg:" +viewModel.errorMsg.value)
                    binding.errorMsg.text = viewModel.errorMsg.value
                }
            }
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