package com.siupay.paymentview

import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.transaction.annotation.EnableTransactionManagement
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import java.util.*

/**
 * Created by yangzhenhao on 2020/6/15.
 */

@SpringBootApplication


@Profile("dev")
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableEncryptableProperties

@Slf4j
class PaymentViewDevApplication
    fun main(args: Array<String>) {
        System.setProperty("CONSUL_HOST", "10.218.13.166")
        System.setProperty("spring.profiles.active", "dev")
        System.setProperty("CONSUL_CONFIG_ENABLED", "false")
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        runApplication<PaymentViewDevApplication>(*args)
    }
