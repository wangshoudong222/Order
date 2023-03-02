package com.yun.orderPad.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yun.orderPad.model.request.LoginRequest
import com.yun.orderPad.model.result.LoginModel
import com.yun.orderPad.net.OrderRepository
import com.yun.orderPad.net.model.NetResult
import com.yun.orderPad.util.LogUtil
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val _loginResult = MutableLiveData<LoginModel?>()
    val loginResult: LiveData<LoginModel?> = _loginResult

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

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

    companion object {
        const val TAG = "LoginViewModel"
    }
}