package com.example.app.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, length = 50, nullable = false)
    val username: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @Column(length = 50)
    val nickname: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)

