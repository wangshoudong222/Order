package com.julihe.order.model.result

data class ReceivedOrder(
    /**
     * 班级
     */
    val className: String,

    /**
     * 餐点名称
     */
    val dishSkuName: String,

    /**
     * 餐次名称
     */
    val mealTableName: String,

    /**
     * 取餐时间
     */
    val pickUpTime: String,

    /**
     * 价格，BigDecimal
     */
    val price: Double,

    /**
     * 学生名称
     */
    val studentName: String
)
