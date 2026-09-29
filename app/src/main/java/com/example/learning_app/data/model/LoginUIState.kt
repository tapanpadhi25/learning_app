package com.example.learning_app.data.model

data class LoginStateUI(
    val email: String = "",
    val password: String = "",

    val emailError: String? = null,
    val passwordError: String? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val isLoginSuccess: Boolean = false,

    )