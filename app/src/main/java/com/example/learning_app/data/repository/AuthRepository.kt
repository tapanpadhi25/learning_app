package com.example.learning_app.data.repository

import kotlinx.coroutines.delay

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
}

class FakeAuthRepository : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(1500)
        return if (email == DEMO_EMAIL && password == DEMO_PASSWORD) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        }
    }

    companion object {
        const val DEMO_EMAIL = "test@gmail.com"
        const val DEMO_PASSWORD = "123456"
    }
}
