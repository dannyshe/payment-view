package com.siupay.paymentview.dto

import java.math.BigDecimal
import java.util.ArrayList


data  class OrderAmount(val currency :String,var amount :BigDecimal, var periodNumbers: ArrayList<com.siupay.paymentview.dto.PeriodNumber>)

