package com.siupay.paymentview.dto

import java.util.ArrayList

data  class ChannelNumberAndAmount(val channel :String,var number :Long, var amounts: ArrayList<Amount>)