package com.yun.orderPad.ui.order

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.yun.orderPad.model.COMMIT_STATE
import com.yun.orderPad.model.request.*
import com.yun.orderPad.model.result.*
import com.yun.orderPad.net.OrderRepository
import com.yun.orderPad.net.model.NetResult
import com.yun.orderPad.util.LogUtil
import com.yun.orderPad.util.sp.SpUtil
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SingleViewModel : ViewModel() {

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config

    private val _currentMeal = MutableLiveData<Meal?>()
    val currentMeal: LiveData<Meal?> = _currentMeal

    private val _student = MutableLiveData<Student?>()
    val student: LiveData<Student?> = _student

    private val _sum = MutableLiveData<String?>()
    val sum: LiveData<String?> = _sum

    private val _listMenu = MutableLiveData<List<MealMenu>?>()
    val listMenu: LiveData<List<MealMenu>?> = _listMenu

    private val _confirmOrder = MutableLiveData<List<MealMenu>?>()
    val confirmOrder: LiveData<List<MealMenu>?> = _confirmOrder

    private val _commitState = MutableLiveData<COMMIT_STATE>()
    val state: LiveData<COMMIT_STATE?> = _commitState

    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> = _errorMsg

    private val _scan = MutableLiveData<Boolean>()
    val scan: LiveData<Boolean?> = _scan

    private val _scanError = MutableLiveData<String?>()
    val scanError: LiveData<String?> = _scanError

    private val _totalMeals= MutableLiveData<Long?>()
    val totalMeals: LiveData<Long?> = _totalMeals

    private val _total = MutableLiveData<BigDecimal?>()
    val total: LiveData<BigDecimal?> = _total

    fun setTotal(bigDecimal: BigDecimal?) {
        _total.postValue(bigDecimal)
    }

    fun setTotalMeal(long: Long?) {
        _totalMeals.postValue(long)
    }

    private fun setErrorMsg(errorMsg: String?) {
        _errorMsg.postValue(errorMsg)
    }

    fun setScanErrorMsg(scanError: String?) {
        _scanError.postValue(scanError)
    }

    fun confirmOrder(list: List<MealMenu>?) {
       _confirmOrder.postValue(list)
    }

    fun doScan(boolean: Boolean) {
        _scan.postValue(boolean)
    }

    fun getConfig() {
        val s = SpUtil.config()
        if (!TextUtils.isEmpty(s)) {
            _config.postValue(JSON.parseObject(s, Config::class.java))
        } else {
            requestConfig()
        }
    }

    fun checkState(state: COMMIT_STATE) {
        _commitState.postValue(state)
    }

    /**
     * 获取当前餐次信息
     */
    fun getCurrentMeal() {
        viewModelScope.launch {
            val result: NetResult<Meal?> = OrderRepository.instance.getCurrentMeal()
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val meal = result.data
                    LogUtil.d(TAG,"getCurrentMeal ${JSON.toJSONString(meal)}")
                    _currentMeal.postValue(meal)
                } else {
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
            val mealOrder = MealOrder(confirmOrder.value, currentMeal.value?.mealTableCode,
                currentMeal.value?.mealTableName,student.value?.id)
            LogUtil.d(TAG,"submitMealOrder mealOrder: ${JSON.toJSONString(mealOrder)}")
            val result: NetResult<Boolean?> = OrderRepository.instance.submitMealOrder(mealOrder)
            if (result is NetResult.Success && result.data == true) {
                checkState(COMMIT_STATE.SUCCESS)
                LogUtil.d("取餐成功")
            } else if (result is NetResult.Error){
                getStudentAccount()
                LogUtil.d(TAG,"getCurrentMeal ${result.exception}")
                checkState(COMMIT_STATE.ERROR)
                setErrorMsg(result.exception.msg)
            }
        }
    }

    /**
     * 获取学生余额
     */
    private fun getStudentAccount() {
        viewModelScope.launch {
            val result: NetResult<BigDecimal?> = OrderRepository.instance.getStudentAccount(StudentRequest(student.value?.id))
            if (result is NetResult.Success) {
                if (result.data != null) {
                    _sum.postValue(result.data.toString())
                    LogUtil.d("getStudentAccount:${result.data}")
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getStudentAccount ${result.exception}")
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
                        return@launch
                    }
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getConfig ${result.exception}")
            }
        }
    }

    fun getMenuByCode(code: String): Int{
        var result = -1
        _listMenu.value?.forEachIndexed { index, mealMenu ->
            if (code == mealMenu.dishCode) {
                result = index
                return@forEachIndexed
            }
        }
        return result
    }


    fun reOrder() {
        getConfig()
        getCurrentMeal()
        _student.postValue(null)
        _sum.postValue(null)
        _confirmOrder.postValue(null)
        _scan.postValue(false)
        _errorMsg.postValue(null)
        _totalMeals.postValue(null)
        _total.postValue(null)
        _commitState.postValue(COMMIT_STATE.ORDER)
    }

    companion object {
        const val TAG = "SingleViewModel"
    }
}