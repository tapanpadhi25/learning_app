package com.example.learning_app.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException

class MockCourseApi(private val context: Context) : CourseApi {

    override suspend fun getCourses(): List<CourseDto> = withContext(Dispatchers.IO) {
        delay(1000)
        if (!isOnline()) throw IOException("No internet connection")
        val json = context.assets.open("courses.json").bufferedReader().use { it.readText() }
        parseCourses(json)
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun parseCourses(json: String): List<CourseDto> {
        val coursesArray = JSONArray(json)
        return List(coursesArray.length()) { i ->
            val course = coursesArray.getJSONObject(i)
            val lessonsArray = course.getJSONArray("lessons")
            CourseDto(
                id = course.getInt("id"),
                title = course.getString("title"),
                instructor = course.getString("instructor"),
                lessons = List(lessonsArray.length()) { j ->
                    val lesson = lessonsArray.getJSONObject(j)
                    LessonDto(
                        id = lesson.getInt("id"),
                        title = lesson.getString("title"),
                        completed = lesson.getBoolean("completed")
                    )
                }
            )
        }
    }
}
