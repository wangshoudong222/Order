package com.julihe.order.ui.meal

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaRouter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.julihe.order.databinding.ActivitySetMealBinding
import com.julihe.order.smile.IsvInfo
import com.julihe.order.smile.SmileManager
import com.julihe.order.ui.meal.dialog.ErrorDialog
import com.julihe.order.ui.meal.dialog.SuccessDialog
import com.julihe.order.ui.meal.presentation.MealConfirmPresentation
import com.julihe.order.ui.meal.presentation.WaitPresentation
import com.julihe.order.ui.order.SingleActivity
import com.julihe.order.ui.setting.SettingsActivity
import com.julihe.order.ui.simple.SimpleActivity
import com.julihe.order.util.LogUtil
import com.julihe.order.util.MainThreadHandler
import com.julihe.order.util.ToastUtil

/**
 * 包餐取餐页面
 */
class SetMealActivity : AppCompatActivity(), SmileManager.OnInstallResultListener,
    SmileManager.OnScanFaceResultListener, ErrorDialog.OnClickClose, DialogInterface.OnKeyListener {

    private lateinit var binding : ActivitySetMealBinding
    private lateinit var viewModel: SetMealViewModel
    private var mSmileManager: SmileManager? = null
    private var errorDialog: ErrorDialog? = null
    private var successDialog: SuccessDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetMealBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initViewModel()
        initSmile()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SetMealViewModel::class.java)
        viewModel.getConfig()
        viewModel.getCurrentMeal()

        viewModel.scanError.observe(this) {
            if (it != null) {
                ToastUtil.show("人脸识别失败$it")
            }
        }

        viewModel.student.observe(this) {
            if (it != null) {
                viewModel.getStudentPackageMeal()
            }
        }

        viewModel.orders.observe(this) {
            if (it != null && it.isNotEmpty()) {
                showSuccessDialog()
            }
        }

        viewModel.selfOrder.observe(this) {
            if (it != null && it.isNotEmpty()) {
                addPresentation(CONFIRM_PRE)
            }
        }

        viewModel.currentMeal.observe(this) {
            binding.meal.text = it?.mealTableName
            binding.time.text = it?.mealStartTime + "~" + it?.mealEndTime
        }

        viewModel.config.observe(this) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.setCanteen.text = it.kitchenName
                binding.setWindow1.text = it.windowName
                binding.setMealTitle.titleName.text = it.schoolName + " " + it.windowName
            }
        }

        viewModel.scanError.observe(this) {
            if (it != null) {
                showErrorDialog(it)
            }
        }

        viewModel.studentError.observe(this) {
            if (it != null) {
                showErrorDialog(it)
            }
        }

        viewModel.selfOrderError.observe(this) {
            if (it != null) {
                showErrorDialog(it?.msg)
            }
        }

        viewModel.orderError.observe(this) {
            if (it != null) {
                showErrorDialog(it?.msg)
            }
        }

        viewModel.confirmError.observe(this) {
            if (it != null) {
                showErrorDialog(it?.msg)
            }
        }

        viewModel.confirmState.observe(this) {
            if (it == true) {
                MainThreadHandler.postDelayed({
                    closeSuccessDialog()
                    addPresentation(WAIT_PRE)
                    viewModel.cleanState()
                },5000)
            }
        }

        viewModel.mealError.observe(this) {
            if (it == true) {
                MainThreadHandler.removeCallbacks(TAG_MEAL_ERROR)
                // 获取不到餐次信息，每60S获取一次
                MainThreadHandler.postDelayed(TAG_MEAL_ERROR, {
                    viewModel.getCurrentMeal()
                },1000 * 60)
            }
        }
    }

    private fun showErrorDialog(it: String?) {
        if (it == null) return
        if (errorDialog == null) {
            errorDialog = ErrorDialog(it, this)
        } else {
            if (errorDialog?.isShowing == true) {
                errorDialog?.dismiss()
            }
            errorDialog?.setContent(it)
        }
        errorDialog?.show(supportFragmentManager, "ERROR")

        MainThreadHandler.postDelayed("DisMiss",{
            closeErrorDialog()
        },3000)
    }

    private fun closeErrorDialog() {
        if (errorDialog?.isShowing == true) {
            errorDialog?.dismiss()
        }
    }

    private fun initView() {
        binding.setMealTitle.titleSetting.visibility = View.VISIBLE
        binding.setMealTitle.titleSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            this.finish()
        }

        binding.btnScan.setOnClickListener {
            startScan()
        }
        addPresentation(curPre)
    }

    private fun showSuccessDialog() {
        if (successDialog?.isShowing == true) return
        if (successDialog == null) {
            successDialog = SuccessDialog(this,viewModel,this)
        }
        successDialog?.show(supportFragmentManager, "SUCCESS")
    }

    private fun closeSuccessDialog() {
        if (successDialog?.isShowing == true) {
            successDialog?.dismiss()
        }
    }

    /**
     * 副屏相关
     */
    private var waitPre: WaitPresentation? = null
    private var confirmPre: MealConfirmPresentation? = null
    private var curPre = WAIT_PRE

    private fun addPresentation(pre: String){
        if (pre == WAIT_PRE) {
            val mediaRouter: MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
            val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
            if (route != null && route.presentationDisplay != null) {
                waitPre = WaitPresentation(this,route.presentationDisplay)
                waitPre?.setOwnerActivity(this)
            }
            waitPre?.show()
        } else {
            val mediaRouter: MediaRouter = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
            val route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO)
            if (route != null && route.presentationDisplay != null) {
                confirmPre = MealConfirmPresentation(this,route.presentationDisplay)
                confirmPre?.setOwnerActivity(this)
            }
            confirmPre?.show()
        }
        curPre = pre
    }

    private fun initSmile() {
        Log.d(TAG, "initSmileManger")
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
        LogUtil.d(TAG,if (success == true) "人脸识别成功 token:$fToken; uid:$uid" else "人脸识别失败")
        if (success == true) {
            viewModel.getStudentInfo(uid)
        } else {
            viewModel.setScanErrorMsg(uid)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {

        return confirmPre?.dispatchKeyEvent(event!!) == true
//        if (event != null) {
//            if (curPre == WAIT_PRE && waitPre != null) {
//                return waitPre?.dispatchKeyEvent(event) == true
//            } else if (curPre == CONFIRM_PRE && confirmPre != null) {
//                return confirmPre?.dispatchKeyEvent(event) == true
//            }
//        }
        return super.dispatchKeyEvent(event)
    }

    override fun onResume() {
        super.onResume()
        Log.w(TAG, "onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "onDestroy")
        mSmileManager?.onDestroy(TYPE)
        MainThreadHandler.removeCallbacks(TAG_MEAL_ERROR)
    }

    companion object {
        const val TAG = "SetMealActivity"
        var TYPE = SmileManager.SCAN_TYPE_APPROACH_SINGLE

        const val WAIT_PRE = "WAIT_PRE"
        const val CONFIRM_PRE = "CONFIRM_PRE"
        const val TAG_MEAL_ERROR = "Set_TAG_MEAL_ERROR"

    }

    override fun onCloseClick(view: View?) {
        closeErrorDialog()
    }

    override fun onKey(p0: DialogInterface?, p1: Int, event: KeyEvent?): Boolean {
        Log.w(TAG, "p1$p1, p2$event")
        if (event != null) {
            if (curPre == WAIT_PRE && waitPre != null) {
                return waitPre?.dispatchKeyEvent(event) == true
            } else if (curPre == CONFIRM_PRE && confirmPre != null) {
                return confirmPre?.dispatchKeyEvent(event) == true
            }
        }
        return true
    }
}