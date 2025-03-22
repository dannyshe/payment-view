package com.siupay.paymentview.service.impl

import com.siupay.paymentview.domain.ChannelInfo
import com.siupay.paymentview.dto.*
import com.siupay.paymentview.enum.ChannelEnum
import com.siupay.paymentview.enum.CurrencyEnum
import com.siupay.paymentview.mq.PaymentMQConsume
import com.siupay.paymentview.service.DataViewService
import lombok.extern.slf4j.Slf4j
import org.redisson.api.RList
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Service
@Slf4j
class DataViewServiceImpl(
    private  var redissonClient: RedissonClient
    ) : DataViewService{

    @Autowired
    lateinit var paymentMQConsume: PaymentMQConsume

    override fun getNumber(request: NumberRequest): NumberResponse? {
        val channelNumberList = ArrayList<ChannelNumber>()
        //数据类型 1 ORDER  2 REFUND
        val keyList = getKeyList(request.showType,request.searchDay)
        if (keyList.size > 0) {
            //初始化返回值
            val periodNumbers1 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            keyList.forEach { key ->
                periodNumbers1.add(com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0))
            }
            val periodNumbers2 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            keyList.forEach { key ->
                periodNumbers2.add(com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0))
            }
            val periodNumbers3 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            keyList.forEach { key ->
                periodNumbers3.add(com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0))
            }
            val periodNumbers4 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            keyList.forEach { key ->
                periodNumbers4.add(com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0))
            }
            val periodNumbers5 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            keyList.forEach { key ->
                periodNumbers5.add(com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0))
            }



            val channelNumber1 = ChannelNumber(ChannelEnum.Adyen.name, periodNumbers1)
            val channelNumber2 = ChannelNumber(ChannelEnum.Taishin.name, periodNumbers2)
            val channelNumber3 = ChannelNumber(ChannelEnum.Checkout.name, periodNumbers3)
            val channelNumber4 = ChannelNumber(ChannelEnum.RevPay.name, periodNumbers4)
            val channelNumber5 = ChannelNumber(ChannelEnum.Total.name, periodNumbers5)
            channelNumberList.add(channelNumber1)
            channelNumberList.add(channelNumber2)
            channelNumberList.add(channelNumber3)
            channelNumberList.add(channelNumber4)
            channelNumberList.add(channelNumber5)
//            val batch: RBatch = redissonClient.createBatch()
//            for (key in keyList) {
//                batch.getList<ChannelInfo>(key)
//            }
//            val res = batch.execute()
            for (key in keyList) {
                val channelInfoList: RList<ChannelInfo> = redissonClient.getList(key)
                if (channelInfoList.size > 0) {
                    var numberTotal = 0L
                    for (channelInfo in channelInfoList) {
                        val number = if (request.type == "ORDER") channelInfo.orderNumber else channelInfo.refundNumber
                        for (channelNumberIndex in channelNumberList) {
                            if (channelNumberIndex.channel.equals(channelInfo.channel, ignoreCase = true)) {
                                for (index in channelNumberIndex.periodNumbers){
                                    if (index.period==transferKeyToPeriod(key)){
                                        index.number = index.number + number
                                        numberTotal += number
                                        break
                                    }
                                }
                                break
                            }
                        }
                    }
                    for (channelNumberIndex in channelNumberList) {
                        if (channelNumberIndex.channel.equals(ChannelEnum.Total.name, ignoreCase = true)) {
                            for (index in channelNumberIndex.periodNumbers){
                                if (index.period==transferKeyToPeriod(key)){
                                    index.number = numberTotal
                                    break
                                }
                            }
                            break
                        }
                    }
                }
            }
        }
        val filterChannelList = ArrayList<String?>()
        filterChannelList.add(ChannelEnum.Total.name)
        if (request.channel!=null&& request.channel!!.isNotEmpty()){
            filterChannelList.addAll(request.channel!!)
        }
        val channelNumberList2 = channelNumberList.filter{filterChannelList.contains(it.channel)}
        return NumberResponse(ArrayList(channelNumberList2) )
    }


    fun getKeyList(showType:String, searchDay: String?): ArrayList<String> {
        //展示的维度 1 10min   2 小时   3 天
        val keyList = ArrayList<String>()
        val zoneId = ZoneId.of("UTC")
        val df = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        if (searchDay!=null&&searchDay.length==8){
            val year = searchDay.substring(0,4).toInt()
            val month = searchDay.substring(4,6).toInt()
            val day = searchDay.substring(6,8).toInt()
            val localDateTime = LocalDateTime.of(year, month, day, 0, 0, 0).atZone(zoneId)
            val time = df.format(localDateTime)
            val key = "hour"+time.substring(0,10)
            keyList.add(key)
            for (i in 1..23) {
                val localDateTimeNew: ZonedDateTime = localDateTime.plusHours((i).toLong())
                val timeFormat = df.format(localDateTimeNew)
                val keyNew = "hour"+timeFormat.substring(0,10)
                keyList.add(keyNew)
            }
        }else{
            val localDateTime: ZonedDateTime = LocalDateTime.now().atZone(zoneId)
            val time = df.format(localDateTime)
            val key1 = "10min"+time.substring(0,11)+"0"
            val key2 = "hour"+time.substring(0,10)
            val key3 = "day"+time.substring(0,8)
            when (showType) {
                "1" -> {
                    for (i in 17 downTo 1) {
                        val localDateTimeNew: ZonedDateTime = localDateTime.minusMinutes((i * 10).toLong())
                        val timeFormat = df.format(localDateTimeNew)
                        val keyNew = "10min"+timeFormat.substring(0,11)+"0"
                        keyList.add(keyNew)
                    }
                    keyList.add(key1)
                }
                "2" -> {
                    for (i in 23 downTo 1) {
                        val localDateTimeNew: ZonedDateTime = localDateTime.minusHours((i).toLong())
                        val timeFormat = df.format(localDateTimeNew)
                        val keyNew = "hour"+timeFormat.substring(0,10)
                        keyList.add(keyNew)
                    }
                    keyList.add(key2)
                }
                "3" -> {
                    for (i in 13 downTo 1) {
                        val localDateTimeNew: ZonedDateTime = localDateTime.minusDays((i).toLong())
                        val timeFormat = df.format(localDateTimeNew)
                        val keyNew = "day"+timeFormat.substring(0,8)
                        keyList.add(keyNew)
                    }
                    keyList.add(key3)
                }
            }
        }
        return keyList
    }

    override fun allChannelNumberAndAmount(): ChannelNumberAndAmountResponse? {

         //paymentMQConsume.testc()
        val channelNumberAndAmountList = ArrayList<ChannelNumberAndAmount>()
        val channelNumberAndAmountCheckout = ChannelNumberAndAmount(ChannelEnum.Checkout.name,0 ,arrayListOf(Amount(CurrencyEnum.HKD.name,BigDecimal("0.0"),0),Amount(CurrencyEnum.USD.name,BigDecimal("0.0"),0)))
        val channelNumberAndAmountRevPay = ChannelNumberAndAmount(ChannelEnum.RevPay.name,0 ,arrayListOf(Amount(CurrencyEnum.MYR.name,BigDecimal("0.0"),0)))
        val channelNumberAndAmountTaishin = ChannelNumberAndAmount(ChannelEnum.Taishin.name,0 ,arrayListOf(Amount(CurrencyEnum.TWD.name,BigDecimal("0.0"),0)))
        val channelNumberAndAmountAdyen = ChannelNumberAndAmount(ChannelEnum.Adyen.name,0 ,arrayListOf(Amount(CurrencyEnum.SGD.name,BigDecimal("0.0"),0)))
        channelNumberAndAmountList.add(channelNumberAndAmountCheckout)
        channelNumberAndAmountList.add(channelNumberAndAmountRevPay)
        channelNumberAndAmountList.add(channelNumberAndAmountTaishin)
        channelNumberAndAmountList.add(channelNumberAndAmountAdyen)
        val key = getKey()
        val channelInfoList: RList<ChannelInfo> = redissonClient.getList(key)
        if (channelInfoList.size > 0) {
            for (channelInfo in channelInfoList) {
                for (index in channelNumberAndAmountList){
                    if (channelInfo.channel.equals(index.channel, ignoreCase = true)){
                        index.number = channelInfo.orderNumber
                        index.amounts = channelInfo.amounts
                        break
                    }
                }
            }
        }
        return ChannelNumberAndAmountResponse(channelNumberAndAmountList)
    }

    fun getKey(): String {
        val zoneId = ZoneId.of("UTC")
        val df = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        val localDateTime: ZonedDateTime = LocalDateTime.now().atZone(zoneId)
        val timeFormat = df.format(localDateTime)
        return "day" + timeFormat.substring(0, 8)
    }


    override fun getOrderAmount(): OrderAmountResponse? {
        val orderAmounts =  ArrayList<OrderAmount>()
        val keyList = getKeyList3()

        if (keyList.size > 0) {
            //初始化返回值
            val periodNumbers1 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            for (key in keyList) {
                val periodNumber = com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0)
                periodNumbers1.add(periodNumber)
            }
            val periodNumbers2 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            for (key in keyList) {
                val periodNumber = com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0)
                periodNumbers2.add(periodNumber)
            }
            val periodNumbers3 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            for (key in keyList) {
                val periodNumber = com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0)
                periodNumbers3.add(periodNumber)
            }
            val periodNumbers4 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            for (key in keyList) {
                val periodNumber = com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0)
                periodNumbers4.add(periodNumber)
            }
            val periodNumbers5 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            for (key in keyList) {
                val periodNumber = com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0)
                periodNumbers5.add(periodNumber)
            }
            val periodNumbers6 = ArrayList<com.siupay.paymentview.dto.PeriodNumber>()
            for (key in keyList) {
                val periodNumber = com.siupay.paymentview.dto.PeriodNumber(transferKeyToPeriod(key), 0)
                periodNumbers6.add(periodNumber)
            }
            val orderAmount1 = OrderAmount(CurrencyEnum.HKD.name,BigDecimal("0"), periodNumbers1)
            val orderAmount2 = OrderAmount(CurrencyEnum.MYR.name,BigDecimal("0"), periodNumbers2)
            val orderAmount3 = OrderAmount(CurrencyEnum.USD.name,BigDecimal("0"), periodNumbers3)
            val orderAmount4 = OrderAmount(CurrencyEnum.CNY.name,BigDecimal("0"), periodNumbers4)
            val orderAmount5 = OrderAmount(CurrencyEnum.SGD.name,BigDecimal("0"), periodNumbers5)
            val orderAmount6 = OrderAmount(CurrencyEnum.TWD.name,BigDecimal("0"), periodNumbers6)
            orderAmounts.add(orderAmount1)
            orderAmounts.add(orderAmount2)
            orderAmounts.add(orderAmount3)
            orderAmounts.add(orderAmount4)
            orderAmounts.add(orderAmount5)
            orderAmounts.add(orderAmount6)

            for (key in keyList) {
               //HKD MYR USD  CNY  SGD  TWD
                val channelInfoList: RList<ChannelInfo> = redissonClient.getList(key)
                if (channelInfoList.size > 0) {
                    val period = transferKeyToPeriod(key)
                    for (channelInfo in channelInfoList) {
                        val amounts = channelInfo.amounts
                        for (amountIndex in amounts){
                            for (orderAmountIndex in orderAmounts){
                                if (amountIndex.currency.equals(orderAmountIndex.currency, ignoreCase = true)){
                                    orderAmountIndex.amount = orderAmountIndex.amount.add(amountIndex.amount)
                                    for (periodNumberIndex in orderAmountIndex.periodNumbers){
                                        if (periodNumberIndex.period.equals(period, ignoreCase = true)){
                                            periodNumberIndex.number = periodNumberIndex.number + amountIndex.number
                                            break
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
        return OrderAmountResponse(orderAmounts)
    }


    fun getKeyList3(): ArrayList<String> {
        val keyList = ArrayList<String>()
        val zoneId = ZoneId.of("UTC")
        val df = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        val localDateTime: ZonedDateTime = LocalDateTime.now().atZone(zoneId)
        val localDateTime1: ZonedDateTime = localDateTime.withHour(0).withMinute(0).withSecond(0)
        val localDateTime2: ZonedDateTime = localDateTime1.minusDays(1)
        val timeFormat = df.format(localDateTime2)
        val key = "hour" + timeFormat.substring(0, 10)
        keyList.add(key)
        for (i in 1..23) {
            val localDateTimeNew: ZonedDateTime = localDateTime2.plusHours((i).toLong())
            val timeFormat = df.format(localDateTimeNew)
            val keyNew = "hour" + timeFormat.substring(0, 10)
            keyList.add(keyNew)
        }
        return keyList
    }

    fun transferKeyToPeriod(key :String) :String{
        val zoneId = ZoneId.of("UTC")
        val second: Long
        val localDateTime: ZonedDateTime = when {
            key.startsWith("day") -> {
                LocalDateTime.of(key.substring(3,7).toInt(), key.substring(7,9).toInt(), key.substring(9,11).toInt(), 0, 0, 0).atZone(zoneId)
            }
            key.startsWith("hour") -> {
                LocalDateTime.of(key.substring(4,8).toInt(), key.substring(8,10).toInt(), key.substring(10,12).toInt(), key.substring(12,14).toInt(), 0, 0).atZone(zoneId)
            }
            key.startsWith("10min") -> {
                LocalDateTime.of(key.substring(5,9).toInt(), key.substring(9,11).toInt(), key.substring(11,13).toInt(), key.substring(13,15).toInt(), key.substring(15,17).toInt(), 0).atZone(zoneId)
            }
            else -> return "0"
        }
        val instant = localDateTime.toInstant()
        second = instant.atZone(zoneId).toEpochSecond()
        return second.toString()
    }

}