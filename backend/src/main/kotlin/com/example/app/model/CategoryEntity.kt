package com.example.app.model

import jakarta.persistence.*

@Entity
@Table(name = "categories")
class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 50, nullable = false)
    val name: String,

    @Column(length = 50)
    val icon: String? = null,

    // Null means system default category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity? = null
)

