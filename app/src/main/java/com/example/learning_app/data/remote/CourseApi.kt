package com.example.learning_app.data.remote

interface CourseApi {
    suspend fun getCourses(): List<CourseDto>
}

data class CourseDto(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessons: List<LessonDto>
)

data class LessonDto(
    val id: Int,
    val title: String,
    val completed: Boolean
)
