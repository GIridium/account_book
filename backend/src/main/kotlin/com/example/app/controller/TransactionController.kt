package com.example.app.controller

import com.example.app.dto.CreateTransactionRequest
import com.example.app.dto.UpdateTransactionRequest
import com.example.app.model.Transaction
import com.example.app.service.TransactionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val service: TransactionService
) {
    @GetMapping
    fun list(): List<Transaction> = service.findAll()

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): Transaction = service.findById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateTransactionRequest): Transaction =
        service.create(req)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: UpdateTransactionRequest
    ): Transaction = service.update(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        service.delete(id)
    }
}