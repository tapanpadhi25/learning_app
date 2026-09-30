package com.example.learning_app.data.repository

import com.example.learning_app.data.local.CourseDao
import com.example.learning_app.data.local.CourseEntity
import com.example.learning_app.data.local.CourseWithLessons
import com.example.learning_app.data.local.LessonEntity
import com.example.learning_app.data.remote.CourseApi
import com.example.learning_app.data.remote.CourseDto
import com.example.learning_app.data.remote.LessonDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class OfflineFirstCourseRepositoryTest {

    private val api = FakeCourseApi()
    private val repository = OfflineFirstCourseRepository(api, FakeCourseDao())

    @Test
    fun `cached courses are still available when refresh fails offline`() = runTest {
        repository.refreshCourses()
        api.isOnline = false

        val result = repository.refreshCourses()

        assertTrue(result.isFailure)
        assertEquals(listOf("Python Programming"), repository.observeCourses().first().map { it.title })
    }

    @Test
    fun `completing a lesson updates course progress and survives refresh`() = runTest {
        repository.refreshCourses()
        assertEquals(50, repository.observeCourse(1).first()!!.progress)

        repository.setLessonCompleted(lessonId = 103, completed = true)
        repository.refreshCourses()

        val course = repository.observeCourse(1).first()!!
        assertEquals(75, course.progress)
        assertTrue(course.lessons.first { it.id == 103 }.isCompleted)
    }

    private class FakeCourseApi : CourseApi {
        var isOnline = true

        override suspend fun getCourses(): List<CourseDto> {
            if (!isOnline) throw IOException("No internet connection")
            return listOf(
                CourseDto(
                    id = 1,
                    title = "Python Programming",
                    instructor = "John Smith",
                    lessons = listOf(
                        LessonDto(101, "Introduction", true),
                        LessonDto(102, "Variables & Data Types", true),
                        LessonDto(103, "Functions", false),
                        LessonDto(104, "OOP", false)
                    )
                )
            )
        }
    }

    private class FakeCourseDao : CourseDao() {
        private val courses = MutableStateFlow<Map<Int, CourseEntity>>(emptyMap())
        private val lessons = MutableStateFlow<Map<Int, LessonEntity>>(emptyMap())

        override fun observeCourses(): Flow<List<CourseWithLessons>> =
            combine(courses, lessons) { courseMap, lessonMap ->
                courseMap.values.sortedBy { it.id }.map { course ->
                    CourseWithLessons(course, lessonMap.values.filter { it.courseId == course.id })
                }
            }

        override fun observeCourse(courseId: Int): Flow<CourseWithLessons?> =
            observeCourses().map { list -> list.firstOrNull { it.course.id == courseId } }

        override suspend fun upsertCourses(courses: List<CourseEntity>) {
            this.courses.update { current -> current + courses.associateBy { it.id } }
        }

        override suspend fun insertLessons(lessons: List<LessonEntity>) {
            this.lessons.update { current ->
                current + lessons.filterNot { it.id in current }.associateBy { it.id }
            }
        }

        override suspend fun updateLessonCompleted(lessonId: Int, completed: Boolean) {
            lessons.update { current ->
                val lesson = current[lessonId] ?: return@update current
                current + (lessonId to lesson.copy(isCompleted = completed))
            }
        }
    }
}
