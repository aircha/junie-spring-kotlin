package me.aircha.claudespring.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val authInterceptor: AuthInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor)
            .addPathPatterns("/todos", "/todos/**", "/api/todos/**")
            .excludePathPatterns(
                "/login",
                "/signup",
                "/h2-console/**",
                "/css/**",
                "/js/**",
                "/images/**"
            )
    }
}
