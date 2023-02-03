package com.yun.orderPad.net

import com.yun.orderPad.net.service.BaseRepository
import com.yun.orderPad.net.service.RetrofitClient
import com.win.lib_net.model.NetResult
import com.yun.orderPad.model.Model

class OrderRepository(private val service: RetrofitClient) : BaseRepository() {

    suspend fun login(): NetResult<List<Model>> {
        return callRequest(call = { requestLogin() })
    }

    private suspend fun requestLogin() =
        handleResponse(service.create(RequestApi::class.java).login())

}