package com.example.learning_app.data.repository

import com.example.learning_app.data.local.CourseDao
import com.example.learning_app.data.local.CourseEntity
import com.example.learning_app.data.local.CourseWithLessons
import com.example.learning_app.data.local.LessonEntity
import com.example.learning_app.data.model.Course
import com.example.learning_app.data.model.Lesson
import com.example.learning_app.data.remote.CourseApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface CourseRepository {
    fun observeCourses(): Flow<List<Course>>
    fun observeCourse(courseId: Int): Flow<Course?>
    suspend fun refreshCourses(): Result<Unit>
    suspend fun setLessonCompleted(lessonId: Int, completed: Boolean)
}

class OfflineFirstCourseRepository(
    private val api: CourseApi,
    private val dao: CourseDao
) : CourseRepository {

    override fun observeCourses(): Flow<List<Course>> =
        dao.observeCourses().map { courses -> courses.map { it.toCourse() } }

    override fun observeCourse(courseId: Int): Flow<Course?> =
        dao.observeCourse(courseId).map { it?.toCourse() }

    override suspend fun refreshCourses(): Result<Unit> = try {
        val courses = api.getCourses()
        dao.saveCourses(
            courses = courses.map { CourseEntity(it.id, it.title, it.instructor) },
            lessons = courses.flatMap { course ->
                course.lessons.mapIndexed { index, lesson ->
                    LessonEntity(lesson.id, course.id, lesson.title, index, lesson.completed)
                }
            }
        )
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun setLessonCompleted(lessonId: Int, completed: Boolean) {
        dao.updateLessonCompleted(lessonId, completed)
    }

    private fun CourseWithLessons.toCourse() = Course(
        id = course.id,
        title = course.title,
        instructor = course.instructor,
        lessons = lessons
            .sortedBy { it.position }
            .map { Lesson(it.id, it.title, it.isCompleted) }
    )
}
