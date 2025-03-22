package com.siupay.paymentview.controller

import com.siupay.paymentview.api.DataViewApi
import com.siupay.paymentview.dto.ChannelNumberAndAmountResponse
import com.siupay.paymentview.dto.NumberResponse
import com.siupay.paymentview.dto.NumberRequest
import com.siupay.paymentview.dto.OrderAmountResponse
import com.siupay.paymentview.service.DataViewService
import io.vavr.control.Either
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@FeignClient(name = "paymentview", qualifier = "dataViewController")
class DataViewController(
       private var dataViewService: DataViewService
    ) : DataViewApi

    {

        override fun getNumber(request: NumberRequest): Either<String, NumberResponse?> {
            return Either.right(dataViewService.getNumber(request))
        }

        override fun allChannelNumberAndAmount(): Either<String, ChannelNumberAndAmountResponse?> {
            return Either.right(dataViewService.allChannelNumberAndAmount())
        }

        override fun getOrderAmount(): Either<String, OrderAmountResponse?> {
            return Either.right(dataViewService.getOrderAmount())
        }

}