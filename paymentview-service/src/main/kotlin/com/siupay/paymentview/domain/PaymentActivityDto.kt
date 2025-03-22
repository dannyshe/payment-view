package com.siupay.paymentview.domain

import java.util.*
data  class PaymentActivityDto(var appId: String? = null,
                               var orderId: String? = null,
                               var orderType: OrderType? = null,
                               var status: String? = null,
                               var createTime: Date? = null,
                               var amount: Long? = null,
                               var currency: String? = null)
{

    enum class OrderType {
        PAYMENT, REFUND
    }



    override fun toString(): String {
        return "PaymentActivity{" +
                "orderId='" + orderId + '\'' +
                ", appId='" + appId + '\'' +
                ", orderType=" + orderType +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", amount=" + amount +
                ", currencye='" + currency + '\'' +
                '}'
    }

}


