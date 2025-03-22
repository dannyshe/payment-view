package com.siupay.paymentview.service

import com.siupay.paymentview.dto.ChannelNumberAndAmountResponse
import com.siupay.paymentview.dto.NumberRequest
import com.siupay.paymentview.dto.NumberResponse
import com.siupay.paymentview.dto.OrderAmountResponse

interface DataViewService {
    fun getNumber(request: NumberRequest): NumberResponse?
    fun allChannelNumberAndAmount(): ChannelNumberAndAmountResponse?
    fun getOrderAmount(): OrderAmountResponse?
}