# Claude Spring – 세션 기반 투두 리스트

세션(Session) 기반 회원가입/로그인 기능을 갖춘 Kotlin + Spring Boot 투두 리스트 애플리케이션입니다. 로그인한 사용자만 자신의 투두를 조회/등록/수정/삭제할 수 있도록 사용자별로 데이터가 격리됩니다. Thymeleaf 기반의 뷰를 제공하며, H2 인메모리 데이터베이스를 사용합니다.

## 주요 기능
- 회원가입 (/signup)
  - 이메일 중복 검사, 비밀번호 BCrypt 해시 저장, 닉네임 입력
  - 성공 시 /login 으로 리다이렉트
- 로그인 (/login)
  - 인증 성공 시 HttpSession에 SessionUser 저장(id, nickname)
  - 실패 시 오류 메시지 표시
- 로그아웃 (/logout)
  - 세션 무효화 후 /login으로 이동
- 투두(Todo)
  - 사용자별 Todo 소유 (User:Todo = 1:N)
  - 로그인한 사용자만 자신의 투두 목록을 조회/생성/수정/삭제
  - 목록/등록/수정 화면(Thymeleaf) 제공 및 REST API 제공
- 접근 제어
  - HandlerInterceptor로 /todos, /api/todos/** 접근 시 세션 검증 → 미로그인 시 /login으로 302 리다이렉트
- 로케일 고정
  - 한국어(ko_KR) 고정으로 화면의 한글 메시지 표준화

## 기술 스택
- Kotlin, Java 17
- Spring Boot 3.5.x
  - spring-boot-starter-web
  - spring-boot-starter-thymeleaf
  - spring-boot-starter-data-jpa
  - spring-boot-starter-validation
- Spring Security Crypto (BCryptPasswordEncoder만 사용)
- H2 Database (런타임, 인메모리)
- JUnit 5 + Spring Boot Test

## 프로젝트 구조 (요약)
- src/main/kotlin/me/aircha/claudespring
  - config: AuthConfig, AuthInterceptor, WebConfig, LocaleConfig
  - controller: AuthController, TodoController (REST), TodoViewController (Thymeleaf)
  - dto: AuthDtos, TodoDtos
  - entity: User, Todo
  - repository: UserRepository, TodoRepository
  - service: UserService, TodoService
- src/main/resources
  - templates: login.html, signup.html, list.html, create.html, update.html
  - static/css: styles.css
  - application.properties (H2, JPA 설정)
- src/test/kotlin/me/aircha/claudespring
  - 통합 테스트 (MockMvc)

## 실행 방법
사전 요구 사항: JDK 17 이상 설치

1) 애플리케이션 실행
- macOS/Linux: `./gradlew bootRun`
- Windows: `gradlew.bat bootRun`

2) 접속
- 웹: http://localhost:8080/login
- H2 콘솔: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:todosdb`
  - User Name: `sa`, Password: 빈 값

3) 초기 흐름
- 회원가입 → 로그인 → 투두 목록/등록/수정/삭제 이용

## 테스트 실행
- 전체 테스트: `./gradlew test`

## 주요 엔드포인트
- 뷰(Thymeleaf)
  - GET /login, POST /login
  - GET /signup, POST /signup
  - GET /logout
  - GET /todos (목록)
  - GET /todos/create (등록 화면), POST /todos (등록 처리)
  - GET /todos/{id}/edit (수정 화면), POST /todos/{id} (수정 처리)
  - POST /todos/{id}/toggle (완료 토글), POST /todos/{id}/delete (삭제)
- REST API (세션 필요, 미로그인 시 302 /login)
  - GET /api/todos
  - POST /api/todos
  - GET /api/todos/{id}
  - PUT /api/todos/{id}
  - DELETE /api/todos/{id}

## 도메인 모델
- User
  - email (unique), password (BCrypt), nickname
- Todo
  - title (NotBlank on create), description, isDone
  - user (ManyToOne, Lazy)

## 인증/인가
- 로그인 성공 시 HttpSession에 `SessionUser(id, nickname)` 저장
- `AuthInterceptor`가 `/todos`, `/todos/**`, `/api/todos/**`에 대해 세션 검증
- 미인증 접근 시 `/login`으로 리다이렉트(302)

## 환경 설정
- H2 인메모리 DB, 애플리케이션 시작 시 스키마 자동 생성/업데이트 (spring.jpa.hibernate.ddl-auto=update)
- SQL 로그 출력 및 포맷팅 활성화
- 로케일 고정(KR)

## 라이선스
본 저장소의 라이선스가 별도로 명시되지 않았다면, 내부 학습/실습용으로 사용하십시오.
