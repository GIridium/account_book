package com.example.app.repository

import com.example.app.model.BillEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BillRepository : JpaRepository<BillEntity, Long> {
    // Used to list all bills, ordering by date desc
    @Query("SELECT b FROM BillEntity b LEFT JOIN FETCH b.category LEFT JOIN FETCH b.user ORDER BY b.billDate DESC")
    fun findAllWithDetails(): List<BillEntity>
}

