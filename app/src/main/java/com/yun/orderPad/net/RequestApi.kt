package com.yun.orderPad.net


import com.win.lib_net.model.BaseModel
import com.yun.orderPad.model.Model
import retrofit2.http.GET


interface RequestApi {

    @GET("/login")
    suspend fun login(): BaseModel<MutableList<Model>>


}