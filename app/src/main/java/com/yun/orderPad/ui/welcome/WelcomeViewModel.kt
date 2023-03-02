package com.yun.orderPad.ui.welcome

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.yun.orderPad.model.result.Config
import com.yun.orderPad.net.OrderRepository
import com.yun.orderPad.net.model.NetResult
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.sp.SpUtil
import kotlinx.coroutines.launch

class WelcomeViewModel: ViewModel() {

    private val _configRequest = MutableLiveData<Boolean>()
    val configRequest: LiveData<Boolean> = _configRequest

    /**
     * 获取配置信息
     */
    fun getConfig() {
        viewModelScope.launch {
            val result: NetResult<Config?> = OrderRepository.instance.getDeviceConfig()
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val config = result.data
                    LogUtil.d(TAG,"getConfig ${JSON.toJSONString(config)}")
                    if (!TextUtils.isEmpty(JSON.toJSONString(config))) {
                        SpUtil.config(JSON.toJSONString(config))
                        _configRequest.postValue(true)
                        return@launch
                    }
                }
                _configRequest.postValue(false)
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getConfig ${result.exception}")
                SpUtil.config("")
                _configRequest.postValue(false)
            }
        }
    }

    companion object {
        const val TAG = "WelcomeViewModel"
    }
}