package com.julihe.order.model.result

data class FaceSignature(
    val terminalType: String,
    val signature: String,
    val apdidToken: String,
    val hardToken: String,
    val bizCode: String,
    val bizTid: String,
    val time: String,
    val signedKeys: String,
)
