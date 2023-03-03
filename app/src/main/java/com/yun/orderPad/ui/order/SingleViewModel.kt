package com.yun.orderPad.ui.order

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.yun.orderPad.model.request.*
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

    private val _student = MutableLiveData<Student?>()
    val student: LiveData<Student?> = _student

    private val _listMenu = MutableLiveData<List<MealMenu>?>()
    val listMenu: LiveData<List<MealMenu>?> = _listMenu

    private val _choosed = MutableLiveData<List<MealMenu>?>()
    val choosed: LiveData<List<MealMenu>?> = _choosed

    private val _commit = MutableLiveData<Boolean>()
    val commit: LiveData<Boolean?> = _commit

    private val _scan = MutableLiveData<Boolean>()
    val scan: LiveData<Boolean?> = _scan

    fun setCommit(boolean: Boolean) {
       _commit.postValue(boolean)
    }

    fun doScan(boolean: Boolean) {
        _scan.postValue(boolean)
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

    /**
     * 获取当前餐次信息
     */
    fun getCurrentMeal() {
        viewModelScope.launch {
            val result: NetResult<Meal?> = OrderRepository.instance.getCurrentMeal()
            if (result is NetResult.Success) {
                if (result.data != null) {

                    val mode = Meal("17:00","11:00","午餐","lunch")
                    _currentMeal.postValue(mode)
//                    val meal = result.data
//                    LogUtil.d(TAG,"getCurrentMeal ${JSON.toJSONString(meal)}")
//                    _currentMeal.postValue(meal)
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

    /**
     * 获取菜单信息
     */
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
     * 获取学生信息  uid:2088520423158772 无信息未找到用户信息，请重新尝试或联系负责人！
     */
    fun getStudentInfo(faceId: String?) {
        viewModelScope.launch {
            val result: NetResult<Student?> = OrderRepository.instance.getStudentByFaceUid(FaceInfo(faceId))
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val student = result.data
                    LogUtil.d(TAG,"getStudentInfo ${JSON.toJSONString(student)}")
                    _student.postValue(student)
                } else {
                    LogUtil.d("未获取到该学生信息")
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getStudentInfo ${result.exception}")
            }
        }
    }

    /**
     * 提交学生订单
     */
    fun submitMealOrder() {
        viewModelScope.launch {
            val list = mutableListOf<MealOrderDetail>()
//            choosed.value?.get(0)?.let {
//                list.add(MealOrderDetail(it.dishSkuId,it.dishSkuName,it.price, 2))
//            }
//            choosed.value?.get(1)?.let {
//                list.add(MealOrderDetail(it.dishSkuId,it.dishSkuName,it.price, 2))
//            }
            list.add(MealOrderDetail(listMenu.value?.get(0)?.dishSkuId,listMenu.value?.get(0)?.dishSkuName,listMenu.value?.get(0)?.price, 2))
            list.add(MealOrderDetail(listMenu.value?.get(1)?.dishSkuId,listMenu.value?.get(1)?.dishSkuName,listMenu.value?.get(1)?.price, 2))
            //无餐次ID
            val mealOrder = MealOrder(list, currentMeal.value?.mealTableCode, currentMeal.value?.mealTableName,student.value?.id)
            val result: NetResult<Boolean?> = OrderRepository.instance.submitMealOrder(mealOrder)
            if (result is NetResult.Success) {
                if (result.data != null && result.data == true) {
                    LogUtil.d(TAG,"submitMealOrder ${JSON.toJSONString(result.data)}")
                    LogUtil.d("取餐成功")
                } else {
                    LogUtil.d("取餐失败")
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getCurrentMeal ${result.exception}")
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