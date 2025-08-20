package me.aircha.claudespring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClaudeSpringApplication

fun main(args: Array<String>) {
    runApplication<ClaudeSpringApplication>(*args)
}
