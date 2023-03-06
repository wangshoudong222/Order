package com.yun.orderPad.ui.meal

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.R
import com.yun.orderPad.databinding.ActivitySetMealBinding
import com.yun.orderPad.model.COMMIT_STATE
import com.yun.orderPad.smile.IsvInfo
import com.yun.orderPad.smile.SmileManager
import com.yun.orderPad.ui.bind.BindActivity
import com.yun.orderPad.ui.meal.fragment.MealConfirmFragment
import com.yun.orderPad.ui.meal.fragment.MealOrderFragment
import com.yun.orderPad.ui.order.fragment.SingleConfirmFragment
import com.yun.orderPad.ui.order.fragment.SingleOrderFragment
import com.yun.orderPad.ui.order.fragment.SinglePayErrorFragment
import com.yun.orderPad.ui.order.fragment.SinglePayFragment
import com.yun.orderPad.ui.setting.SettingsActivity
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.ToastUtil

/**
 * 包餐取餐页面
 */
class SetMealActivity : AppCompatActivity(), SmileManager.OnInstallResultListener,
    SmileManager.OnScanFaceResultListener {

    private lateinit var binding : ActivitySetMealBinding
    private lateinit var viewModel: SetMealViewModel
    private var mSmileManager: SmileManager? = null
    private var orderFragment: MealOrderFragment? = null
    private var confirmFragment: MealConfirmFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetMealBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initViewModel()
        initFragment(savedInstanceState)
        initSmile()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SetMealViewModel::class.java)
        viewModel.getCurrentMeal()
        viewModel.getConfig()

        viewModel.scan.observe(this){
            if (it == true) {
                startScan()
            }
        }

        viewModel.scanError.observe(this) {
            ToastUtil.show("人脸识别失败$it")
        }

        viewModel.student.observe(this) {

        }

    }

    private fun initView() {
        binding.setMealTitle.titleSetting.visibility = View.VISIBLE
        binding.setMealTitle.titleSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        orderFragment = MealOrderFragment.newInstance()
        confirmFragment = MealConfirmFragment.newInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, orderFragment!!)
                .commitNow()
        }
    }

    private fun replaceFragment(fm: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,fm)
            .commitAllowingStateLoss()
    }

    private fun initSmile() {
        Log.d(TAG, "initSmileManger")
        mSmileManager = SmileManager(IsvInfo.ISV_INFO, this)
        mSmileManager?.initScan(this)
    }

    private fun startScan() {
        mSmileManager?.startSmile(TYPE,this, this)
    }


    override fun onInstallResult(isSuccess: Boolean, errMsg: String?) {
        runOnUiThread {
            ToastUtil.show(if (isSuccess) "扫脸程序初始化成功" else "扫脸程序初始化失败:$errMsg")
        }
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

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "onDestroy")
        mSmileManager?.onDestroy(TYPE)
    }


    companion object {
        const val TAG = "SetMealActivity"
        var TYPE = SmileManager.SCAN_TYPE_APPROACH_SINGLE
    }
}