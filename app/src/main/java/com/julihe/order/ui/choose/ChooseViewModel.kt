package com.julihe.order.ui.choose

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.julihe.order.model.request.ConfigInfo
import com.julihe.order.model.request.SchoolRequest
import com.julihe.order.model.result.*
import com.julihe.order.net.OrderRepository
import com.julihe.order.net.model.NetResult
import com.julihe.order.ui.simple.SimpleViewModel
import com.julihe.order.util.LogUtil
import com.julihe.order.util.ToastUtil
import com.julihe.order.util.sp.SpUtil
import kotlinx.coroutines.launch

class ChooseViewModel : ViewModel() {

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config

    private val _configRequest = MutableLiveData<Boolean>()
    val configRequest: LiveData<Boolean> = _configRequest

    private val _mode = MutableLiveData<OrderMode?>()
    val mode : LiveData<OrderMode?> = _mode

    private val _orderModes = MutableLiveData<List<OrderMode>?>()
    val orderModes: LiveData<List<OrderMode>?> = _orderModes

    fun getConfig() {
        val s = SpUtil.config()
        if (!TextUtils.isEmpty(s)) {
            _config.postValue(JSON.parseObject(s, Config::class.java))
            _configRequest.postValue(true)
        } else {
            requestConfig()
        }
    }

    fun getOrderModeList() {
        viewModelScope.launch {
            val result: NetResult<List<OrderMode>?> = OrderRepository.instance.getOrderModeList()
            if (result is NetResult.Success) {
                if (result.data != null && result.data.isNotEmpty()) {
                    val orderMode = result.data
                    LogUtil.d(TAG,"getOrderModeList ${JSON.toJSONString(orderMode)}")
                    _orderModes.postValue(orderMode)
                } else {
                    ToastUtil.show("当前没有配置取餐模式")
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

    fun setMode(mode: OrderMode?) {
        val config = ConfigInfo(
            mode?.orderMode,mode?.orderModeName,null,
            null,null,
            null,
            null,null
        )
        viewModelScope.launch {
            val result: NetResult<Boolean?> = OrderRepository.instance.save(config)
            if (result is NetResult.Success) {
                if (result.data == true) {
                    _mode.postValue(mode)
                } else {
                    ToastUtil.show("取餐模式设置失败")
                }
            } else if (result is NetResult.Error){
                ToastUtil.show(result.exception.msg)
                LogUtil.d(TAG,"requestWindows ${result.exception}")
            }
        }
    }
    
    companion object {
        const val TAG = "BindViewModel"
    }
}