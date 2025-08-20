package me.aircha.claudespring.controller

import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import me.aircha.claudespring.dto.SessionUser
import me.aircha.claudespring.dto.TodoCreateRequest
import me.aircha.claudespring.dto.TodoUpdateRequest
import me.aircha.claudespring.service.TodoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
class TodoViewController(
    private val todoService: TodoService,
) {

    @GetMapping("/")
    fun index(): String = "redirect:/todos"

    @GetMapping("/todos")
    fun list(model: Model, session: HttpSession): String {
        val user = session.getAttribute("user") as SessionUser
        val todos = todoService.findAllByUser(user.id)
        model.addAttribute("todos", todos)
        model.addAttribute("nickname", user.nickname)
        return "list"
    }

    @GetMapping("/todos/create")
    fun showCreateForm(model: Model): String {
        model.addAttribute("form", TodoCreateRequest(title = "", description = null))
        return "create"
    }

    @PostMapping("/todos")
    fun create(
        @Valid @ModelAttribute("form") form: TodoCreateRequest,
        bindingResult: BindingResult,
        model: Model,
        session: HttpSession,
    ): String {
        if (bindingResult.hasErrors()) {
            return "create"
        }
        val user = session.getAttribute("user") as SessionUser
        todoService.createForUser(user.id, form)
        return "redirect:/todos"
    }

    @GetMapping("/todos/{id}/edit")
    fun showUpdateForm(@PathVariable id: Long, model: Model, session: HttpSession): String {
        val user = session.getAttribute("user") as SessionUser
        val current = todoService.findByIdForUser(user.id, id)
        model.addAttribute("id", id)
        model.addAttribute(
            "form",
            TodoUpdateRequest(
                title = current.title,
                description = current.description,
                isDone = current.isDone,
            )
        )
        return "update"
    }

    @PostMapping("/todos/{id}")
    fun update(
        @PathVariable id: Long,
        @ModelAttribute("form") form: TodoUpdateRequest,
        bindingResult: BindingResult,
        model: Model,
        session: HttpSession,
    ): String {
        // Validate title not blank if provided
        if (form.title != null && form.title!!.isBlank()) {
            bindingResult.rejectValue("title", "NotBlank", "제목은 필수입니다")
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id)
            return "update"
        }
        val user = session.getAttribute("user") as SessionUser
        todoService.updateForUser(user.id, id, form)
        return "redirect:/todos"
    }

    @PostMapping("/todos/{id}/toggle")
    fun toggleDone(@PathVariable id: Long, session: HttpSession): String {
        val user = session.getAttribute("user") as SessionUser
        todoService.toggleForUser(user.id, id)
        return "redirect:/todos"
    }

    @PostMapping("/todos/{id}/delete")
    fun delete(@PathVariable id: Long, session: HttpSession): String {
        val user = session.getAttribute("user") as SessionUser
        todoService.deleteForUser(user.id, id)
        return "redirect:/todos"
    }
}