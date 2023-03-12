package com.julihe.order.ui.setting

import android.content.Context
import android.content.Intent
import android.media.MediaRouter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.julihe.order.databinding.ActivitySettingsBinding
import com.julihe.order.ui.bind.BindActivity
import com.julihe.order.ui.choose.ChooseModeActivity
import com.julihe.order.ui.login.LoginActivity
import com.julihe.order.util.sp.SpUtil

class SettingsActivity : AppCompatActivity() {


    private lateinit var binding : ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addPresentation()
        initView()
        initViewModel()
    }

    private fun initView() {
        binding.settingTitle.titleSetting.visibility = View.GONE
        binding.btnExit.setOnClickListener { this.finish() }
        binding.btnCheck.setOnClickListener {
            startActivity(Intent(this, ChooseModeActivity::class.java))
            this.finish()
        }
        binding.btnBind.setOnClickListener {
            startActivity(Intent(this, BindActivity::class.java))
            this.finish()
        }
        binding.btnLogout.setOnClickListener {
            SpUtil.token("")
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        viewModel.getConfig()

        viewModel.config.observe(this) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.settingTitle.titleName.text = it.schoolName + " " + it.windowName
            }
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
}