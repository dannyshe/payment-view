package com.siupay.paymentview.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object JsonUtils {
    private val objectMapper = ObjectMapper().registerKotlinModule()

    /**
     * 将对象转换为 JSON 字符串
     * @param obj 要转换的对象
     * @return 转换后的 JSON 字符串，如果转换失败则返回空字符串
     */
    fun toJson(obj: Any?): String {
        return try {
            objectMapper.writeValueAsString(obj)
        } catch (e: Exception) {
            println("对象转换为 JSON 字符串时出错: ${e.message}")
            ""
        }
    }
}