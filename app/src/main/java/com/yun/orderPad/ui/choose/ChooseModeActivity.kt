package com.yun.orderPad.ui.choose

import android.content.Context
import android.content.Intent
import android.media.MediaRouter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.databinding.ActivityChooseModeBinding
import com.yun.orderPad.ui.meal.SetMealActivity
import com.yun.orderPad.ui.order.SingleOrderActivity
import com.yun.orderPad.ui.bind.BindActivity
import com.yun.orderPad.ui.login.LoginPresentation
import com.yun.orderPad.ui.order.SingleActivity
import com.yun.orderPad.ui.setting.SettingsActivity
import com.yun.orderPad.ui.setting.SettingsPresentation
import com.yun.orderPad.util.MainThreadHandler
import com.yun.orderPad.util.ToastUtil

class ChooseModeActivity : AppCompatActivity() {

    private lateinit var binding :ActivityChooseModeBinding
    private lateinit var viewModel: ChooseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addPresentation()
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(ChooseViewModel::class.java)
        viewModel.getOrderModeList()
        viewModel.getConfig()

        viewModel.config.observe(this) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.chooseTitle.titleName.text = it.schoolName + " " + it.windowName
            } else {
                ToastUtil.show("设备未绑定，请先绑定设备信息")
                startActivity(Intent(this, BindActivity::class.java))
            }
        }

        viewModel.orderModes.observe(this) {
            if (it != null && it.size >= 2) {
                binding.btn1.text = it[0].orderModeName
                binding.btn2.text = it[1].orderModeName
            }
        }

        viewModel.mode.observe(this) {
            startActivity(Intent(this, if (MODE_TSLDXJ == it?.orderMode) SingleActivity::class.java else SetMealActivity::class.java))
            this.finish()
        }

        MainThreadHandler.postDelayed({
            startActivity(Intent(this, SingleActivity::class.java))
        },1000)
    }

    private fun initView() {
        binding.chooseTitle.titleSetting.visibility = View.VISIBLE
        binding.chooseTitle.titleSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btn1.setOnClickListener {
            viewModel.setMode(viewModel.orderModes.value?.get(0))
        }

        binding.btn2.setOnClickListener {
            viewModel.setMode(viewModel.orderModes.value?.get(1))
        }
    }

    private var presentationDisplay: SettingsPresentation? = null
    private fun addPresentation(){
        val mediaRouter: MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
        val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
        if (route != null && route.presentationDisplay != null) {
            presentationDisplay = SettingsPresentation(this,route.presentationDisplay)
            presentationDisplay?.show()
        }
    }

    companion object {
        const val MODE_QCMS = "QCMS"
        const val MODE_TSLDXJ = "TSLDXJ"
    }
}