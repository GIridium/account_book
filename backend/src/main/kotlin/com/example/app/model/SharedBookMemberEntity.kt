package com.example.app.model

import jakarta.persistence.*
import java.time.Instant

enum class MemberRole {
    OWNER, EDITOR
}

@Entity
@Table(name = "shared_book_members")
class SharedBookMemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_book_id", nullable = false)
    val sharedBook: SharedBookEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: MemberRole,

    @Column(name = "joined_at", nullable = false)
    val joinedAt: Instant = Instant.now()
)

