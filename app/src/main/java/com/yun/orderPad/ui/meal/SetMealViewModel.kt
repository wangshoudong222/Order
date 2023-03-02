package com.yun.orderPad.ui.meal

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.yun.orderPad.model.request.ConfigInfo
import com.yun.orderPad.model.result.*
import com.yun.orderPad.net.OrderRepository
import com.yun.orderPad.net.model.NetResult
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.util.sp.SpUtil
import kotlinx.coroutines.launch

class SetMealViewModel : ViewModel() {

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config

    private val _configRequest = MutableLiveData<Boolean>()
    val configRequest: LiveData<Boolean> = _configRequest

    private val _mode = MutableLiveData<OrderMode?>()
    val mode : LiveData<OrderMode?> = _mode

    private val _currentMeal = MutableLiveData<Meal?>()
    val currentMeal: LiveData<Meal?> = _currentMeal

    fun getConfig() {
        val s = SpUtil.config()
        if (!TextUtils.isEmpty(s)) {
            _config.postValue(JSON.parseObject(s, Config::class.java))
            _configRequest.postValue(true)
        } else {
            requestConfig()
        }
    }

    fun getCurrentMeal() {
        viewModelScope.launch {
            val result: NetResult<Meal?> = OrderRepository.instance.getCurrentMeal()
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val orderMode = result.data
                    LogUtil.d(TAG,"getCurrentMeal ${JSON.toJSONString(orderMode)}")
                    _currentMeal.postValue(orderMode)
                } else {
                    LogUtil.d("未获取到当前餐次信息")
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getConfig ${result.exception}")
            }
        }
    }

    /**
     * 获取配置信息
     */
    private fun requestConfig() {
        viewModelScope.launch {
            val result: NetResult<Config?> = OrderRepository.instance.getDeviceConfig()
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val config = result.data
                    LogUtil.d(TAG,"getConfig ${JSON.toJSONString(config)}")
                    if (!TextUtils.isEmpty(JSON.toJSONString(config))) {
                        SpUtil.config(JSON.toJSONString(config))
                        _config.postValue(config)
                        _configRequest.postValue(true)
                    }
                } else {
                    _configRequest.postValue(false)
                }

            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getConfig ${result.exception}")
            }
        }
    }
    
    companion object {
        const val TAG = "SetMealViewModel"
    }
}