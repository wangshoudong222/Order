package com.julihe.orderPad.ui.bind

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.julihe.orderPad.model.request.ConfigInfo
import com.julihe.orderPad.model.request.KitchenRequest
import com.julihe.orderPad.model.result.Config
import com.julihe.orderPad.model.result.KitchenInfo
import com.julihe.orderPad.model.result.SchoolInfo
import com.julihe.orderPad.model.result.WindowInfo
import com.julihe.orderPad.net.OrderRepository
import com.julihe.orderPad.net.model.NetResult
import com.julihe.orderPad.util.LogUtil
import com.julihe.orderPad.util.ToastUtil
import com.julihe.orderPad.util.sp.SpUtil
import kotlinx.coroutines.launch

class BindViewModel : ViewModel() {

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config

    private val _configRequest = MutableLiveData<Boolean>()
    val configRequest: LiveData<Boolean> = _configRequest

    private val _saveState = MutableLiveData<Boolean?>()
    val saveState : LiveData<Boolean?> = _saveState

    private val _unBindState = MutableLiveData<Boolean?>()
    val unBindState : LiveData<Boolean?> = _unBindState

    private val _school = MutableLiveData<SchoolInfo>()
    val school: LiveData<SchoolInfo> = _school

    private val _kitchen = MutableLiveData<KitchenInfo>()
    val kitchen: LiveData<KitchenInfo> = _kitchen

    private val _window = MutableLiveData<WindowInfo>()
    val window: LiveData<WindowInfo> = _window

    private val _schools = MutableLiveData<List<SchoolInfo>?>()
    val schools: LiveData<List<SchoolInfo>?> = _schools

    private val _kitchens = MutableLiveData<List<KitchenInfo>?>()
    val kitchens: LiveData<List<KitchenInfo>?> = _kitchens

    private val _windows = MutableLiveData<List<WindowInfo>?>()
    val windows: LiveData<List<WindowInfo>?> = _windows

    fun getConfig(init: Boolean) {
        if (init) {
            val s = SpUtil.config()
            if (!TextUtils.isEmpty(s)) {
                _config.postValue(JSON.parseObject(s, Config::class.java))
                _configRequest.postValue(true)
            }
        }
        requestConfig()
    }

    fun chooseSchool(info: SchoolInfo) {
        _school.postValue(info)
    }

    fun chooseKitchen(info: KitchenInfo) {
        _kitchen.postValue(info)
    }

    fun chooseWindows(info: WindowInfo) {
        _window.postValue(info)
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

    /**
     * 获取学校信息
     */
    fun requestSchools() {
        viewModelScope.launch {
            val result: NetResult<List<SchoolInfo>?> = OrderRepository.instance.listSchool()
            if (result is NetResult.Success) {
                val listSchoolInfo = result.data
                LogUtil.d(TAG,"requestSchools ${JSON.toJSONString(listSchoolInfo)}")
                if (!TextUtils.isEmpty(JSON.toJSONString(listSchoolInfo))) {
                    _schools.postValue(listSchoolInfo)
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"requestSchools ${result.exception}")
            }
        }
    }

    /**
     * 获取食堂信息
     */
    fun requestKitchen(schoolInfo: SchoolInfo) {
        viewModelScope.launch {
            val result: NetResult<List<KitchenInfo>?> = OrderRepository.instance.listKitchen(schoolInfo)
            if (result is NetResult.Success) {
                val listKitchenInfo = result.data
                LogUtil.d(TAG,"requestKitchen ${JSON.toJSONString(listKitchenInfo)}")
                if (!TextUtils.isEmpty(JSON.toJSONString(listKitchenInfo))) {
                    _kitchens.postValue(listKitchenInfo)
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"requestKitchen ${result.exception}")
            }
        }
    }

    /**
     * 获取学校信息
     */
    fun requestWindows(request: KitchenRequest) {
        viewModelScope.launch {
            val result: NetResult<List<WindowInfo>?> = OrderRepository.instance.listWindow(request)
            if (result is NetResult.Success) {
                val listWindowInfo = result.data
                LogUtil.d(TAG,"requestWindows ${JSON.toJSONString(listWindowInfo)}")
                if (!TextUtils.isEmpty(JSON.toJSONString(listWindowInfo))) {
                    _windows.postValue(listWindowInfo)
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"requestWindows ${result.exception}")
            }
        }
    }

    fun saveConfig() {
        val config = ConfigInfo(
            null,null,school.value?.schoolId,
            school.value?.schoolName,kitchen.value?.kitchenId,
            kitchen.value?.kitchenName,
            window.value?.windowId,window.value?.windowName
        )
        viewModelScope.launch {
            val result: NetResult<Boolean?> = OrderRepository.instance.save(config)
            if (result is NetResult.Success) {
                _saveState.postValue(result.data)
                getConfig(false)
            } else if (result is NetResult.Error){
                _saveState.postValue(false)
                ToastUtil.show(result.exception.msg)
                LogUtil.d(TAG,"requestWindows ${result.exception}")
            }
        }
    }

    /**
     * 解绑设备
     */
    fun unBind() {
        viewModelScope.launch {
            val result: NetResult<Boolean?> = OrderRepository.instance.unbind()
            if (result is NetResult.Success) {
                if (result.data == true) {
                    SpUtil.config("")
                }
                _unBindState.postValue(result.data)
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"unBind ${result.exception}")
                _unBindState.postValue(false)
            }
        }
    }

    companion object {
        const val TAG = "BindViewModel"
    }
}