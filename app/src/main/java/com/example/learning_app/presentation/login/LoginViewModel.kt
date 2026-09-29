package com.example.learning_app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learning_app.data.model.LoginStateUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginStateUI())

    val uiState: StateFlow<LoginStateUI> =
        _uiState.asStateFlow()

    fun onEmailChange(email: String) {

        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = null,
            errorMessage = null
        )
    }

    fun onPasswordChange(password: String) {

        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null,
            errorMessage = null
        )
    }

    fun login() {

        val currentState = _uiState.value

        val emailError = validateEmail(
            currentState.email
        )

        val passwordError = validatePassword(
            currentState.password
        )

        if (emailError != null || passwordError != null) {

            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError
            )

            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            delay(1500)

            if (
                currentState.email == "test@gmail.com" &&
                currentState.password == "123456"
            ) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccess = true
                )

            } else {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Invalid email or password"
                )
            }
        }
    }

    private fun validateEmail(email: String): String? {

        if (email.isBlank()) {
            return "Email is required"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            return "Enter a valid email"
        }

        return null
    }

    private fun validatePassword(password: String): String? {

        if (password.isBlank()) {
            return "Password is required"
        }

        if (password.length < 6) {
            return "Password must contain at least 6 characters"
        }

        return null
    }
}