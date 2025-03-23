package com.siupay.paymentview

import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import java.util.*

@EnableDiscoveryClient
@EnableEncryptableProperties
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@Slf4j
@EnableBinding(Source::class)
class PaymentViewApplication


fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    runApplication<PaymentViewApplication>(*args)
}