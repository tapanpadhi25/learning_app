package com.example.learning_app.presentation.dashboard

import Course
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(DashboardUiState())

    val uiState: StateFlow<DashboardUiState> =
        _uiState.asStateFlow()


    init {
        loadCourses()
    }


    fun loadCourses() {

        viewModelScope.launch {

            _uiState.value = DashboardUiState(
                isLoading = true
            )

            try {

                // Simulate API call
                delay(1000)

                val courses = listOf(

                    Course(
                        id = 1,
                        title = "Python Programming",
                        instructor = "John Smith",
                        progress = 65,
                        lessons = 20
                    ),

                    Course(
                        id = 2,
                        title = "Generative AI",
                        instructor = "Sarah Williams",
                        progress = 40,
                        lessons = 16
                    ),

                    Course(
                        id = 3,
                        title = "Full Stack Development",
                        instructor = "David Brown",
                        progress = 25,
                        lessons = 28
                    )
                )

                _uiState.value =
                    DashboardUiState(
                        isLoading = false,
                        courses = courses
                    )

            } catch (e: Exception) {

                _uiState.value =
                    DashboardUiState(
                        isLoading = false,
                        errorMessage =
                        "Unable to load courses"
                    )
            }
        }
    }
}