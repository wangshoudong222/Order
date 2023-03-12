package com.julihe.order.net.exception


class ResultException(var errCode: String?, var msg: String?) : Exception(msg)
