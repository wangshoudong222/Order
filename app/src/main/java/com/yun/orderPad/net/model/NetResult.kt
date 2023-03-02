package com.yun.orderPad.net.model

import com.yun.orderPad.net.exception.ResultException


sealed class NetResult<out T : Any?> {

    data class Success<out T : Any?>(val data: T?) : NetResult<T?>()

    data class Error(val exception: ResultException) : NetResult<Nothing>()


}