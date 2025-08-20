package me.aircha.claudespring.service

import me.aircha.claudespring.dto.TodoCreateRequest
import me.aircha.claudespring.dto.TodoResponse
import me.aircha.claudespring.dto.TodoUpdateRequest
import me.aircha.claudespring.dto.toResponse
import me.aircha.claudespring.entity.Todo
import me.aircha.claudespring.repository.TodoRepository
import me.aircha.claudespring.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class TodoService(
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository,
) {

    // Legacy methods (not user-scoped). Kept for compatibility in tests or future use
    @Transactional
    fun create(req: TodoCreateRequest): TodoResponse {
        val entity = Todo(
            title = req.title,
            description = req.description,
            isDone = false,
        )
        return todoRepository.save(entity).toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<TodoResponse> = todoRepository.findAll()
        .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: Long): TodoResponse = getEntity(id).toResponse()

    @Transactional
    fun update(id: Long, req: TodoUpdateRequest): TodoResponse {
        val entity = getEntity(id)
        req.title?.let { entity.title = it }
        if (req.description != null) entity.description = req.description
        if (req.isDone != null) entity.isDone = req.isDone
        return entity.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        if (!todoRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "할 일을 찾을 수 없습니다: $id")
        }
        todoRepository.deleteById(id)
    }

    // User-scoped methods
    @Transactional
    fun createForUser(userId: Long, req: TodoCreateRequest): TodoResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다: $userId") }
        val entity = Todo(
            title = req.title,
            description = req.description,
            isDone = false,
            user = user,
        )
        return todoRepository.save(entity).toResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByUser(userId: Long): List<TodoResponse> = todoRepository.findAllByUserId(userId)
        .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findByIdForUser(userId: Long, id: Long): TodoResponse = getEntityForUser(userId, id).toResponse()

    @Transactional
    fun updateForUser(userId: Long, id: Long, req: TodoUpdateRequest): TodoResponse {
        val entity = getEntityForUser(userId, id)
        req.title?.let { entity.title = it }
        if (req.description != null) entity.description = req.description
        if (req.isDone != null) entity.isDone = req.isDone
        return entity.toResponse()
    }

    @Transactional
    fun toggleForUser(userId: Long, id: Long) {
        val entity = getEntityForUser(userId, id)
        entity.isDone = !entity.isDone
    }

    @Transactional
    fun deleteForUser(userId: Long, id: Long) {
        if (!todoRepository.existsByIdAndUserId(id, userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "할 일을 찾을 수 없습니다: $id")
        }
        todoRepository.deleteById(id)
    }

    private fun getEntity(id: Long): Todo = todoRepository.findById(id)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "할 일을 찾을 수 없습니다: $id") }

    private fun getEntityForUser(userId: Long, id: Long): Todo = todoRepository.findByIdAndUserId(id, userId)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "할 일을 찾을 수 없습니다: $id") }
}
