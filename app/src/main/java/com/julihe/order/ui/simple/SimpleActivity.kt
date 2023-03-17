package com.julihe.order.ui.simple

import android.content.Context
import android.content.Intent
import android.media.MediaRouter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.julihe.order.R
import com.julihe.order.databinding.ActivitySingleBinding
import com.julihe.order.event.ConfirmEvent
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.smile.IsvInfo
import com.julihe.order.smile.SmileManager
import com.julihe.order.ui.bind.BindActivity
import com.julihe.order.ui.meal.SetMealActivity
import com.julihe.order.ui.order.SingleActivity
import com.julihe.order.ui.order.fragment.SingleConfirmFragment
import com.julihe.order.ui.order.fragment.SingleOrderFragment
import com.julihe.order.ui.order.fragment.SinglePayErrorFragment
import com.julihe.order.ui.order.fragment.SinglePayFragment
import com.julihe.order.ui.order.presentation.ConfirmPresentation
import com.julihe.order.ui.order.presentation.SinglePresentation
import com.julihe.order.ui.setting.SettingsActivity
import com.julihe.order.ui.simple.fragment.SimpleConfirmFragment
import com.julihe.order.ui.simple.fragment.SimpleOrderFragment
import com.julihe.order.ui.simple.fragment.SimplePayErrorFragment
import com.julihe.order.ui.simple.fragment.SimplePaySuccessFragment
import com.julihe.order.ui.simple.presentation.SimplePayPresentation
import com.julihe.order.ui.simple.presentation.SimplePresentation
import com.julihe.order.util.LogUtil
import com.julihe.order.util.MainThreadHandler
import com.julihe.order.util.ToastUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 简约模式
 */
class SimpleActivity : AppCompatActivity(), SmileManager.OnInstallResultListener,
    SmileManager.OnScanFaceResultListener{

    private lateinit var binding : ActivitySingleBinding
    private lateinit var viewModel: SimpleViewModel
    private lateinit var orderFragment: SimpleOrderFragment
    private lateinit var confirmFragment: SimpleConfirmFragment
    private lateinit var paySuccessFragment: SimplePaySuccessFragment
    private lateinit var payErrorFragment: SimplePayErrorFragment

    private var mSmileManager: SmileManager? = null
    private var curPre = ORDER_PRE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initViewModel()
        initSmile()
        initFragment(savedInstanceState)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SimpleViewModel::class.java)
        viewModel.getCurrentMeal()
        viewModel.getConfig()
        viewModel.checkState(COMMIT_STATE.ORDER)
        viewModel.config.observe(this) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.setSingleTitle.titleName.text = it.schoolName + " " + it.windowName
            } else {
                ToastUtil.show("设备未绑定，请先绑定设备信息")
                startActivity(Intent(this, BindActivity::class.java))
                this.finish()
            }
        }
        viewModel.currentMeal.observe(this) {
            LogUtil.d(SinglePresentation.TAG, "SingleActivity currentMeal变化")
            if (it != null) {
                viewModel.getMealMenuList()
            }
        }

        viewModel.mealError.observe(this) {
            if (it == true) {
                // 获取不到餐次信息，每60S获取一次
                MainThreadHandler.removeCallbacks(TAG_MEAL_ERROR)
                MainThreadHandler.postDelayed(TAG_MEAL_ERROR, {
                    viewModel.getCurrentMeal()
                },1000 * 60)
            }
        }

        viewModel.scan.observe(this) {
            if (it == true) {
                viewModel.checkState(COMMIT_STATE.SCANNING)
                startScan()
            }
        }

        viewModel.student.observe(this) {
            it?.let {
                viewModel.submitMealOrder()
            }
        }

        viewModel.state.observe(this) {
            when(it) {
                COMMIT_STATE.ORDER -> {
                    LogUtil.d(TAG,"正在点餐")
                }

                COMMIT_STATE.REORDER -> {
                    LogUtil.d(TAG,"重新点餐")
                    viewModel.reOrder()
                    replaceFragment(orderFragment)
                    addPresentation(ORDER_PRE)
                }

                COMMIT_STATE.COMMITTING -> {
                    LogUtil.d(TAG,"正在提交")
                    replaceFragment(confirmFragment)
                }

                COMMIT_STATE.SCANNING -> {
                    LogUtil.d(TAG,"正在扫脸")
                }

                COMMIT_STATE.SUCCESS -> {
                    ToastUtil.show("取餐成功")
                    addPresentation(PAY_PRE)
                    replaceFragment(paySuccessFragment)
                }

                COMMIT_STATE.ERROR -> {
                    ToastUtil.show("取餐失败")
                    addPresentation(PAY_PRE)
                    replaceFragment(payErrorFragment)
                }

                else -> {

                }
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
        orderFragment = SimpleOrderFragment.newInstance()
        confirmFragment = SimpleConfirmFragment.newInstance()
        paySuccessFragment = SimplePaySuccessFragment.newInstance()
        payErrorFragment = SimplePayErrorFragment.newInstance()


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, orderFragment)
                .commitNow()
        }
    }

    private fun replaceFragment(fm: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,fm)
            .commitAllowingStateLoss()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event != null) {
            if (curPre == ORDER_PRE && orderPre != null) {
                return orderPre?.dispatchKeyEvent(event) == true
            } else if (curPre == PAY_PRE && payPre != null) {
                return payPre?.dispatchKeyEvent(event) == true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    /**
     * 人脸识别相关
     */
    private fun initSmile() {
        LogUtil.d(TAG, "initSmileManger")
        mSmileManager =
            SmileManager(IsvInfo.ISV_INFO, this)
        mSmileManager?.initScan(this)
    }

    private fun startScan() {
        mSmileManager?.startSmile(TYPE,this, this)
    }

    override fun onInstallResult(isSuccess: Boolean, errMsg: String?) {
        LogUtil.d(TAG,if (isSuccess) "扫脸程序初始化成功" else "扫脸程序初始化失败:$errMsg")
    }

    override fun onVerifyResult(success: Boolean?, fToken: String?, uid: String?) {
        runOnUiThread{
            ToastUtil.show(if (success == true) "人脸识别成功" else "人脸识别失败")
        }
        LogUtil.d(TAG,if (success == true) "人脸识别成功 token:$fToken; uid:$uid" else "人脸识别失败")
        if (success == true) {
            viewModel.getStudentInfo(uid)
        } else {
            viewModel.checkState(COMMIT_STATE.SCAN_ERROR)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        addPresentation(curPre)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSmileManager?.onDestroy(TYPE)
        MainThreadHandler.removeCallbacks(TAG_MEAL_ERROR)
        Log.w(TAG, "onDestroy")
    }

    /**
     * 副屏相关
     */
    private var orderPre: SimplePresentation? = null
    private var payPre: SimplePayPresentation? = null

    private fun addPresentation(pre: String){
        if (pre == ORDER_PRE && (orderPre == null || orderPre?.isShowing == false)) {
            val mediaRouter: MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
            val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
            if (route != null && route.presentationDisplay != null) {
                orderPre = SimplePresentation(this,route.presentationDisplay)
                orderPre?.setOwnerActivity(this)
            }
            orderPre?.show()
            payPre?.dismiss()
            payPre = null
        } else if (pre == PAY_PRE && (payPre == null || payPre?.isShowing == false)){
            val mediaRouter: MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
            val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
            if (route != null && route.presentationDisplay != null) {
                payPre = SimplePayPresentation(this,route.presentationDisplay)
                payPre?.setOwnerActivity(this)
            }
            payPre?.show()
            orderPre?.dismiss()
            orderPre = null
        }
        curPre = pre
    }

    companion object {
        const val TAG = "SimpleActivity"
        const val TAG_MEAL_ERROR = "TAG_MEAL_ERROR"
        const val ORDER_PRE = "ORDER_PRE"
        const val PAY_PRE = "PAY_PRE"

        var TYPE = SmileManager.SCAN_TYPE_APPROACH_SINGLE
    }
}