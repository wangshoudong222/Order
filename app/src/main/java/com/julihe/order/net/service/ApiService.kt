package com.julihe.order.net.service

import android.os.Build

interface ApiService {

    companion object {

        private const val TEST_BASE_URL = "http://jlh.dzy315.com"
        private const val ONLINE_BASE_URL = "https://jsa.5855mall.cn"
        const val BASE_URL = ONLINE_BASE_URL

    }
}

