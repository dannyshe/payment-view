package com.siupay.paymentview.domain

import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang3.StringUtils
import java.io.Serializable

data class TradeMessage(val table: String, val timestamp: Long ,
                        val type:Int,val binarybitmap :Long,
                        @JsonProperty("columns") val columns: List<String>,
                        @JsonProperty("pks") val pks: List<Int>? = null,
                        @JsonProperty("datas") val datas: List<List<Any>>,
                        @JsonProperty("beforeChanges") val beforeChanges: List<Map<String, Any>>? = null)
    : Serializable {

    fun getDatabaseName(): String {
        val pair = table!!.split("\\.".toRegex()).toTypedArray()
        return pair[0]
    }

    fun getTableName(): String {
        val pair = table!!.split("\\.".toRegex()).toTypedArray()
        val originalName = pair[1]
        val split = originalName.split("_".toRegex()).toTypedArray()
        return if (StringUtils.isNumeric(split[split.size - 1])) {
            originalName.substring(0, originalName.lastIndexOf("_"))
        } else originalName
    }


    override fun toString(): String {
        return JSON.toJSONString(this)
    }


    companion object {
        private const val serialVersionUID = -1L
    }
}