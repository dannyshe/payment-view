package com.siupay.paymentview.handel

import com.siupay.paymentview.domain.DataStructure
import com.siupay.paymentview.domain.PaymentActivityDto
import lombok.extern.slf4j.Slf4j
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

@Slf4j
@Service
class RefundMessageHandler(

    override val redissonClient: RedissonClient
): PaymentActivityMessageHandler() {
    private val status = "PENDING"
    override var dataStructure: DataStructure= DataStructure(
            amountColName = "amount",
            orderIdColName = "refundOrderId",
            statusColName = "statusCode",
            appIdColName = "payChId",
            createTimeColName ="createTime",
            currencyColName="currency"
        )




    override fun buildDto(columns: List<String?>, objects: List<Any?>): PaymentActivityDto {
        val result: PaymentActivityDto = super.buildDto(columns, objects)
        result.orderType  = PaymentActivityDto.OrderType.REFUND
        return result
    }

    override fun filter(columns: List<String?>?, objects: List<Any?>?): Boolean {
        super.filter(columns, objects)
        val statusNow = objects!![columns!!.indexOf("statusCode")].toString()
        return statusNow==status
    }
}