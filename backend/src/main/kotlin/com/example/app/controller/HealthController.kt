package com.example.app.controller

import com.example.app.model.HealthResponse
import com.example.app.service.HealthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HealthController(
    private val healthService: HealthService
) {
    @GetMapping("/health")
    fun health(): HealthResponse = healthService.health()
}