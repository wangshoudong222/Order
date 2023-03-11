package com.julihe.orderPad.ui.welcome

import android.content.Context
import android.widget.TextView
import android.os.Bundle
import com.julihe.orderPad.util.sp.SpUtil
import android.text.TextUtils
import com.julihe.orderPad.util.MainThreadHandler
import android.content.Intent
import android.media.MediaRouter
import android.util.Log
import android.view.Display
import androidx.lifecycle.ViewModelProvider
import com.julihe.orderPad.BuildConfig
import com.julihe.orderPad.R
import com.julihe.orderPad.databinding.ActivityWelcomeBinding
import com.julihe.orderPad.ui.BaseActivity
import com.julihe.orderPad.ui.login.LoginActivity
import com.julihe.orderPad.ui.bind.BindActivity
import com.julihe.orderPad.ui.choose.ChooseModeActivity

class WelcomeActivity : BaseActivity() {

    private var tvVersion: TextView? = null
    private var viewModel: WelcomeViewModel? = null
    private lateinit var binding : ActivityWelcomeBinding
    private var presentationDisplay: WelcomePresentation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addPresentation()
        viewModel = ViewModelProvider(this).get(WelcomeViewModel::class.java)

        viewModel?.configRequest?.observe(this) {
            jumpActivity(if (it) ChooseModeActivity::class.java else BindActivity::class.java)
        }

        tvVersion = findViewById(R.id.version)
        tvVersion?.text = BuildConfig.VERSION_NAME
        checkLogin()
    }

    private fun checkLogin() {
        val token = SpUtil.token()
        Log.d(TAG,"token:$token")
        if (TextUtils.isEmpty(token)) {
            jumpActivity(LoginActivity::class.java)
        } else {
            viewModel?.getConfig()
        }
    }

    private fun jumpActivity(clazz: Class<*>) {
        MainThreadHandler.postDelayed(TAG, {
            startActivity(Intent(this, clazz))
            finish()
        }, 2000)
    }

    private fun addPresentation(){
        val mediaRouter:MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
        val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
        if (route != null && route.presentationDisplay != null) {
            presentationDisplay = WelcomePresentation(this,route.presentationDisplay)
            presentationDisplay?.show()
        }
    }

    companion object {
        private const val TAG = "WelcomeActivity"
    }
}