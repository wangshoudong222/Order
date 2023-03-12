package com.julihe.order.net.model

data class BaseModel<out T>(val code: Int, val message: String, val data: T)