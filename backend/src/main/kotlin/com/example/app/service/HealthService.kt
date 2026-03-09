package com.example.app.service

import com.example.app.model.HealthResponse
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class HealthService {
    fun health(): HealthResponse =
        HealthResponse(
            status = "ok",
            timestamp = Instant.now().toString()
        )
}