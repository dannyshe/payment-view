package com.siupay.paymentview.filter

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SimpleCORSFilter : Filter {

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
        //log.info("run SimpleCORSFilter init");
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val response = res as HttpServletResponse
        val request = req as HttpServletRequest
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE")
        response.setHeader(
            "Access-Control-Allow-Headers",
            "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization,token,lang"
        )
        if (request.method == HttpMethod.OPTIONS.name) {
            response.status = HttpStatus.NO_CONTENT.value()
        }
        chain.doFilter(req, res)
    }

    override fun destroy() {}
}