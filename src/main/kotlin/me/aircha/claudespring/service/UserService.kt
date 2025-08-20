package me.aircha.claudespring.service

import me.aircha.claudespring.dto.LoginRequest
import me.aircha.claudespring.dto.SignupRequest
import me.aircha.claudespring.entity.User
import me.aircha.claudespring.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun signup(req: SignupRequest): User {
        if (userRepository.existsByEmail(req.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다")
        }
        val user = User(
            email = req.email,
            password = passwordEncoder.encode(req.password),
            nickname = req.nickname,
        )
        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun authenticate(req: LoginRequest): User {
        val user = userRepository.findByEmail(req.email)
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다") }
        if (!passwordEncoder.matches(req.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다")
        }
        return user
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): User = userRepository.findById(id)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다") }
}
