package me.aircha.claudespring.dto

import jakarta.validation.constraints.NotBlank
import me.aircha.claudespring.entity.Todo

// Requests
data class TodoCreateRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    val title: String,
    val description: String? = null,
)

data class TodoUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val isDone: Boolean? = null,
)

// Response
data class TodoResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val isDone: Boolean,
)

fun Todo.toResponse(): TodoResponse = TodoResponse(
    id = requireNotNull(this.id) { "응답으로 매핑할 때 Todo id는 null일 수 없습니다" },
    title = this.title,
    description = this.description,
    isDone = this.isDone,
)
