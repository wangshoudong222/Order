package com.yun.orderPad.net

import com.google.gson.Gson
import com.yun.orderPad.model.request.LoginRequest
import com.yun.orderPad.model.result.LoginModel
import com.yun.orderPad.net.model.NetResult
import com.yun.orderPad.net.service.BaseRepository
import com.yun.orderPad.net.service.RetrofitClient
import okhttp3.MediaType
import okhttp3.RequestBody
import org.msgpack.util.json.JSON


class OrderRepository(private val service: RetrofitClient) : BaseRepository() {

    companion object {
        val instance: OrderRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OrderRepository(RetrofitClient.instance)
        }

        var TAG = "OrderRepository"
    }


    suspend fun login(loginRequest: LoginRequest): NetResult<LoginModel> {
        return callRequest(call = { requestLogin(getRequestBody(Gson().toJson(loginRequest))) })
    }

    private suspend fun requestLogin(body: RequestBody) =
        handleResponse(service.create(RequestApi::class.java).login(body))



    private fun getRequestBody(body: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), body)
    }

}