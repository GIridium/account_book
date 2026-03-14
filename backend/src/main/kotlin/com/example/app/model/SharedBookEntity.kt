package com.example.app.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "shared_books")
class SharedBookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 100, nullable = false)
    val name: String,

    @Column(name = "invite_code", length = 10, unique = true, nullable = false)
    val inviteCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    val creator: UserEntity,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)

