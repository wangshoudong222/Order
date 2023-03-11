package com.julihe.orderPad.net.interceptor

import com.julihe.orderPad.util.sp.SpUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class CommonInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = addHeaders(request.newBuilder())
        return chain.proceed(builder)
    }

    private fun addHeaders(builder: Request.Builder): Request {
        return builder.addHeader("Content_Type", "application/json")
            .addHeader("charset", "UTF-8")
            .addHeader("deviceId", SpUtil.deviceId())
            .addHeader("Authorization", "Bearer "+ SpUtil.token())
            .build()
    }
}