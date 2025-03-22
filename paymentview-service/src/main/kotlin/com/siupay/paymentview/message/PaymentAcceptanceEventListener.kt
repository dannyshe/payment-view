//package com.siupay.paymentview.message
//
//import com.siupay.paymentview.mq.PaymentMQConsume
//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.slf4j.LoggerFactory
//import org.springframework.kafka.annotation.KafkaListener
//import org.springframework.messaging.support.MessageBuilder
//import org.springframework.stereotype.Component
//
//@Component
//class PaymentAcceptanceEventListener(
//    private var consumer: PaymentMQConsume
//) {
//    private val log = LoggerFactory.getLogger(this::class.java)
//
//    @KafkaListener(topics = ["PAYMENT_ACCEPTANCE_TOPIC"])
//    fun onMessage(record: ConsumerRecord<*, *>) {
//        try {
//            val message = record.value()
//            if (message != null) {
//                // 将 Kafka 消息转换为 Spring Message
//                val springMessage = MessageBuilder.withPayload(message).build()
//                consumer.process(springMessage)
//            } else {
//                log.error("Payment acceptance message is null")
//            }
//        } catch (e: Exception) {
//            log.error("Error processing payment acceptance message", e)
//            throw e
//        }
//    }
//}