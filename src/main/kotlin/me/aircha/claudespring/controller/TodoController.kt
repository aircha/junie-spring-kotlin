package me.aircha.claudespring.controller

import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import me.aircha.claudespring.dto.SessionUser
import me.aircha.claudespring.dto.TodoCreateRequest
import me.aircha.claudespring.dto.TodoResponse
import me.aircha.claudespring.dto.TodoUpdateRequest
import me.aircha.claudespring.service.TodoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todos")
class TodoController(
    private val todoService: TodoService,
) {

    @PostMapping
    fun create(@Valid @RequestBody req: TodoCreateRequest, session: HttpSession): ResponseEntity<TodoResponse> {
        val user = session.getAttribute("user") as SessionUser
        val created = todoService.createForUser(user.id, req)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping
    fun getAll(session: HttpSession): List<TodoResponse> {
        val user = session.getAttribute("user") as SessionUser
        return todoService.findAllByUser(user.id)
    }

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long, session: HttpSession): TodoResponse {
        val user = session.getAttribute("user") as SessionUser
        return todoService.findByIdForUser(user.id, id)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: TodoUpdateRequest, session: HttpSession): TodoResponse {
        val user = session.getAttribute("user") as SessionUser
        return todoService.updateForUser(user.id, id, req)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long, session: HttpSession): ResponseEntity<Void> {
        val user = session.getAttribute("user") as SessionUser
        todoService.deleteForUser(user.id, id)
        return ResponseEntity.noContent().build()
    }
}
