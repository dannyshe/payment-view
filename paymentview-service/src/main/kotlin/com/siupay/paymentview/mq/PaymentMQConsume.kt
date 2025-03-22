package com.siupay.paymentview.mq

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.siupay.paymentview.domain.PaymentActivityDto
import com.siupay.paymentview.domain.TradeMessage
import com.siupay.paymentview.handel.MessageHandler
import com.siupay.paymentview.handel.PaymentMessageHandler
import com.siupay.paymentview.handel.RefundMessageHandler
import com.siupay.paymentview.util.JsonUtils
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Slf4j
@Component
@EnableBinding(Sink::class)
class PaymentMQConsume(private var mapper: ObjectMapper,
                       private var paymentMessageHandler: PaymentMessageHandler,
                       private var refundMessageHandler: RefundMessageHandler

    ) {

    val log : Logger = LoggerFactory.getLogger(PaymentMQConsume::class.java)

    companion object {
        private val HANDLER_MAP: MutableMap<String, MessageHandler<PaymentActivityDto>> = ConcurrentHashMap()
    }
    fun select(message: TradeMessage): MessageHandler<PaymentActivityDto>? {
        val tableName: String = message.getTableName()
        if (tableName.toLowerCase().contains("payment_order")) {
            if (!HANDLER_MAP.containsKey(tableName)) {
                HANDLER_MAP[tableName] = paymentMessageHandler
            }
        } else if (tableName.toLowerCase().contains("refund_order")) {
            if (!HANDLER_MAP.containsKey(tableName)) {
                HANDLER_MAP[tableName] = refundMessageHandler
            }
        }
        return HANDLER_MAP[tableName]
    }
    @StreamListener(Sink.INPUT)
    fun process(message: Message<*>) {
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        log.info("receive message:${message.payload}")
        val acknowledgment: Acknowledgment = message.headers.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment::class.java)
        if (acknowledgment == null) {
            log.warn("acknowledgement  is null")
            return
        }
        try {
            //处理中
            log.info("start to handle request ${message.headers.id}")
            val payData: String = mapper.writeValueAsString(message.payload)
            log.info("payData is $payData")
           //message.payload.toString()
           // var a  = "{\"table\":\"Payment.payment_order\",\"timestamp\":1615889030000,\"type\":0,\"binarybitmap\":4320133120,\"columns\":[\"payOrderId\",\"appId\",\"appOrderId\",\"appOrderTime\",\"chOrderId\",\"chId\",\"payChId\",\"payMethod\",\"bankId\",\"payCountry\",\"payBankId\",\"payChannelAcctId\",\"amount\",\"currency\",\"userType\",\"userId\",\"userName\",\"prodId\",\"prodName\",\"prodInfo\",\"attach\",\"createTime\",\"updateTime\",\"orderData\",\"payData\",\"returnUrl\",\"notifyUrl\",\"chDealId\",\"chDealTime\",\"extData\",\"statusCode\",\"statusMsg\",\"riskData\",\"orderType\",\"merchantId\",\"subChannel\",\"payAdaptor\"], \"pks\":[0],\"datas\":[[\"20210316180349801990060570000\",\"999\",\"40318356\",1615860228000,\"20210316180349801990060570000\",\"Checkout\",\"Checkout\",\"Gate\",\"\",\"\",\"\",\"\",620400,\"MYR\",\"shopline\",\"1234567\",\"test\",\"测试\",\"1 ssfrom\",\"\",\"\",1615860230000,1615860230000,\"eyJkZXNjcmlwdG9yTmFtZSI6InRlc3QiLCJkZXNjcmlwdG9yQ2l0eSI6IkhLIiwiZW5hYmxlM0QiOnRydWV9\",\"eyJyaXNrTGV2ZWwiOjAuMCwiY2hhbm5lbFBheVVybCI6Imh0dHBzOi8vcGEtYXBpLXByZXYuam95eWluYy5jb20vY2hhbm5lbC9DaGVja291dC9HYXRlLzk5OS9jWFpETUZGU1kwMWxka3c0Ynk5aVpEWkdaR0ZhZW13eVJHbG1SMHR4YVZoR1N6Tm9WRWxPU3psaGNGRnNTV3B6YXpKQ2QwbEZXV05VUlU5Qk1qSjRNRFZaY2xWdmMwYzVWMDluZVVKMlYweDJUR0UxYm1obVJURk9lSGRyVFc4dlJXRnRiM1JWYWt0VVZUaE5NVk14T1U1d1FWZ3hkejA5Iiwicmlza1Jlc3VsdCI6eyJlbmFibGUiOnRydWUsInJlc3VsdCI6eyJjaGVja1N1Y2Nlc3MiOnRydWUsInBhc3MiOnRydWUsImV4cGVjdFJpc2tMZXZlbCI6NzkuMCwicmlza0xldmVsIjowLjAsImRldGFpbCI6IntcImFudGlzZGtfbWFza1wiOlwiMFwiLFwiYW50aXNka19wY2lkXCI6XCIwXCIsXCJhcHBJZFwiOlwic2hvcGxpbmVfcmVjaGFyZ2VcIixcImFzc29jQWNjdFR5cGVcIjoyNTUuMCxcImRlY2lzaW9uXCI6XCIwXCIsXCJpcF9jb3VudHJ5X2NvZGVcIjpcIjBcIixcImlwX3Jpc2tfbWFza1wiOlwiMFwiLFwiaXNfY2hhbGxlbmdlXCI6XCIwXCIsXCJwZXJzb25hX3Jpc2tfbWFza1wiOlwiMFwiLFwicGhvbmVcIjpcIlwiLFwicGhvbmVfcmlza19tYXNrXCI6XCIwXCIsXCJyaWRcIjowLjAsXCJyaXNrX2xldmVsXCI6MC4wLFwidGVybVR5cGVcIjowLjAsXCJ0aW1lc3RhbXBcIjoxLjYxNTg4OTAzMDA5RTEyLFwidHJhY2VJZFwiOlwiMjAxMjU0Njk5NTA1ODAzMjY0XCIsXCJ1aWRcIjpcIjEyMzQ1NjdcIn0ifX0sInB1YmxpY0tleSI6InBrX3Rlc3RfNjU3YmQ1MTgtOWVhNy00ZDdmLWJkMzktYWRiMWFhZmE3MmMxIn0=\",\"\",\"http://requestbin.net/r/x1dse5x1\",\"\",null,\"\",\"PAYING\",\"checkout paying.\",\"eyJwcm9kdWN0TnVtIjoiMSIsImJ1c2luZXNzSWQiOiI3NDU3MjcyODI0ODM1NjA0NDgiLCJNQ0MiOiI1NjkxIiwibWVyY2hhbnRJZCI6Ijc0NTcyNzI4MjQ4MzU2MDQ0OCIsInN1YkFwcElkIjoiOTk5In0=\",\"\",745727282483560448,\"\",\"XPAY\"]]}"
            val tradeMessage: TradeMessage = mapper.readValue(message.payload.toString(), TradeMessage::class.java)
            log.info("trade data is ${tradeMessage.table}")
            //过滤表
            val messageHandler = select(tradeMessage)
            var result = true
            if (messageHandler != null) {
                log.info("message handler is $messageHandler")
                var realData: List<PaymentActivityDto> = messageHandler.getPersistList(tradeMessage)
                log.info("all data ready to persist are {}", JsonUtils.toJson(realData))
                if (realData.isNotEmpty()) {
                    result = messageHandler.messagePersist(realData)
                    log.info("persist result is $result")
                }
            }
            if (result) {
                acknowledgment.acknowledge()
            } else {
                log.error("error failed in metadata $payData")
                acknowledgment.acknowledge()
            }
       } catch (e: Exception) {
            log.error("process error ", e)
        }
    }

    /*fun testc() {
        mapper?.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            ?.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            ?.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        var a  =  "{\"table\":\"Payment.payment_order\",\"timestamp\":1616379128000,\"type\":1,\"binarybitmap\":4320133120,\"columns\":[\"payOrderId\",\"appId\",\"appOrderId\",\"appOrderTime\",\"chOrderId\",\"chId\",\"payChId\",\"payMethod\",\"bankId\",\"payCountry\",\"payBankId\",\"payChannelAcctId\",\"amount\",\"currency\",\"userType\",\"userId\",\"userName\",\"prodId\",\"prodName\",\"prodInfo\",\"attach\",\"createTime\",\"updateTime\",\"orderData\",\"payData\",\"returnUrl\",\"notifyUrl\",\"chDealId\",\"chDealTime\",\"extData\",\"statusCode\",\"statusMsg\",\"riskData\",\"orderType\",\"merchantId\",\"subChannel\",\"payAdaptor\"], \"pks\":[0],\"datas\":[[\"20210322100827483990170600000\",\"999\",\"848820\",1616350106000,\"20210322100827483990170600000\",\"Checkout\",\"Checkout\",\"Gate\",\"\",\"\",\"\",\"\",762300,\"HKD\",\"shopline\",\"12345678907\",\"test\",\"测试\",\"111\",\"\",\"\",1616350108000,1616350328000,\"eyJkZXNjcmlwdG9yTmFtZSI6InRlc3QiLCJkZXNjcmlwdG9yQ2l0eSI6IkhLIiwiZW5hYmxlM0QiOnRydWV9\",\"eyJyaXNrTGV2ZWwiOi0xLjAsInB1YmxpY0tleSI6InBrX3Rlc3RfNjU3YmQ1MTgtOWVhNy00ZDdmLWJkMzktYWRiMWFhZmE3MmMxIiwiY2hJZCI6IkNoZWNrb3V0IiwicGF5TWV0aG9kIjoiR2F0ZSIsInVzZXJJZCI6IjEyMzQ1Njc4OTA3IiwiY2hhbm5lbENvZGUiOiJudWxsIiwiY2hhbm5lbE1zZyI6Im51bGwiLCJwYXltZW50RXJyb3JDb2RlIjoiNTAwMCJ9\",\"\",\"http://requestbin.net/r/x1dse5x1\",\"\",null,\"\",\"FAIL\",\"Other-null\",\"eyJwcm9kdWN0TnVtIjoiMSIsImJ1c2luZXNzSWQiOiI3NDYzOTgwMDA1NDAwMjQ4MzMiLCJNQ0MiOiI1NjkxIiwibWVyY2hhbnRJZCI6Ijc0NjM5ODAwMDU0MDAyNDgzMyIsInN1YkFwcElkIjoiOTk5In0=\",\"\",746398000540024833,\"\",\"XPAY\"]],\"update-choice\":1,\"beforeChanges\":[{\"updateTime\":1616350108000,\"payData\":\"eyJyaXNrTGV2ZWwiOi0xLjAsImNoYW5uZWxQYXlVcmwiOiJodHRwczovL3BhLWFwaS1wcmV2Mi5qb3l5aW5jLmNvbS9jaGFubmVsL0NoZWNrb3V0L0dhdGUvOTk5L2FsZFhVa3MwVFdwNlEzRllhSGh1UVdoTVkwNWlVbXhMWjBJd1NFbFBjVXhNWTNodmVWQkdSbEpKWVhGMVRuaFlhR3hGTmxFcldDOTFSRmxLVlRsbE5rdEdaVmxvZGxoelRreDJiVWM0Y21Ka00zUmFPRVpHYW14dE9FVm1NbFp1Y1RFd0sydG5TM1J6V2xSWFZHeHFhV1I1ZGxobFVUMDkiLCJyaXNrUmVzdWx0Ijp7ImVuYWJsZSI6dHJ1ZSwicmVzdWx0Ijp7ImNoZWNrU3VjY2VzcyI6ZmFsc2UsInBhc3MiOnRydWUsImRldGFpbCI6Int9In19LCJwdWJsaWNLZXkiOiJwa190ZXN0XzY1N2JkNTE4LTllYTctNGQ3Zi1iZDM5LWFkYjFhYWZhNzJjMSJ9\",\"statusCode\":\"PAYING\",\"statusMsg\":\"checkout paying.\"}]}"
        var tradeMessage: TradeMessage = mapper!!.readValue(a, TradeMessage::class.java)
        log.info("trade data is {}", tradeMessage.table)
        //过滤表
        val messageHandler = select(tradeMessage)
        var result = true
        if (messageHandler != null) {
            log.info("message handler is {}", messageHandler.toString())
            var realData: List<PaymentActivityDto> = messageHandler.getPersistList(tradeMessage)
            log.info("all data ready to persist are {}", JsonUtils.toJson(realData))
            if (realData.isNotEmpty()) {
                result = messageHandler.messagePersist(realData)
                log.info("persist result is {}", result)
            }
        }
    }*/

}