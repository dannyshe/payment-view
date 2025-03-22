package com.siupay.paymentview.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vavr.jackson.datatype.VavrModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
class SysConfig {
    @Bean
    fun responseCustomizer() = MappingJackson2HttpMessageConverter(
        jacksonObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerModule(VavrModule(VavrModule.Settings().useOptionInPlainFormat(true)))
    )

}