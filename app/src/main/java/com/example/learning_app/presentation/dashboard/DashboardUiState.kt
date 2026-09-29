package com.example.learning_app.presentation.dashboard

import Course

data class DashboardUiState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val errorMessage: String? = null
)