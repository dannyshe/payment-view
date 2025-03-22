package com.siupay.paymentview.dto

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

class NumberRequest {
    /**
     * 数据类型 1 ORDER  2 REFUND
     */
     lateinit var type: @NotEmpty(message = "type is empty") @Length(max = 32, message = "type max length is {max}") String

    /**
     * 展示的维度 1 10min   2 小时   3 天
     */
    lateinit var  showType: @NotEmpty(message = "showType is empty") @Length(max = 8, message = "showType max length is {max}") String

    /**
     * 渠道
     */
    val  channel: ArrayList<String>? = null

    /**
     * 查询某一天
     */
    val searchDay: String? = null


}