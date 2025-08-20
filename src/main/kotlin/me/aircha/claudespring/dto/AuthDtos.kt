package me.aircha.claudespring.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

// Signup
data class SignupRequest(
    @field:Email(message = "유효한 이메일을 입력하세요")
    @field:NotBlank(message = "이메일은 필수입니다")
    val email: String = "",

    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String = "",

    @field:NotBlank(message = "닉네임은 필수입니다")
    val nickname: String = "",
)

// Login
data class LoginRequest(
    @field:Email(message = "유효한 이메일을 입력하세요")
    @field:NotBlank(message = "이메일은 필수입니다")
    val email: String = "",

    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String = "",
)

// Session user (stored in HTTP session)
data class SessionUser(
    val id: Long,
    val nickname: String,
)
