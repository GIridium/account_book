package com.example.app.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "bills")
class BillEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(precision = 12, scale = 2, nullable = false)
    var amount: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: CategoryEntity,

    @Column(name = "bill_date", nullable = false)
    var billDate: LocalDate,

    @Column(length = 255)
    var remark: String? = null,

    @Column(length = 100)
    var merchant: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_book_id")
    var sharedBook: SharedBookEntity? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
) {
    fun toTransaction(): Transaction {
        return Transaction(
            id = id!!,
            amount = amount,
            categoryId = category.id!!,
            categoryName = category.name,
            remark = remark,
            merchant = merchant,
            date = billDate,
            createdAt = createdAt
        )
    }
}
