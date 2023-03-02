package com.yun.orderPad.ui.order

import android.content.Context
import android.content.Intent
import android.media.MediaRouter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.R
import com.yun.orderPad.databinding.ActivitySetMealBinding
import com.yun.orderPad.databinding.ActivitySingleBinding
import com.yun.orderPad.smile.IsvInfo
import com.yun.orderPad.smile.SmileManager
import com.yun.orderPad.ui.bind.BindActivity
import com.yun.orderPad.ui.meal.SetMealActivity
import com.yun.orderPad.ui.meal.SetMealViewModel
import com.yun.orderPad.ui.order.fragment.SingleOrderFragment
import com.yun.orderPad.ui.setting.SettingsActivity
import com.yun.orderPad.ui.setting.SettingsPresentation
import com.yun.orderPad.ui.test.ui.main.TestFragment
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.ToastUtil

/**
 * 零点模式
 */
class SingleActivity : AppCompatActivity(), SmileManager.OnInstallResultListener,
    SmileManager.OnScanFaceResultListener{

    private lateinit var binding : ActivitySingleBinding
    private lateinit var viewModel: SingleViewModel
    private var mSmileManager: SmileManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addPresentation()
        initView()
        initViewModel()
        initSmile()
        initFragment(savedInstanceState)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SingleViewModel::class.java)
        viewModel.getCurrentMeal()
        viewModel.getConfig()

        viewModel.config.observe(this) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.setSingleTitle.titleName.text = it.schoolName + " " + it.windowName
            } else {
                ToastUtil.show("设备未绑定，请先绑定设备信息")
                startActivity(Intent(this, BindActivity::class.java))
                this.finish()
            }
        }

        viewModel.commit.observe(this) {
            if (it == true) {
                startActivity(Intent(this, SingleOrderActivity::class.java))
            }
        }
    }

    private fun initView() {
        binding.setSingleTitle.titleSetting.visibility = View.VISIBLE
        binding.setSingleTitle.titleSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SingleOrderFragment.newInstance())
                .commitNow()
        }
    }


    /**
     * 人脸识别相关
     */
    private fun initSmile() {
        Log.d(SetMealActivity.TAG, "initSmileManger")
        mSmileManager = SmileManager(IsvInfo.ISV_INFO, this)
        mSmileManager?.initScan(this)
    }

    private fun startScan() {
        mSmileManager?.startSmile(SetMealActivity.TYPE,this, this)
    }

    override fun onInstallResult(isSuccess: Boolean, errMsg: String?) {
        runOnUiThread {
            ToastUtil.show(if (isSuccess) "扫脸程序初始化成功" else "扫脸程序初始化失败:$errMsg")
        }
        LogUtil.d(SetMealActivity.TAG,if (isSuccess) "扫脸程序初始化成功" else "扫脸程序初始化失败:$errMsg")
    }

    override fun onVerifyResult(success: Boolean?, fToken: String?, uid: String?) {
        runOnUiThread{
            ToastUtil.show(if (success == true) "人脸识别成功" else "人脸识别失败")
        }
        LogUtil.d(SetMealActivity.TAG,if (success == true) "人脸识别成功 token:$fToken; uid:$uid" else "人脸识别失败")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(SetMealActivity.TAG, "onDestroy")
        mSmileManager?.onDestroy(SetMealActivity.TYPE)
    }

    /**
     * 副屏相关
     */
    private var presentationDisplay: SinglePresentation? = null

    private fun addPresentation(){
        val mediaRouter: MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
        val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
        if (route != null && route.presentationDisplay != null) {
            presentationDisplay = SinglePresentation(this,route.presentationDisplay)
            presentationDisplay?.setOwnerActivity(this)
            presentationDisplay?.show()
        }
    }

    companion object {
        const val TAG = "SingleActivity"
    }
}