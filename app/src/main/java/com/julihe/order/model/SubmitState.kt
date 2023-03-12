package com.julihe.order.model

data class OrderState(val state: COMMIT_STATE?, val msg: String?, val code: String?)
enum class COMMIT_STATE{
   ORDER, REORDER,COMMITTING, SUCCESS, ERROR, SCANNING,SCAN_ERROR
}

