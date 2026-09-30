package com.example.learning_app.data.model

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessons: List<Lesson>
) {
    val totalLessons: Int get() = lessons.size

    val completedLessons: Int get() = lessons.count { it.isCompleted }

    val progress: Int
        get() = if (totalLessons == 0) 0 else completedLessons * 100 / totalLessons
}

data class Lesson(
    val id: Int,
    val title: String,
    val isCompleted: Boolean
)
