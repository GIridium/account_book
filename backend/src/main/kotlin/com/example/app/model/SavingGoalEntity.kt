package com.example.app.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "saving_goals")
class SavingGoalEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 100, nullable = false)
    val name: String,

    @Column(name = "target_amount", precision = 12, scale = 2, nullable = false)
    val targetAmount: BigDecimal,

    @Column(name = "current_amount", precision = 12, scale = 2, nullable = false)
    var currentAmount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val deadline: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)

