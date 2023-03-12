package com.julihe.order.net.service

import com.julihe.order.net.exception.DealException

import com.julihe.order.net.exception.ResultException
import com.julihe.order.net.model.BaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import com.julihe.order.net.model.NetResult

open class BaseRepository {

    suspend fun <T : Any?> callRequest(
        call: suspend () -> NetResult<T?>
    ): NetResult<T?> {
        return try {
            call()
        } catch (e: Exception) {
            //这里统一处理异常
            e.printStackTrace()
            NetResult.Error(DealException.handlerException(e))
        }
    }

    suspend fun <T : Any?> handleResponse(
        response: BaseModel<T?>,
        successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): NetResult<T?> {
        return coroutineScope {
            if (response.code != 1) {
                errorBlock?.let { it() }
                NetResult.Error(
                    ResultException(
                        response.code.toString(),
                        response.message
                    )
                )
            } else {
                successBlock?.let { it() }
                NetResult.Success(response.data)
            }
        }
    }


}