package com.siupay.paymentview.handel

import com.siupay.paymentview.domain.DataStructure
import com.siupay.paymentview.domain.PaymentActivityDto
import org.redisson.api.RedissonClient
import org.springframework.data.redis.connection.RedisServer
import org.springframework.stereotype.Service

@Service
class PaymentMessageHandler(
    override val redissonClient: RedissonClient
): PaymentActivityMessageHandler() {
    override var dataStructure: DataStructure = DataStructure(
        amountColName = "amount",
        orderIdColName = "payOrderId",
        statusColName = "statusCode",
        appIdColName = "chId",
        createTimeColName = "createTime",
        currencyColName = "currency"
    )

    private val status = "PAYING"

    override fun buildDto(columns: List<String?>, objects: List<Any?>): PaymentActivityDto {
        val result = super.buildDto(columns, objects)
        result.orderType = PaymentActivityDto.OrderType.PAYMENT
        return result
    }

    override fun filter(columns: List<String?>?, objects: List<Any?>?): Boolean {
        super.filter(columns, objects)
        val statusNow = columns?.let { objects?.get(it.indexOf("statusCode")).toString() }
        return statusNow==status
    }
}