package com.julihe.order.ui.simple

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.alipay.iot.sdk.APIManager
import com.julihe.order.model.COMMIT_STATE
import com.julihe.order.model.request.*
import com.julihe.order.model.result.*
import com.julihe.order.net.OrderRepository
import com.julihe.order.net.model.NetResult
import com.julihe.order.util.LogUtil
import com.julihe.order.util.sp.SpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class SimpleViewModel : ViewModel() {

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config

    private val _currentMeal = MutableLiveData<Meal?>()
    val currentMeal: LiveData<Meal?> = _currentMeal

    private val _student = MutableLiveData<Student?>()
    val student: LiveData<Student?> = _student

    private val _studentToken = MutableLiveData<String?>()
    val token: LiveData<String?> = _studentToken

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

    private val _commit = MutableLiveData<Boolean>()
    val commit: LiveData<Boolean?> = _commit

    private val _scanError = MutableLiveData<String?>()
    val scanError: LiveData<String?> = _scanError

    private val _mealError = MutableLiveData<Boolean?>()
    val mealError: LiveData<Boolean?> = _mealError

    private val _input = MutableLiveData<String?>()
    val input: LiveData<String?> = _input

    fun setInput(input: String?) {
        _input.postValue(input)
    }

    private fun setErrorMsg(errorMsg: String?) {
        _errorMsg.postValue(errorMsg)
    }

    fun setToken(token: String?) {
        _studentToken.postValue(token)
    }

    private fun setMealError(boolean: Boolean?) {
        _mealError.postValue(boolean)
    }

    fun setScanErrorMsg(scanError: String?) {
        _scanError.postValue(scanError)
    }

    fun confirmOrder() {
        if (_listMenu.value != null && _listMenu.value?.isNotEmpty() == true) {
            _listMenu.value!![0].price = BigDecimal(_input.value)
            _listMenu.value!![0].quantity = 1
            _confirmOrder.postValue(_listMenu.value)
        }
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
        LogUtil.d(TAG, "checkState:${state.name}")

        _commitState.postValue(state)
    }

    /**
     * 获取当前餐次信息
     */
    fun getCurrentMeal() {
        viewModelScope.launch {
            val result: NetResult<Meal?> = OrderRepository.instance.getCurrentMeal()
//            val mea = Meal("12:40","11:40","午餐","lunch")
//            _currentMeal.postValue(mea)
//            setMealError(false)

            if (result is NetResult.Success) {
                if (result.data != null) {
                    val meal = result.data
                    LogUtil.d(TAG,"getCurrentMeal ${JSON.toJSONString(meal)}")
                    _currentMeal.postValue(meal)
                    setMealError(false)
                } else {
                    setMealError(true)
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
     * 获取学生信息
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
                    _student.postValue(null)
                    checkState(COMMIT_STATE.ERROR)
                    setErrorMsg("未获取到该学生信息")
                }
            } else if (result is NetResult.Error){
                LogUtil.d(TAG,"getStudentInfo ${result.exception}")
                _student.postValue(null)
                checkState(COMMIT_STATE.ERROR)
                setErrorMsg(result.exception.msg)
            }
        }
    }

    /**
     * 提交学生订单
     */
    fun submitMealOrder() {
        viewModelScope.launch {
            val signature = getTokenSign()
            LogUtil.d(TAG, "signature:$signature")

            if (TextUtils.isEmpty(signature)) {
                checkState(COMMIT_STATE.ERROR)
                setErrorMsg("验签错误，取餐失败")
                return@launch
            }

            val mealOrder = MealOrderFace(confirmOrder.value, currentMeal.value?.mealTableCode,
                currentMeal.value?.mealTableName,student.value?.id, _studentToken.value, signature, student.value?.instId)
            LogUtil.d(TAG,"submitMealOrder mealOrder: ${JSON.toJSONString(mealOrder)}")
            val result: NetResult<String?> = OrderRepository.instance.submitMealOrderByAlipayFacePay(mealOrder)
            if (result is NetResult.Success) {
                checkState(COMMIT_STATE.SUCCESS)
                LogUtil.d("取餐成功，订单号:${result.data}")
            } else if (result is NetResult.Error){
                getStudentAccount()
                LogUtil.d(TAG,"submitMealOrder ${result.exception}")
                checkState(COMMIT_STATE.ERROR)
                setErrorMsg(result.exception.msg)
            }
        }
    }

    private suspend fun getTokenSign(): String? {
        var sign = ""
        withContext(Dispatchers.IO) {
            sign = APIManager.getInstance().paymentAPI.signWithFaceToken(token.value, _input.value)
        }
        return sign
    }

    /**
     * 获取学生余额
     */
    private fun getStudentAccount() {
        viewModelScope.launch {
            val result: NetResult<BigDecimal?> = OrderRepository.instance.getStudentAccount(StudentRequest(student.value?.studentNo))
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


    fun reOrder() {
        _student.postValue(null)
        _sum.postValue(null)
        _confirmOrder.postValue(null)
        _scan.postValue(false)
        _errorMsg.postValue(null)
        _studentToken.postValue(null)
        _commitState.postValue(COMMIT_STATE.ORDER)
        _input.postValue("0")
        getConfig()
        getCurrentMeal()
    }

    companion object {
        const val TAG = "SimpleViewModel"
    }
}