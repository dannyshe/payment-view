package com.siupay.paymentview.dto

import java.io.Serializable
import java.math.BigDecimal

data class Amount (val currency:String,var amount: BigDecimal,var number:Long):
    Serializable {

    companion object {
        private const val serialVersionUID = -1L
    }
}