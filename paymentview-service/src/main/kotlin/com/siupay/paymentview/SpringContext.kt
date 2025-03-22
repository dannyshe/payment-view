package com.siupay.paymentview

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
@Lazy(false)
class SpringContext : ApplicationContextAware {
    private var applicationContext: ApplicationContext? = null

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    companion object {
        val applicationContext: ApplicationContext? = null

        fun getBean(name: String?): Any {
            return applicationContext!!.getBean(name)
        }

        fun <T> getBean(clazz: Class<T>?): T {
            return applicationContext!!.getBean(clazz)
        }

        fun <T> getBean(name: String?, clazz: Class<T>?): T {
            return applicationContext!!.getBean(name, clazz)
        }
    }
}