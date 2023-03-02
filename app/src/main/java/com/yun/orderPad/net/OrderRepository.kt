package com.yun.orderPad.net

import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import com.yun.orderPad.model.request.*
import com.yun.orderPad.model.result.*
import com.yun.orderPad.net.model.NetResult
import com.yun.orderPad.net.service.BaseRepository
import com.yun.orderPad.net.service.RetrofitClient
import okhttp3.MediaType
import okhttp3.RequestBody


class OrderRepository(private val service: RetrofitClient) : BaseRepository() {

    companion object {
        val instance: OrderRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OrderRepository(RetrofitClient.instance)
        }

        var TAG = "OrderRepository"
    }

    suspend fun getDeviceConfig(): NetResult<Config?> {
        return callRequest(call = { requestDeviceConfig() })
    }

    suspend fun getCurrentMeal(): NetResult<Meal?> {
        return callRequest(call = { requestCurrentMeal() })
    }

    suspend fun getOrderModeList(): NetResult<List<OrderMode>?> {
        return callRequest(call = { requestOrderModeList() })
    }

    suspend fun queryMealTableInfo(): NetResult<List<TableInfo>?> {
        return callRequest(call = { requestMealTableInfo() })
    }

    suspend fun listSchool(): NetResult<List<SchoolInfo>?> {
        return callRequest(call = { requestListSchool() })
    }

    suspend fun unbind(): NetResult<Boolean?> {
        return callRequest(call = { requestUnbind() })
    }

    suspend fun logout(): NetResult<Boolean?> {
        return callRequest(call = { requestLogout() })
    }

    suspend fun login(loginRequest: LoginRequest): NetResult<LoginModel?> {
        return callRequest(call = { requestLogin(getRequestBody(JSON.toJSONString(loginRequest))) })
    }

    suspend fun save(configInfo: ConfigInfo): NetResult<Boolean?> {
        return callRequest(call = { requestSave(getRequestBody(JSON.toJSONString(configInfo))) })
    }

    suspend fun confirmPickUp(pickUp: Pickup): NetResult<Boolean?> {
        return callRequest(call = { requestConfirmPickUp(getRequestBody(JSON.toJSONString(pickUp))) })
    }

    suspend fun getStudentByFaceUid(faceInfo: FaceInfo): NetResult<Student?> {
        return callRequest(call = { requestStudentByFaceUid(getRequestBody(JSON.toJSONString(faceInfo))) })
    }

    suspend fun listKitchen(schoolInfo: SchoolInfo): NetResult<List<KitchenInfo>?> {
        return callRequest(call = { requestListKitchen(getRequestBody(JSON.toJSONString(schoolInfo))) })
    }

    suspend fun listWindow(kitchenRequest: KitchenRequest): NetResult<List<WindowInfo>?> {
        return callRequest(call = { requestListWindow(getRequestBody(JSON.toJSONString(kitchenRequest)))})
    }

    suspend fun getStudentPackageMeal(request: MealRequest): NetResult<List<OrderInfo>?> {
        return callRequest(call = { requestStudentPackageMeal(getRequestBody(JSON.toJSONString(request))) })
    }

    suspend fun getLatestReceivedOrderList(request: MealTableRequest): NetResult<List<ReceivedOrder>?> {
        return callRequest(call = { requestLatestReceivedOrderList(getRequestBody(JSON.toJSONString(request))) })
    }

    suspend fun getMealMenuList(request: MealTableRequest): NetResult<List<MealMenu>?> {
        return callRequest(call = { requestMealMenuList(getRequestBody(JSON.toJSONString(request))) })
    }


    private suspend fun requestDeviceConfig() =
        handleResponse(service.create(RequestApi::class.java).getDeviceConfig())

    private suspend fun requestCurrentMeal() =
        handleResponse(service.create(RequestApi::class.java).getCurrentMeal())

    private suspend fun requestOrderModeList() =
        handleResponse(service.create(RequestApi::class.java).getOrderModeList())

    private suspend fun requestMealTableInfo() =
        handleResponse(service.create(RequestApi::class.java).queryMealTableInfo())

    private suspend fun requestUnbind() =
        handleResponse(service.create(RequestApi::class.java).unbind())

    private suspend fun requestListSchool() =
        handleResponse(service.create(RequestApi::class.java).listSchool())

    private suspend fun requestLogout() =
        handleResponse(service.create(RequestApi::class.java).logout())

    private suspend fun requestLogin(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).login(body))

    private suspend fun requestSave(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).save(body))

    private suspend fun requestConfirmPickUp(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).confirmPickUp(body))

    private suspend fun requestStudentByFaceUid(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).getStudentByFaceUid(body))

    private suspend fun requestListKitchen(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).listKitchen(body))

    private suspend fun requestListWindow(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).listWindow(body))

    private suspend fun requestStudentPackageMeal(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).getStudentPackageMeal(body))

    private suspend fun requestLatestReceivedOrderList(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).getLatestReceivedOrderList(body))

    private suspend fun requestMealMenuList(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).getMealMenuList(body))

    private fun getRequestBody(body: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), body)
    }

}