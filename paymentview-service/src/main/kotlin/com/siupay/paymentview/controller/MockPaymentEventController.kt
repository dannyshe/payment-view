package com.siupay.paymentview.controller

import com.alibaba.fastjson.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.kafka.support.Acknowledgment

@RestController
@RequestMapping("/api/v1/mockEvent")
class MockPaymentEventController(
    private val source: Source
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun mockPaymentEvent(@RequestBody payload: JSONObject): Any {
        log.info("Received mock payment event: {}", payload)
        try {
            val message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, MockAcknowledgment())
                .build()
            source.output().send(message)
            return mapOf(
                "success" to true,
                "message" to "Event sent successfully"
            )
        } catch (e: Exception) {
            log.error("Failed to send mock payment event", e)
            return mapOf(
                "success" to false,
                "message" to "Failed to send event: ${e.message}"
            )
        }
    }
}

class MockAcknowledgment : Acknowledgment {
    override fun acknowledge() {
        // 模拟确认消息
    }
}