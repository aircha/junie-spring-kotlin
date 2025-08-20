package me.aircha.claudespring.entity

import jakarta.persistence.*

@Entity
@Table(name = "users", indexes = [Index(columnList = "email", unique = true)])
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false)
    var nickname: String = "",
)
