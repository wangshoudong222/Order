package com.julihe.order.smile

import com.alibaba.fastjson.JSON
import com.julihe.order.model.result.Config
import com.julihe.order.smile.SmileManager.MetaInfo
import com.julihe.order.util.sp.SpUtil

object IsvInfo {
    @JvmField
    val ISV_INFO = MetaInfo(
        "2088610645102760",
        "高阳县庞口镇庞口中学",
        "2088541567616841",
        "beijingjulihe",
        "K12_3113013556",
        SpUtil.deviceId(),
        "2088541566333127")

    fun getInfo():MetaInfo {
        val config = JSON.parseObject(SpUtil.config(), Config::class.java)
       return  MetaInfo(
           config.instId,
           config.schoolName,
           "2088541567616841",
           "beijingjulihe",
           config.schoolFaceId,
           SpUtil.deviceId(),
           config.schoolPaymentUserId)
    }
    /**
     * isvPid : 2088541567616841
     * ISV英文名称（isv_name）：beijingjulihe
     * ISV中文名称：北京聚利和科技有限公司
     * 商户pid: 2088520367412275
     * 商户学校名称：北京聚利和学校
     * 商户收款pId：2088821434894708
     * groupId:K12_91110302MA01Q2UF9D
     */

    /**
     * isvPid : 2088541567616841
     * ISV英文名称（isv_name）：beijingjulihe
     * ISV中文名称：北京聚利和科技有限公司
     * 商户pid: 2088610645102760
     * 商户学校名称：高阳县庞口镇庞口中学
     * 商户收款pId：2088541566333127
     * groupId:K12_3113013556
     */
}