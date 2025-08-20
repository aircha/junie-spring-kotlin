package me.aircha.claudespring.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import me.aircha.claudespring.dto.LoginRequest
import me.aircha.claudespring.dto.SessionUser
import me.aircha.claudespring.dto.SignupRequest
import me.aircha.claudespring.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class AuthController(
    private val userService: UserService,
) {

    @GetMapping("/signup")
    fun signupForm(model: Model): String {
        model.addAttribute("form", SignupRequest())
        return "signup"
    }

    @PostMapping("/signup")
    fun signup(@Valid @ModelAttribute("form") form: SignupRequest, bindingResult: BindingResult): String {
        if (bindingResult.hasErrors()) {
            return "signup"
        }
        try {
            userService.signup(form)
        } catch (ex: Exception) {
            bindingResult.reject("signup.failed", ex.message ?: "회원가입에 실패했습니다")
            return "signup"
        }
        return "redirect:/login"
    }

    @GetMapping("/login")
    fun loginForm(model: Model): String {
        model.addAttribute("form", LoginRequest())
        return "login"
    }

    @PostMapping("/login")
    fun login(
        @Valid @ModelAttribute("form") form: LoginRequest,
        bindingResult: BindingResult,
        request: HttpServletRequest,
    ): String {
        if (bindingResult.hasErrors()) {
            return "login"
        }
        return try {
            val user = userService.authenticate(form)
            val session = request.getSession(true)
            session.setAttribute("user", SessionUser(id = user.id!!, nickname = user.nickname))
            "redirect:/todos"
        } catch (ex: Exception) {
            bindingResult.reject("login.failed", ex.message ?: "로그인에 실패했습니다")
            "login"
        }
    }

    @GetMapping("/logout")
    fun logout(session: HttpSession): String {
        session.invalidate()
        return "redirect:/login"
    }
}
