package com.yun.orderPad.net


import com.yun.orderPad.model.request.LoginRequest
import com.yun.orderPad.model.result.LoginModel
import com.yun.orderPad.net.model.BaseModel
import okhttp3.RequestBody
import retrofit2.http.*


interface RequestApi {

    @POST("/api-jlh-bff/canteen/login")
    suspend fun login(@Body body: RequestBody): BaseModel<LoginModel>

}