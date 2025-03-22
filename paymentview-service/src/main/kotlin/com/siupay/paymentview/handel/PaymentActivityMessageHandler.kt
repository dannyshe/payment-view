package com.siupay.paymentview.handel

import com.google.common.collect.Lists
import com.siupay.paymentview.domain.ChannelInfo
import com.siupay.paymentview.domain.DataStructure
import com.siupay.paymentview.domain.PaymentActivityDto
import com.siupay.paymentview.domain.TradeMessage
import com.siupay.paymentview.dto.Amount
import com.siupay.paymentview.enum.ChannelEnum
import org.redisson.api.RList
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

abstract class PaymentActivityMessageHandler :
    MessageHandler<PaymentActivityDto> {

    abstract val redissonClient: RedissonClient

    abstract var dataStructure: DataStructure

    private val log : Logger = LoggerFactory.getLogger(PaymentActivityMessageHandler::class.java)



    override fun buildDto(columns: List<String?>, objects: List<Any?>): PaymentActivityDto {
        val paymentActivity = PaymentActivityDto()
        paymentActivity.amount = objects[columns.indexOf(dataStructure.amountColName)].toString().toLong()
        paymentActivity.currency =  objects[columns.indexOf(dataStructure.currencyColName)].toString()
        paymentActivity.status = objects[columns.indexOf(dataStructure.statusColName)].toString()
        paymentActivity.orderId = objects[columns.indexOf(dataStructure.orderIdColName)].toString()
        paymentActivity.appId  = objects[columns.indexOf(dataStructure.appIdColName)].toString()
        paymentActivity.createTime = Date(objects[columns.indexOf(dataStructure.createTimeColName)]  as Long)
        paymentActivity.orderType = PaymentActivityDto.OrderType.PAYMENT
        return paymentActivity
    }
    override fun filter(columns: List<String?>?, objects: List<Any?>?): Boolean {
        //val status = objects!![columns!!.indexOf(dataStructure.statusColName)].toString()
        return true;
    }




    override fun messagePersist(paymentActivitys: List<PaymentActivityDto>?): Boolean {
        if (paymentActivitys != null && paymentActivitys.isNotEmpty()) {
            //key:    day20210303    hour2021030301    10min2021030301 10
            // value:  List<ChannelInfo
            paymentActivitys.filter{it.createTime!=null}.forEach {
                var isExist = true
                if (PaymentActivityDto.OrderType.PAYMENT == it.orderType){
                    isExist = redissonClient.getBucket<Int>("jkdpP"+it.orderId).get()!=null
                }else if (PaymentActivityDto.OrderType.REFUND == it.orderType){
                    isExist = redissonClient.getBucket<Int>("jkdpR"+it.orderId).get()!=null
                }
                if (it.appId!=null&&!isExist) {
                    val keyList = getKeyList(it.createTime!!)
                    saveRedis(it,keyList)
                }
            }
        }
        return true
    }



    private fun saveRedis(it: PaymentActivityDto, keyList :ArrayList<String>){
        if(it.appId=="UNKNOWN"){//Taishin 初始渠道为UNKNOWN
            it.appId = ChannelEnum.Taishin.name
        }
        if (keyList.size>0) {
            for ( key in keyList) {
                if (PaymentActivityDto.OrderType.PAYMENT == it.orderType){//第一条数据
                    val channelInfoList: RList<ChannelInfo> = redissonClient.getList(key)
                    channelInfoList.expire(90,TimeUnit.DAYS)
                    if (channelInfoList.size==0) {
                        val amountList = ArrayList<Amount>()
                        val amount = Amount(it.currency!!, BigDecimal(it.amount!!),1L)
                        amountList.add(amount)
                        val channelInfo = ChannelInfo(it.appId?.toLowerCase(), 1, 0,amountList)
                        channelInfoList.add(channelInfo)
                    }else {//老渠道
                        var flag = false
                        for (i in channelInfoList.indices) {
                            val channelInfo =channelInfoList[i]
                            if (channelInfo.channel.equals(it.appId, ignoreCase = true)) {
                                channelInfo.orderNumber= channelInfo.orderNumber+1
                                var flag2 = false
                                if(channelInfo.amounts.size>0) {
                                    for ( amountIndex in channelInfo.amounts) {
                                        if (amountIndex.currency==it.currency) {
                                            amountIndex.amount = amountIndex.amount.add(BigDecimal(it.amount!!))
                                            amountIndex.number = amountIndex.number+1
                                            flag2 = true
                                            break
                                        }
                                    }
                                }
                                if (!flag2) {
                                    val amount = Amount(it.currency!!,BigDecimal(it.amount!!),1L)
                                    channelInfo.amounts.add(amount)
                                }
                                channelInfoList.remove(channelInfoList[i])
                                channelInfoList.add(i,channelInfo)
                                flag = true
                                break
                            }
                        }
                        if (!flag) {  //新渠道
                            val amountList = ArrayList<Amount>()
                            val amount = Amount(it.currency!!,BigDecimal(it.amount!!),1)
                            amountList.add(amount)
                            val channelInfo = ChannelInfo(it.appId?.toLowerCase(), 1, 0,amountList)
                            channelInfoList.add(channelInfo)
                        }
                    }
                    redissonClient.getBucket<Int>("jkdpP"+it.orderId).set(1,7,TimeUnit.HOURS)
                }else if (PaymentActivityDto.OrderType.REFUND == it.orderType){
                    val channelInfoList: RList<ChannelInfo> = redissonClient!!.getList(key)
                    if (channelInfoList.size==0) {
                        val channelInfo = ChannelInfo(it.appId?.toLowerCase(), 0, 1,ArrayList())
                        channelInfoList.add(channelInfo)
                    }else {//老渠道
                        var flag = false
                        for (i in channelInfoList.indices) {
                            val channelInfo = channelInfoList[i]
                            if (channelInfo.channel==it.appId?.toLowerCase()) {
                                channelInfo.refundNumber=channelInfo.refundNumber+1
                                channelInfoList.remove(channelInfoList[i])
                                channelInfoList.add(i,channelInfo)
                                flag = true
                                break
                            }
                        }
                        if (!flag) {  //新渠道
                            val channelInfo = ChannelInfo(it.appId?.toLowerCase(), 0, 1,ArrayList())
                            channelInfoList.add(channelInfo)
                        }
                    }
                    redissonClient.getBucket<Int>("jkdpR"+it.orderId).set(1,7,TimeUnit.HOURS)
                }
            }
        }
    }


    private fun getKeyList(date: Date):ArrayList<String>{
        val instant = date.toInstant()
        val zoneId = ZoneId.of("UTC")
        //数据库存储的是utc但是部署的是utc-8  所有数据库转化来的时间戳是按照北京时间转化的 所以要加8
        val localDateTime = instant.atZone(zoneId).toLocalDateTime().plusHours(8)
        val df = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        val time = df.format(localDateTime)
        val key1 = "10min"+time.substring(0,11)+"0"
        val key2 = "hour"+time.substring(0,10)
        val key3 = "day"+time.substring(0,8)
        log.info("当前的key为：$key1$key2$key3")
        val list = ArrayList<String>()
        list.add(key1)
        list.add(key2)
        list.add(key3)
        return list
    }

    override fun getPersistList(message: TradeMessage): List<PaymentActivityDto> {
        val size: Int = message.datas.size
        val paymentActivities: MutableList<PaymentActivityDto> = Lists.newArrayList()
        for (i in 0 until size) {
            paymentActivities.add(buildDto(message.columns, message.datas[i]))
        }
        return paymentActivities
    }
}