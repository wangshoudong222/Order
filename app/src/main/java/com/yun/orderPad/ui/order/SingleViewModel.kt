package com.yun.orderPad.ui.order

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.yun.orderPad.model.request.ConfigInfo
import com.yun.orderPad.model.request.MealTableRequest
import com.yun.orderPad.model.result.*
import com.yun.orderPad.net.OrderRepository
import com.yun.orderPad.net.model.NetResult
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.ToastUtil
import com.yun.orderPad.util.sp.SpUtil
import kotlinx.coroutines.launch

class SingleViewModel : ViewModel() {

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config

    private val _configRequest = MutableLiveData<Boolean>()
    val configRequest: LiveData<Boolean> = _configRequest

    private val _currentMeal = MutableLiveData<Meal?>()
    val currentMeal: LiveData<Meal?> = _currentMeal

    private val _listMenu = MutableLiveData<List<MealMenu>?>()
    val listMenu: LiveData<List<MealMenu>?> = _listMenu

    private val _choosed = MutableLiveData<List<MealMenu>?>()
    val choosed: LiveData<List<MealMenu>?> = _choosed

    private val _commit = MutableLiveData<Boolean>()
    val commit: LiveData<Boolean?> = _commit

    fun setCommit(boolean: Boolean) {
       _commit.postValue(boolean)
    }
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
                    val meal = result.data
                    LogUtil.d(TAG,"getCurrentMeal ${JSON.toJSONString(meal)}")
                    _currentMeal.postValue(meal)
                } else {
                    val mode = Meal("23:00","18:00","晚餐","dinner")
                    _currentMeal.postValue(mode)
                    LogUtil.d("未获取到当前餐次信息")
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getCurrentMeal ${result.exception}")
            }
        }
    }

    fun getMealMenuList() {
        viewModelScope.launch {
            val result: NetResult<List<MealMenu>?> = OrderRepository.instance.
                getMealMenuList(MealTableRequest(currentMeal.value?.mealTableCode))
            if (result is NetResult.Success) {
                if (result.data != null && result.data.isNotEmpty()) {
                    val list = result.data
                    LogUtil.d(TAG,"getMealMenuList ${JSON.toJSONString(list)}")
                    _listMenu.postValue(list)
                } else {
                    LogUtil.d("未获取到菜单信息")
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getMealMenuList ${result.exception}")
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
        const val TAG = "SingleViewModel"
    }
}