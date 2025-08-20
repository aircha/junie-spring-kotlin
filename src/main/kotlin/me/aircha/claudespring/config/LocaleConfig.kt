package me.aircha.claudespring.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.FixedLocaleResolver
import java.util.Locale

@Configuration
class LocaleConfig {
    @Bean
    fun localeResolver(): LocaleResolver {
        // 한국어(대한민국)로 로케일 고정
        return FixedLocaleResolver(Locale.KOREA)
    }
}
