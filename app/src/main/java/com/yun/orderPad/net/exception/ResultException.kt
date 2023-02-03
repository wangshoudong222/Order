package com.yun.orderPad.net.exception


class ResultException(var errCode: String?, var msg: String?) : Exception(msg)
