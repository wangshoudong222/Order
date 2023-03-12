package com.julihe.order.ui.setting

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.julihe.order.model.result.Config
import com.julihe.order.net.OrderRepository
import com.julihe.order.net.model.NetResult
import com.julihe.order.util.LogUtil
import com.julihe.order.util.sp.SpUtil
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config

    private val _configRequest = MutableLiveData<Boolean>()
    val configRequest: LiveData<Boolean> = _configRequest


    fun getConfig() {
        val s = SpUtil.config()
        if (!TextUtils.isEmpty(s)) {
            _config.postValue(JSON.parseObject(s, Config::class.java))
            _configRequest.postValue(true)
        } else {
            requestConfig()
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
        const val TAG = "SettingsViewModel"
    }
}