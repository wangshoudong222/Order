package com.yun.orderPad.model.result

data class OrderInfo (
    /**
     * 餐点日期
     */
    val mealDate: String,

    /**
     * 餐次名称
     */
    val mealTableName: String,

    /**
     * 餐点类型名称
     */
    val mealTypeName: String,

    val orderDetailInfoList: List<OrderDetailInfo>,

    /**
     * 订单号
     */
    val orderNo: String
)
