package me.aircha.claudespring.repository

import me.aircha.claudespring.entity.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
    fun findAllByUserId(userId: Long): List<Todo>
    fun findByIdAndUserId(id: Long, userId: Long): Optional<Todo>
    fun existsByIdAndUserId(id: Long, userId: Long): Boolean
}
