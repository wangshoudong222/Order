package com.julihe.order.model.result

data class Student(
    /**
     * 头像链接
     */
    val avatar: String,

    /**
     * 班级名称
     */
    val className: String,

    /**
     * 年级名称
     */
    val gradeName: String,

    /**
     * 学生id
     */
    val id: String,

    /**
     * 届数名称
     */
    val numberOfClassName: String,

    /**
     * 学校名称
     */
    val schoolName: String,
    /**
     * 学校内标
     */
    val instId: String,

    /**
     * 学生名称
     */
    val studentName: String,

    /**
     * 学生编号
     */
    val studentNo: String
)
