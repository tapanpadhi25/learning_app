package com.example.learning_app.presentation.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learning_app.R
import com.example.learning_app.data.model.Course
import com.example.learning_app.data.model.Lesson
import com.example.learning_app.ui.components.AppTopBar
import com.example.learning_app.ui.theme.AppStyle

@Composable
fun CourseDetailsScreen(
    onBack: () -> Unit,
    viewModel: CourseDetailsViewModel = viewModel(factory = CourseDetailsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.title_course_details), onBack = onBack) }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        val course = uiState.course
        when {
            uiState.isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            course == null -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.course_not_found), style = AppStyle.SectionTitle)
            }

            else -> CourseDetailsContent(
                course = course,
                onLessonCompletedChange = viewModel::onLessonCompletedChange,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun CourseDetailsContent(
    course: Course,
    onLessonCompletedChange: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppStyle.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(AppStyle.SpaceMedium)
    ) {
        item {
            Column {
                Text(text = course.title, style = AppStyle.HeaderTitle)
                Spacer(modifier = Modifier.height(AppStyle.SpaceSmall))
                Text(
                    text = stringResource(R.string.course_instructor, course.instructor),
                    style = AppStyle.Body
                )
                Spacer(modifier = Modifier.height(AppStyle.SpaceLarge))
                Text(
                    text = stringResource(R.string.course_progress, course.progress),
                    style = AppStyle.BodyBold
                )
                Spacer(modifier = Modifier.height(AppStyle.SpaceMedium))
                LinearProgressIndicator(
                    progress = { course.progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(AppStyle.SpaceMedium))
                Text(
                    text = stringResource(
                        R.string.course_lessons_completed,
                        course.completedLessons,
                        course.totalLessons
                    ),
                    style = AppStyle.Caption
                )
                Spacer(modifier = Modifier.height(AppStyle.SpaceLarge))
                Text(text = stringResource(R.string.course_lessons_header), style = AppStyle.SectionTitle)
            }
        }

        items(items = course.lessons, key = { it.id }) { lesson ->
            LessonItem(
                lesson = lesson,
                onCompletedChange = { completed -> onLessonCompletedChange(lesson.id, completed) }
            )
        }
    }
}

@Composable
private fun LessonItem(lesson: Lesson, onCompletedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCompletedChange(!lesson.isCompleted) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppStyle.CardPadding, vertical = AppStyle.SpaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lesson.title, style = AppStyle.LessonTitle)
                Text(
                    text = stringResource(
                        if (lesson.isCompleted) R.string.lesson_completed else R.string.lesson_pending
                    ),
                    style = AppStyle.Caption,
                    color = if (lesson.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Checkbox(checked = lesson.isCompleted, onCheckedChange = onCompletedChange)
        }
    }
}
