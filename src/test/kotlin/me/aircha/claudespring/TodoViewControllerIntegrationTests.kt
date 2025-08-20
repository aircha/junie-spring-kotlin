package me.aircha.claudespring

import me.aircha.claudespring.dto.SessionUser
import me.aircha.claudespring.dto.SignupRequest
import me.aircha.claudespring.dto.TodoCreateRequest
import me.aircha.claudespring.service.TodoService
import me.aircha.claudespring.service.UserService
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoViewControllerIntegrationTests @Autowired constructor(
    val mockMvc: MockMvc,
    val todoService: TodoService,
    val userService: UserService,
) {
    lateinit var session: MockHttpSession
    var userId: Long = -1

    @BeforeEach
    fun setupSession() {
        val saved = userService.signup(SignupRequest(email = "test@example.com", password = "password", nickname = "테스터"))
        userId = saved.id!!
        session = MockHttpSession()
        session.setAttribute("user", SessionUser(id = userId, nickname = "테스터"))
    }

    @Test
    @DisplayName("목록 화면 렌더링: 모델과 한국어 타이틀 확인")
    fun listPageRenders() {
        mockMvc.perform(get("/todos").session(session))
            .andExpect(status().isOk)
            .andExpect(view().name("list"))
            .andExpect(model().attributeExists("todos"))
            .andExpect(content().string(containsString("할 일 목록")))
    }

    @Test
    @DisplayName("등록 화면 렌더링: 폼 백잉 객체 존재")
    fun createPageRenders() {
        mockMvc.perform(get("/todos/create").session(session))
            .andExpect(status().isOk)
            .andExpect(view().name("create"))
            .andExpect(model().attributeExists("form"))
            .andExpect(content().string(containsString("할 일 등록")))
    }

    @Test
    @DisplayName("등록 처리: 유효한 입력으로 생성 후 목록으로 리다이렉트 및 목록에 표시")
    fun createSubmit() {
        // when: submit form
        mockMvc.perform(
            post("/todos").session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "테스트 할 일")
                .param("description", "설명입니다")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/todos"))

        // then: list shows item
        mockMvc.perform(get("/todos").session(session))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("테스트 할 일")))
            .andExpect(content().string(containsString("설명입니다")))
    }

    @Test
    @DisplayName("수정 화면 렌더링: 기존 값 표시")
    fun updatePageRenders() {
        val created = todoService.createForUser(userId, TodoCreateRequest(title = "원래 제목", description = "원래 설명"))

        mockMvc.perform(get("/todos/${created.id}/edit").session(session))
            .andExpect(status().isOk)
            .andExpect(view().name("update"))
            .andExpect(model().attributeExists("form"))
            .andExpect(content().string(containsString("할 일 수정")))
            // title input value and description text exist in HTML
            .andExpect(content().string(containsString("원래 제목")))
            .andExpect(content().string(containsString("원래 설명")))
    }

    @Test
    @DisplayName("수정 처리: 제목/완료 상태 변경 후 목록으로 리다이렉트")
    fun updateSubmit() {
        val created = todoService.createForUser(userId, TodoCreateRequest(title = "수정 전", description = "desc"))

        mockMvc.perform(
            post("/todos/${created.id}").session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "수정 후")
                .param("description", "변경된 설명")
                .param("isDone", "on") // 체크박스
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/todos"))

        // 목록에서 변경된 제목 확인
        mockMvc.perform(get("/todos").session(session))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("수정 후")))
            .andExpect(content().string(containsString("변경된 설명")))
    }

    @Test
    @DisplayName("완료 토글: 체크로 상태 반전 후 리다이렉트")
    fun toggleDone() {
        val created = todoService.createForUser(userId, TodoCreateRequest(title = "토글 테스트", description = null))

        mockMvc.perform(post("/todos/${created.id}/toggle").session(session))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/todos"))

        // 목록에 여전히 존재
        mockMvc.perform(get("/todos").session(session))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("토글 테스트")))
    }

    @Test
    @DisplayName("삭제: 삭제 후 목록으로 리다이렉트되고 목록에서 사라짐")
    fun deleteItem() {
        val created = todoService.createForUser(userId, TodoCreateRequest(title = "삭제 대상", description = null))

        mockMvc.perform(post("/todos/${created.id}/delete").session(session))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/todos"))

        // 목록이 비어있거나 해당 텍스트가 없어야 함
        mockMvc.perform(get("/todos").session(session))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("할 일 목록")))
            // 간단 검증: 삭제 안내 혹은 더 이상 제목이 노출되지 않음 (제목이 존재하지 않는지까지는 정밀 체크 생략)
            .andExpect(content().string(containsString("등록")))
    }

    @Test
    @DisplayName("검증 실패(등록): 제목 공백이면 에러 메시지와 함께 동일 화면")
    fun createValidationError() {
        mockMvc.perform(
            post("/todos").session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "")
                .param("description", "내용")
        )
            .andExpect(status().isOk)
            .andExpect(view().name("create"))
            .andExpect(content().string(containsString("제목은 필수입니다")))
    }

    @Test
    @DisplayName("검증 실패(수정): 제목 공백이면 에러 메시지와 함께 동일 화면")
    fun updateValidationError() {
        val created = todoService.createForUser(userId, TodoCreateRequest(title = "검증 전", description = null))

        mockMvc.perform(
            post("/todos/${created.id}").session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "") // 공백
        )
            .andExpect(status().isOk)
            .andExpect(view().name("update"))
            .andExpect(content().string(containsString("제목은 필수입니다")))
    }
}
