package com.siupay.paymentview.dto

import java.lang.System.out


data class PeriodNumber (val period:String,var number: Long)

open class Solution {
    fun kthLargestValue(matrix: Array<IntArray>, k: Int): Int {
        val n = matrix.size
        val m = matrix[0].size
        var result = IntArray(n * m)
        var index = 0
        for(i in 0..(m-1)){
            for(j in 0..(n-1)){
                result[index] = getXOR(matrix, i , j)
                index++
            }
        }

        result.sortDescending()
        return result[k]
    }

    fun getXOR(matrix: Array<IntArray>, a: Int, b: Int): Int{
        var result  = matrix[a][b]
        for(i in 0..(a-1)){
            for(j in 0..(b-1)){
                result = result.xor(matrix[i][j])
            }
        }
        return result
    }

    fun main(args: Array<String>) {
        val matrix = arrayOf(intArrayOf(5,2),intArrayOf(1,6))
        out.println(kthLargestValue(matrix, 1))
    }
}