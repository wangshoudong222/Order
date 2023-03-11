package com.julihe.orderPad.ui.bind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.julihe.orderPad.databinding.ActivityBindBinding
import com.julihe.orderPad.ui.choose.ChooseModeActivity
import com.julihe.orderPad.ui.login.LoginActivity
import com.julihe.orderPad.util.sp.SpUtil

class BindActivity : AppCompatActivity() {

    private lateinit var binding :ActivityBindBinding
    private lateinit var viewModel: BindViewModel

    private var setDialog: SettingPop? = null
    private var unbindDialog: UnBindPop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBindBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
        setBinding()
        setOnClick()
        initRequest()
    }

    private fun setView() {
        binding.sn.text = SpUtil.deviceId()
        binding.settingTitle.titleSetting.visibility = View.GONE
    }

    private fun setBinding() {
        viewModel = ViewModelProvider(this).get(BindViewModel::class.java)
        viewModel.config.observe(this) {
            if (it != null && !TextUtils.isEmpty(it.kitchenName)) {
                val bindInfo = it.kitchenName + "  " + it.windowName
                binding.bindInfo.text = bindInfo
            }
        }

        viewModel.configRequest.observe(this) {
            if(it) {
                binding.bindState.text = "设备已绑定"
            } else {
                binding.bindState.text = "设备未绑定"
                binding.bindInfo.text = ""
            }
        }

        viewModel.unBindState.observe(this) {
            if(it == true) {
                binding.bindState.text = "设备未绑定"
                binding.bindInfo.text = ""
            }
        }
    }

    private fun setOnClick() {
        binding.btnSetting.setOnClickListener {
            if (setDialog == null) {
                setDialog = SettingPop(viewModel)
                setDialog?.let { log->
                    if (!log.isShowing) {
                        log.show(supportFragmentManager, log.javaClass.name)
                    }
                }
            } else if (setDialog?.isShowing != true) {
                setDialog?.show(supportFragmentManager, it.javaClass.name)
            }
        }

        binding.btnExit.setOnClickListener {
            val data = intent.getStringExtra("data")
            if (data == LoginActivity.START && viewModel.configRequest.value == true) {
                startActivity(Intent(this, ChooseModeActivity::class.java))
            }
            this.finish()
        }

        binding.btnUnbind.setOnClickListener {
            if (unbindDialog == null) {
                unbindDialog = UnBindPop(viewModel)
                unbindDialog?.let { log->
                    if (!log.isShowing) {
                        log.show(supportFragmentManager, log.javaClass.name)
                    }
                }
            } else if (unbindDialog?.isShowing != true) {
                unbindDialog?.show(supportFragmentManager, it.javaClass.name)
            }
        }
    }

    private fun initRequest() {
        viewModel.getConfig(true)
    }
}