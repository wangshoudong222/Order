package com.julihe.orderPad.ui.meal

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.julihe.orderPad.model.ErrorState
import com.julihe.orderPad.model.request.ConfigInfo
import com.julihe.orderPad.model.request.FaceInfo
import com.julihe.orderPad.model.request.MealRequest
import com.julihe.orderPad.model.request.Pickup
import com.julihe.orderPad.model.result.*
import com.julihe.orderPad.net.OrderRepository
import com.julihe.orderPad.net.exception.ResultException
import com.julihe.orderPad.net.model.NetResult
import com.julihe.orderPad.ui.order.SingleViewModel
import com.julihe.orderPad.util.CommonUtils
import com.julihe.orderPad.util.FileUtil
import com.julihe.orderPad.util.LogUtil
import com.julihe.orderPad.util.ToastUtil
import com.julihe.orderPad.util.sp.SpUtil
import kotlinx.coroutines.launch

class SetMealViewModel : ViewModel() {

    private val _config = MutableLiveData<Config?>()
    val config: LiveData<Config?> = _config

    private val _configRequest = MutableLiveData<Boolean>()
    val configRequest: LiveData<Boolean> = _configRequest

    private val _currentMeal = MutableLiveData<Meal?>()
    val currentMeal: LiveData<Meal?> = _currentMeal

    private val _student = MutableLiveData<Student?>()
    val student: LiveData<Student?> = _student

    private val _orders = MutableLiveData<List<OrderInfo>?>()
    val orders: LiveData<List<OrderInfo>?> = _orders

    private val _selfOrder = MutableLiveData<List<OrderInfo>?>()
    val selfOrder: LiveData<List<OrderInfo>?> = _selfOrder

    private val _orderError = MutableLiveData<ResultException?>()
    val orderError: LiveData<ResultException?> = _orderError

    private val _selfOrderError = MutableLiveData<ResultException?>()
    val selfOrderError: LiveData<ResultException?> = _selfOrderError

    private val _confirmState = MutableLiveData<Boolean?>()
    val confirmState: LiveData<Boolean?> = _confirmState

    private val _confirmError = MutableLiveData<ResultException?>()
    val confirmError: LiveData<ResultException?> = _confirmError

    private val _scan = MutableLiveData<Boolean>()
    val scan: LiveData<Boolean?> = _scan

    private val _scanError = MutableLiveData<String?>()
    val scanError: LiveData<String?> = _scanError

    private val _studentError = MutableLiveData<String?>()
    val studentError: LiveData<String?> = _studentError

    fun setScanErrorMsg(scanError: String?) {
        _scanError.postValue(scanError)
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
     * 获取餐次信息
     */
    fun getCurrentMeal() {
        viewModelScope.launch {
            val result: NetResult<Meal?> = OrderRepository.instance.getCurrentMeal()
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val orderMode = result.data
                    LogUtil.d(TAG,"getCurrentMeal ${JSON.toJSONString(orderMode)}")
                    _currentMeal.postValue(orderMode)
                } else {
                    LogUtil.d("未获取到当前餐次信息")
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

    /**
     * 获取学生信息
     */
    fun getStudentInfo(faceId: String?) {
        viewModelScope.launch {
            val result: NetResult<Student?> = OrderRepository.instance.getStudentByFaceUid(FaceInfo(faceId))
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val student = result.data
                    LogUtil.d(SingleViewModel.TAG,"getStudentInfo ${JSON.toJSONString(student)}")
                    _student.postValue(student)
                    return@launch
                } else {
                    LogUtil.d("未获取到该学生信息")
                }
            } else if (result is NetResult.Error){
                _studentError.postValue(result.exception.msg)
                LogUtil.d(SingleViewModel.TAG,"getStudentInfo ${result.exception}")
            }
        }
    }

    /**
     * 获取学生包餐信息
     */
    fun getStudentPackageMeal() {
        viewModelScope.launch {
            val result: NetResult<List<OrderInfo>?> = OrderRepository.instance.getStudentPackageMeal(
                MealRequest(currentMeal.value?.mealTableCode, student.value?.id))
            if (result is NetResult.Success) {
                if (result.data != null) {
                    val orders = result.data
                    LogUtil.d(TAG,"orders ${JSON.toJSONString(orders)}")
                    _orders.postValue(orders)
                    //过滤本窗口的数据
                    val filter = orders.filter {
                        it.orderDetailInfoList.any { orderDetailInfo -> orderDetailInfo.isSelfWindow }
                    }
                    _selfOrder.postValue(filter)
                    if (filter.isEmpty()) {
                        _selfOrderError.postValue(ResultException("-1", "本窗口无您要取的餐点，请去其他窗口领取"))
                    }
                    return@launch
                }
                _orderError.postValue(ResultException("0","获取包餐信息为空"))
            } else if (result is NetResult.Error){
                LogUtil.d(SingleViewModel.TAG,"getStudentInfo ${result.exception}")
            }
        }
    }

    /**
     * 提交学生包餐信息
     */
    fun confirmPickUp() {
        viewModelScope.launch {
            val pickOder = selfOrder.value?.map { it.orderNo }
            val result: NetResult<Boolean?> = OrderRepository.instance.confirmPickUp(Pickup(pickOder,student.value?.id))
            if (result is NetResult.Success) {
                if (result.data != null && result.data == true) {
                    LogUtil.d(TAG,"confirmPickUp 成功")
                    val time = CommonUtils.formatToDate(System.currentTimeMillis())
                    selfOrder.value?.forEach {
                        it.orderDetailInfoList.forEach { info ->
                            info.stateName = "已取餐"
                            info.isWaitingPickUp = false
                            info.confirmTime = time
                        }
                    }
                    _confirmState.postValue(result.data)
                    return@launch
                }
                _confirmError.postValue(ResultException("0","提交学生包餐信息错误"))
            } else if (result is NetResult.Error){
                _confirmError.postValue(result.exception)
                LogUtil.d(SingleViewModel.TAG,"提交学生包餐信息错误 ${result.exception}")
            }
        }
    }

    fun cleanState () {
        _student.postValue(null)
        _orders.postValue(null)
        _selfOrder.postValue(null)
        _orderError.postValue(null)
        _confirmState.postValue(false)
        _selfOrderError.postValue(null)
        _confirmError.postValue(null)
        _scanError.postValue(null)
        _studentError.postValue(null)
    }


    companion object {
        const val TAG = "SetMealViewModel"
    }
}