package com.siupay.paymentview.domain

import com.siupay.paymentview.dto.Amount
import java.io.Serializable
import java.util.ArrayList



data  class ChannelInfo(val channel :String ? =null,var orderNumber :Long,var refundNumber :Long, var amounts: ArrayList<Amount>):
    Serializable{

    companion object {
        private const val serialVersionUID = -1L
    }


    }

