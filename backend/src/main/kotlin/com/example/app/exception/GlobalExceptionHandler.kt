package com.example.app.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to ex.statusCode.value(),
            "error" to (ex.reason ?: "Error"),
            "message" to (ex.reason ?: "An error occurred")
        )
        return ResponseEntity(body, ex.statusCode)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Validation Failed",
            "message" to errors
        )
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Internal Server Error",
            "message" to (ex.message ?: "Unknown error")
        )
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

