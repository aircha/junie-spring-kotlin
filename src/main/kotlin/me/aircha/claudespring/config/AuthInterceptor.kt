package me.aircha.claudespring.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.aircha.claudespring.dto.SessionUser
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val session = request.getSession(false)
        val user = session?.getAttribute("user") as? SessionUser
        if (user == null) {
            response.sendRedirect("/login")
            return false
        }
        return true
    }
}
