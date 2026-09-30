package com.example.learning_app.presentation.dashboard

import androidx.annotation.StringRes
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learning_app.R
import com.example.learning_app.data.model.Course
import com.example.learning_app.ui.components.AppTopBar
import com.example.learning_app.ui.theme.AppStyle

@Composable
fun DashboardScreen(
    onCourseClick: (Int) -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.title_dashboard)) }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        val errorMessage = uiState.errorMessage
        when {
            uiState.courses.isNotEmpty() -> CourseList(
                courses = uiState.courses,
                errorMessage = errorMessage,
                onRetry = viewModel::refresh,
                onCourseClick = onCourseClick,
                modifier = modifier
            )

            uiState.isLoading -> LoadingContent(modifier)

            errorMessage != null -> MessageContent(
                title = stringResource(errorMessage),
                message = stringResource(R.string.dashboard_error_message),
                onRetry = viewModel::refresh,
                modifier = modifier
            )

            else -> MessageContent(
                title = stringResource(R.string.dashboard_empty_title),
                message = stringResource(R.string.dashboard_empty_message),
                onRetry = viewModel::refresh,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun CourseList(
    courses: List<Course>,
    @StringRes errorMessage: Int?,
    onRetry: () -> Unit,
    onCourseClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppStyle.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(AppStyle.ItemSpacing)
    ) {
        if (errorMessage != null) {
            item { OfflineBanner(message = stringResource(errorMessage), onRetry = onRetry) }
        }

        item {
            Text(text = stringResource(R.string.dashboard_header), style = AppStyle.HeaderTitle)
        }

        items(items = courses, key = { it.id }) { course ->
            CourseCard(course = course, onContinue = { onCourseClick(course.id) })
        }
    }
}

@Composable
private fun OfflineBanner(message: String, onRetry: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AppStyle.CardPadding, end = AppStyle.SpaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.dashboard_offline_banner, message),
                style = AppStyle.Body,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
    }
}

@Composable
private fun CourseCard(course: Course, onContinue: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(AppStyle.CardPadding)) {
            Text(text = course.title, style = AppStyle.CardTitle)
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
            Spacer(modifier = Modifier.height(AppStyle.SpaceLarge))
            Text(
                text = stringResource(R.string.course_lessons_count, course.totalLessons),
                style = AppStyle.Body
            )
            Spacer(modifier = Modifier.height(AppStyle.SpaceLarge))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.action_continue))
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(AppStyle.SpaceLarge))
            Text(text = stringResource(R.string.dashboard_loading), style = AppStyle.Body)
        }
    }
}

@Composable
private fun MessageContent(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(AppStyle.FormPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = AppStyle.SectionTitle)
            Spacer(modifier = Modifier.height(AppStyle.SpaceMedium))
            Text(text = message, style = AppStyle.Body)
            Spacer(modifier = Modifier.height(AppStyle.SpaceLarge))
            Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
        }
    }
}
