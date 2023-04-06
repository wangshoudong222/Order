package com.julihe.order.ui.order

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
import com.julihe.order.ui.order.fragment.SingleConfirmFragment
import com.julihe.order.ui.order.fragment.SingleOrderFragment
import com.julihe.order.ui.order.fragment.SinglePayErrorFragment
import com.julihe.order.ui.order.fragment.SinglePayFragment
import com.julihe.order.ui.order.presentation.ConfirmPresentation
import com.julihe.order.ui.order.presentation.SinglePresentation
import com.julihe.order.ui.setting.SettingsActivity
import com.julihe.order.ui.simple.SimpleActivity
import com.julihe.order.util.LogUtil
import com.julihe.order.util.MainThreadHandler
import com.julihe.order.util.ToastUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 零点模式
 */
class SingleActivity : AppCompatActivity(), SmileManager.OnInstallResultListener,
    SmileManager.OnScanFaceResultListener{

    private lateinit var binding : ActivitySingleBinding
    private lateinit var viewModel: SingleViewModel
    private lateinit var orderFragment: SingleOrderFragment
    private lateinit var confirmFragment: SingleConfirmFragment
    private lateinit var paySFragment: SinglePayFragment
    private lateinit var payErrorFragment: SinglePayErrorFragment

    private var mSmileManager: SmileManager? = null
    private var curPre = ORDER_PRE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)
        initView()
        initViewModel()
        initSmile()
        initFragment(savedInstanceState)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SingleViewModel::class.java)
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

        viewModel.mealError.observe(this) {
            if (it == true) {
                // 获取不到餐次信息，每60S获取一次
                MainThreadHandler.removeCallbacks(TAG_MEAL_ERROR)
                MainThreadHandler.postDelayed(TAG_MEAL_ERROR, {
                    viewModel.getCurrentMeal()
                },1000 * 60)
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
                }

                COMMIT_STATE.SCANNING -> {
                    LogUtil.d(TAG,"正在扫脸")
                }

                COMMIT_STATE.SUCCESS -> {
                    ToastUtil.show("取餐成功")
                    addPresentation(CONFIRM_PRE)
                    replaceFragment(paySFragment)
                }

                COMMIT_STATE.ERROR -> {
                    ToastUtil.show("取餐失败")
                    addPresentation(CONFIRM_PRE)
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
            this.finish()
        }
        addPresentation(curPre)
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        orderFragment = SingleOrderFragment.newInstance()
        confirmFragment = SingleConfirmFragment.newInstance()
        paySFragment = SinglePayFragment.newInstance()
        payErrorFragment = SinglePayErrorFragment.newInstance()

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
            } else if (curPre == CONFIRM_PRE && confirmPre != null) {
                return confirmPre?.dispatchKeyEvent(event) == true
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
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(SetMealActivity.TAG, "onDestroy")
        mSmileManager?.onDestroy(TYPE)
        EventBus.getDefault().unregister(this)
        MainThreadHandler.removeCallbacks(TAG_MEAL_ERROR)
    }

    /**
     * 副屏相关
     */
    private var orderPre: SinglePresentation? = null
    private var confirmPre: ConfirmPresentation? = null

    private fun addPresentation(pre: String){
        if (pre == ORDER_PRE && (orderPre == null || orderPre?.isShowing == false)) {
            val mediaRouter: MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
            val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
            if (route != null && route.presentationDisplay != null) {
                orderPre = SinglePresentation(this,route.presentationDisplay)
                orderPre?.setOwnerActivity(this)
            }
            orderPre?.show()
            confirmPre?.dismiss()
            confirmPre = null
        } else if (pre == CONFIRM_PRE && (confirmPre == null || confirmPre?.isShowing == false)){
            val mediaRouter: MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
            val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
            if (route != null && route.presentationDisplay != null) {
                confirmPre = ConfirmPresentation(this,route.presentationDisplay)
                confirmPre?.setOwnerActivity(this)
            }
            confirmPre?.show()
            orderPre?.dismiss()
            orderPre = null
        }
        curPre = pre
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun confirmOrder(event: ConfirmEvent) {
        if (confirmFragment.isAdded && confirmFragment.isVisible) {
            return
        }
        replaceFragment(confirmFragment)
    }

    companion object {
        const val TAG = "SingleActivity"
        const val ORDER_PRE = "ORDER_PRE"
        const val CONFIRM_PRE = "CONFIRM_PRE"
        const val TAG_MEAL_ERROR = "SingleActivity_TAG_MEAL_ERROR"

        var TYPE = SmileManager.SCAN_TYPE_APPROACH_SINGLE
    }
}