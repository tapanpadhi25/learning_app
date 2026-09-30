package com.example.learning_app.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.learning_app.LearningApp
import com.example.learning_app.data.model.Course
import com.example.learning_app.data.repository.CourseRepository
import com.example.learning_app.navigation.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CourseDetailsUiState(
    val isLoading: Boolean = true,
    val course: Course? = null
)

class CourseDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: CourseRepository
) : ViewModel() {

    private val courseId: Int = checkNotNull(savedStateHandle[Screen.CourseDetails.ARG_COURSE_ID])

    val uiState: StateFlow<CourseDetailsUiState> = repository.observeCourse(courseId)
        .map { CourseDetailsUiState(isLoading = false, course = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CourseDetailsUiState())

    fun onLessonCompletedChange(lessonId: Int, completed: Boolean) {
        viewModelScope.launch {
            repository.setLessonCompleted(lessonId, completed)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as LearningApp
                CourseDetailsViewModel(createSavedStateHandle(), app.container.courseRepository)
            }
        }
    }
}
