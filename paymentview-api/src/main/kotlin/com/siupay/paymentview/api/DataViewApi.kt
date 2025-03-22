package com.siupay.paymentview.api

import com.siupay.paymentview.dto.ChannelNumberAndAmountResponse
import com.siupay.paymentview.dto.NumberResponse
import com.siupay.paymentview.dto.NumberRequest
import com.siupay.paymentview.dto.*
import io.vavr.control.Either
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

/**
 * 监控大盘服务接口
 **/
@FeignClient(name = "paymentview", qualifier = "DataViewApi")
interface DataViewApi {
    /**
     * 查询交易或者退款笔数
     * @param request
     * @create: 2020/8/10
     */
    @RequestMapping(
        value = ["/api/v1/paymentview/getNumber"],
        method = [RequestMethod.POST],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun getNumber(@RequestBody request: NumberRequest): Either<String, NumberResponse?>

    /**
     * 查询当天（0点-现在）的各个渠道的的交易笔数以及每个币种的交易金额
     * @create: 2020/8/10
     */
    @RequestMapping(
        value = ["/api/v1/paymentview/allChannelNumberAndAmount"],
        method = [RequestMethod.POST],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun allChannelNumberAndAmount(): Either<String, ChannelNumberAndAmountResponse?>

    /**
     * 查询过去一天的各币种的交易额
     */
    @RequestMapping(
        value = ["/api/v1/paymentview/getOrderAmount"],
        method = [RequestMethod.POST],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun getOrderAmount(): Either<String, OrderAmountResponse?>
}