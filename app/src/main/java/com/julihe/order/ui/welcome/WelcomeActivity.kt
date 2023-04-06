package com.julihe.order.ui.welcome

import android.content.Context
import android.widget.TextView
import android.os.Bundle
import com.julihe.order.util.sp.SpUtil
import android.text.TextUtils
import com.julihe.order.util.MainThreadHandler
import android.content.Intent
import android.media.MediaRouter
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.julihe.order.BuildConfig
import com.julihe.order.R
import com.julihe.order.databinding.ActivityWelcomeBinding
import com.julihe.order.ui.BaseActivity
import com.julihe.order.ui.login.LoginActivity
import com.julihe.order.ui.bind.BindActivity
import com.julihe.order.ui.choose.ChooseModeActivity
import com.julihe.order.ui.meal.SetMealActivity
import com.julihe.order.ui.order.SingleActivity
import com.julihe.order.ui.simple.SimpleActivity

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

        viewModel?.config?.observe(this) {
            if (it != null) {
                if (it.orderMode != null) {
                    jumpActivity(if (ChooseModeActivity.MODE_TSLDXJ == it.orderMode) SingleActivity::class.java
                        else if (ChooseModeActivity.MODE_QCMS == it.orderMode) SetMealActivity::class.java else SimpleActivity::class.java)
                } else {
                    jumpActivity(ChooseModeActivity::class.java)
                }
            } else {
                val intent = Intent(this, BindActivity::class.java)
                intent.putExtra("data", LoginActivity.START)
                startActivity(intent)
                finish()
            }
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