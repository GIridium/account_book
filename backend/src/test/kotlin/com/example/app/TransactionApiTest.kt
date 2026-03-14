package com.example.app

import com.example.app.dto.CreateTransactionRequest
import com.example.app.dto.UpdateTransactionRequest
import com.example.app.model.Transaction
import com.example.app.repository.CategoryRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback after each test to keep DB clean
@org.springframework.test.context.TestPropertySource(properties = ["spring.flyway.clean-disabled=false"])
class TransactionApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    private val objectMapper = jacksonObjectMapper().findAndRegisterModules()

    @org.springframework.boot.test.context.TestConfiguration
    class FlywayConfig {
        @org.springframework.context.annotation.Bean
        fun cleanMigrateStrategy(): org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy {
            return org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy { flyway ->
                flyway.clean()
                flyway.migrate()
            }
        }
    }

    @Test
    fun `should create, retrieve, update and delete a transaction`() {
        // 1. Setup: verify category exists (from migration script)
        val category = categoryRepository.findByName("餐饮")
        assertNotNull(category, "Default category '餐饮' should exist from V1__Init_Schema.sql")
        val categoryId = category!!.id!!

        // 2. Create Transaction
        val createReq = CreateTransactionRequest(
            amount = BigDecimal("100.50"),
            categoryId = categoryId,
            remark = "Lunch with friends",
            merchant = "Tasty Restaurant",
            date = LocalDate.now()
        )

        val createResult = mockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.amount").value(100.50))
            .andExpect(jsonPath("$.categoryId").value(categoryId))
            .andExpect(jsonPath("$.remark").value("Lunch with friends"))
            .andExpect(jsonPath("$.merchant").value("Tasty Restaurant"))
            .andReturn()

        val createdTransaction = objectMapper.readValue(
            createResult.response.contentAsString,
            Transaction::class.java
        )
        val transactionId = createdTransaction.id

        // 3. Get By ID
        mockMvc.perform(get("/api/transactions/$transactionId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(transactionId))
            .andExpect(jsonPath("$.remark").value("Lunch with friends"))

        // 4. Update Transaction
        val updateReq = UpdateTransactionRequest(
            amount = BigDecimal("200.00"),
            categoryId = categoryId,
            remark = "Updated Lunch",
            merchant = "Fancy Restaurant",
            date = LocalDate.now().minusDays(1)
        )

        mockMvc.perform(
            put("/api/transactions/$transactionId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.amount").value(200.00))
            .andExpect(jsonPath("$.remark").value("Updated Lunch"))

        // 5. Delete Transaction
        mockMvc.perform(delete("/api/transactions/$transactionId"))
            .andExpect(status().isNoContent)

        // 6. Verify Deletion (Get should 404)
        // Wait, current implementation might throw 500 or 404 depending on how findById handles empty.
        // Let's check service logic: .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, ...) }
        mockMvc.perform(get("/api/transactions/$transactionId"))
            .andExpect(status().isNotFound)
    }
}

