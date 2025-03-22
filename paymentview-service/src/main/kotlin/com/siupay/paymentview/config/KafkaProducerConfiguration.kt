//package com.siupay.paymentview.config
//
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.common.serialization.StringSerializer
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.kafka.core.DefaultKafkaProducerFactory
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.kafka.core.ProducerFactory
//
//@Configuration
//class KafkaProducerConfiguration(
//    private val kafkaProducerProperties: KafkaProducerProperties
//) {
//
//    @Bean
//    fun producerFactory(): ProducerFactory<String, String> {
//        val props = HashMap<String, Any>()
//        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProducerProperties.props["bootstrap.server"] ?: ""
//        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        return DefaultKafkaProducerFactory(props)
//    }
//
//    @Bean
//    fun kafkaTemplate(): KafkaTemplate<String, String> {
//        return KafkaTemplate(producerFactory())
//    }
//}