package com.siupay.paymentview.handel

import com.siupay.paymentview.domain.TradeMessage

interface MessageHandler<T>{

    fun buildDto(columns: List<String?>, objects: List<Any?>): T
    fun filter(columns: List<String?>?, objects: List<Any?>?): Boolean?
    fun messagePersist(paymentActivity: List<T>?): Boolean
    fun getPersistList(message: TradeMessage): List<T>


}