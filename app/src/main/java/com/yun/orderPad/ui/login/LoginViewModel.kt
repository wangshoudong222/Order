package com.yun.orderPad.ui.login

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.yun.orderPad.model.request.LoginRequest
import com.yun.orderPad.model.result.Config
import com.yun.orderPad.model.result.LoginModel
import com.yun.orderPad.net.OrderRepository
import com.yun.orderPad.net.model.NetResult
import com.yun.orderPad.ui.choose.ChooseViewModel
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.sp.SpUtil
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val _loginResult = MutableLiveData<LoginModel?>()
    val loginResult: LiveData<LoginModel?> = _loginResult

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config


    /**
     * 登录
     */
    fun login(username:String, password: String) {
        viewModelScope.launch {
            val result: NetResult<LoginModel?> = OrderRepository.instance.login(LoginRequest(username, password))
            if (result is NetResult.Success) {
                _loginResult.postValue(result.data)
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"login ${result.exception}")
                _loginError.postValue(result.exception.msg)
            }
        }
    }

    /**
     * 获取配置信息
     */
    fun requestConfig() {
        viewModelScope.launch {
            val result: NetResult<Config?> = OrderRepository.instance.getDeviceConfig()
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val config = result.data
                    LogUtil.d(TAG,"getConfig ${JSON.toJSONString(config)}")
                    if (!TextUtils.isEmpty(JSON.toJSONString(config))) {
                        SpUtil.config(JSON.toJSONString(config))
                        _config.postValue(config)
                        return@launch
                    }
                }
                _config.postValue(null)
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getConfig ${result.exception}")
            }
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}