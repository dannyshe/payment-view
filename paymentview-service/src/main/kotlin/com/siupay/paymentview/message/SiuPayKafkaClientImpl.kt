//package com.siupay.paymentview.message
//
//import com.siupay.paymentview.util.JsonUtils
//import org.slf4j.LoggerFactory
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.kafka.support.SendResult
//import org.springframework.stereotype.Component
//import org.springframework.util.concurrent.ListenableFutureCallback
//
//@Component
//class SiuPayKafkaClientImpl(
//    private val kafkaTemplate: KafkaTemplate<String, String>
//) : SiuPayKafkaClient {
//
//    private val log = LoggerFactory.getLogger(this::class.java)
//
//    override fun send(topic: String, message: Any) {
//        try {
//            val jsonMessage = JsonUtils.toJson(message)
//            kafkaTemplate.send(topic, jsonMessage).get()
//            log.info("Sent message to topic {}: {}", topic, jsonMessage)
//        } catch (e: Exception) {
//            log.error("Error sending message to topic {}", topic, e)
//            throw e
//        }
//    }
//
//    override fun send(topic: String, key: String, message: Any) {
//        try {
//            val jsonMessage = JsonUtils.toJson(message)
//            kafkaTemplate.send(topic, key, jsonMessage).get()
//            log.info("Sent message to topic {} with key {}: {}", topic, key, jsonMessage)
//        } catch (e: Exception) {
//            log.error("Error sending message to topic {} with key {}", topic, key, e)
//            throw e
//        }
//    }
//
//    override fun sendAsync(topic: String, message: Any) {
//        try {
//            val jsonMessage = JsonUtils.toJson(message)
//            val future = kafkaTemplate.send(topic, jsonMessage)
//            future.addCallback(object : ListenableFutureCallback<SendResult<String, String>> {
//                override fun onSuccess(result: SendResult<String, String>?) {
//                    log.info("Async sent message to topic {}: {}", topic, jsonMessage)
//                }
//
//                override fun onFailure(ex: Throwable) {
//                    log.error("Failed to send async message to topic {}", topic, ex)
//                }
//            })
//        } catch (e: Exception) {
//            log.error("Error sending async message to topic {}", topic, e)
//            throw e
//        }
//    }
//
//    override fun sendAsync(topic: String, key: String, message: Any) {
//        try {
//            val jsonMessage = JsonUtils.toJson(message)
//            val future = kafkaTemplate.send(topic, key, jsonMessage)
//            future.addCallback(object : ListenableFutureCallback<SendResult<String, String>> {
//                override fun onSuccess(result: SendResult<String, String>?) {
//                    log.info("Async sent message to topic {} with key {}: {}", topic, key, jsonMessage)
//                }
//
//                override fun onFailure(ex: Throwable) {
//                    log.error("Failed to send async message to topic {} with key {}", topic, key, ex)
//                }
//            })
//        } catch (e: Exception) {
//            log.error("Error sending async message to topic {} with key {}", topic, key, e)
//            throw e
//        }
//    }
//}