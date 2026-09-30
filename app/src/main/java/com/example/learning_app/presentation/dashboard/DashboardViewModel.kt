package com.example.learning_app.presentation.dashboard

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.learning_app.LearningApp
import com.example.learning_app.R
import com.example.learning_app.data.model.Course
import com.example.learning_app.data.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

data class DashboardUiState(
    val isLoading: Boolean = true,
    val courses: List<Course> = emptyList(),
    @StringRes val errorMessage: Int? = null
)

class DashboardViewModel(private val repository: CourseRepository) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val refreshError = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.observeCourses(),
        isRefreshing,
        refreshError
    ) { courses, refreshing, error ->
        DashboardUiState(isLoading = refreshing, courses = courses, errorMessage = error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    init {
        refresh()
    }

    fun refresh() {
        if (isRefreshing.value) return
        viewModelScope.launch {
            isRefreshing.value = true
            refreshError.value = null
            repository.refreshCourses().onFailure { error ->
                refreshError.value = if (error is IOException) {
                    R.string.error_no_internet
                } else {
                    R.string.error_load_courses
                }
            }
            isRefreshing.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as LearningApp
                DashboardViewModel(app.container.courseRepository)
            }
        }
    }
}
