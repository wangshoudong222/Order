package com.yun.orderPad.ui.meal

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.yun.orderPad.databinding.ActivitySetMealBinding
import com.yun.orderPad.smile.IsvInfo
import com.yun.orderPad.smile.SmileManager
import com.yun.orderPad.ui.bind.BindActivity
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
        viewModel.getCurrentMeal()
        viewModel.getConfig()

        viewModel.config.observe(this) {
            if (it != null && !TextUtils.isEmpty(it.schoolName) && !TextUtils.isEmpty(it.windowName)) {
                binding.setMealTitle.titleName.text = it.schoolName + " " + it.windowName
                binding.setCanteen.text = it.kitchenName
                binding.setWindow1.text = it.windowName
            } else {
                ToastUtil.show("设备未绑定，请先绑定设备信息")
                startActivity(Intent(this, BindActivity::class.java))
                this.finish()
            }
        }

        viewModel.currentMeal.observe(this) {
            binding.meal.text = it?.mealTableName
            binding.time.text = it?.mealStartTime + "~" + it?.mealEndTime
        }
    }

    private fun initView() {
        binding.setMealTitle.titleSetting.visibility = View.VISIBLE
        binding.setMealTitle.titleSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnNfc.setOnClickListener {

        }

        binding.btnFace.setOnClickListener {
            startScan()
        }
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
        runOnUiThread{
            ToastUtil.show(if (success == true) "人脸识别成功" else "人脸识别失败")
        }
        LogUtil.d(TAG,if (success == true) "人脸识别成功 token:$fToken; uid:$uid" else "人脸识别失败")
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