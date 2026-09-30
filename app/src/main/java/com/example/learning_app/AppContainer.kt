package com.example.learning_app

import android.content.Context
import com.example.learning_app.data.local.AppDatabase
import com.example.learning_app.data.remote.MockCourseApi
import com.example.learning_app.data.repository.AuthRepository
import com.example.learning_app.data.repository.CourseRepository
import com.example.learning_app.data.repository.FakeAuthRepository
import com.example.learning_app.data.repository.OfflineFirstCourseRepository

class AppContainer(context: Context) {

    private val database = AppDatabase.create(context)

    val courseRepository: CourseRepository =
        OfflineFirstCourseRepository(MockCourseApi(context), database.courseDao())

    val authRepository: AuthRepository = FakeAuthRepository()
}
