package com.example.learning_app.presentation.dashboard

import Course
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onCourseClick: (Course) -> Unit,
    dashboardViewModel: DashboardViewModel = viewModel()
) {

    val uiState by dashboardViewModel
        .uiState
        .collectAsState()


    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Learning Dashboard")
                }
            )
        }

    ) { innerPadding ->

        when {

            // Loading
            uiState.isLoading -> {

                LoadingContent(
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }


            // Error
            uiState.errorMessage != null -> {

                ErrorContent(
                    message =
                    uiState.errorMessage!!,

                    onRetry = {
                        dashboardViewModel
                            .loadCourses()
                    },

                    modifier = Modifier
                        .padding(innerPadding)
                )
            }


            // Empty
            uiState.courses.isEmpty() -> {

                EmptyContent(
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }


            // Success
            else -> {

                CourseList(
                    courses = uiState.courses,

                    onCourseClick = onCourseClick,

                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun CourseList(
    courses: List<Course>,
    onCourseClick: (Course) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(

        modifier = modifier.fillMaxSize(),

        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 16.dp
        ),

        verticalArrangement =
        Arrangement.spacedBy(16.dp)
    ) {

        item {

            Text(
                text = "My Courses",
                style =
                MaterialTheme.typography
                    .headlineSmall,
                fontWeight =
                FontWeight.Bold
            )

            Spacer(
                modifier =
                Modifier.height(4.dp)
            )

            Text(
                text =
                "Continue learning where you left off.",
                style =
                MaterialTheme.typography
                    .bodyMedium
            )
        }


        items(
            items = courses,
            key = { it.id }
        ) { course ->

            CourseCard(
                course = course,

                onContinue = {
                    onCourseClick(course)
                }
            )
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    onContinue: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            Text(
                text = course.title,

                style =
                MaterialTheme.typography
                    .titleLarge,

                fontWeight =
                FontWeight.Bold
            )


            Spacer(
                modifier =
                Modifier.height(6.dp)
            )


            Text(
                text =
                "Instructor: ${course.instructor}",

                style =
                MaterialTheme.typography
                    .bodyMedium
            )


            Spacer(
                modifier =
                Modifier.height(16.dp)
            )


            Text(
                text =
                "Progress: ${course.progress}%",

                fontWeight =
                FontWeight.Medium
            )


            Spacer(
                modifier =
                Modifier.height(8.dp)
            )


            LinearProgressIndicator(

                progress = {
                    course.progress / 100f
                },

                modifier =
                Modifier.fillMaxWidth()
            )


            Spacer(
                modifier =
                Modifier.height(12.dp)
            )


            Text(
                text =
                "${course.lessons} Lessons",

                style =
                MaterialTheme.typography
                    .bodyMedium
            )


            Spacer(
                modifier =
                Modifier.height(12.dp)
            )


            Button(

                onClick = onContinue,

                modifier =
                Modifier.fillMaxWidth()
            ) {

                Text("Continue")
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxSize(),

        contentAlignment =
        Alignment.Center
    ) {

        Column(
            horizontalAlignment =
            Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator()

            Spacer(
                modifier =
                Modifier.height(12.dp)
            )

            Text(
                text = "Loading courses..."
            )
        }
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxSize(),

        contentAlignment =
        Alignment.Center
    ) {

        Column(
            horizontalAlignment =
            Alignment.CenterHorizontally
        ) {

            Text(
                text = "No courses available",

                style =
                MaterialTheme.typography
                    .titleMedium,

                fontWeight =
                FontWeight.Bold
            )

            Spacer(
                modifier =
                Modifier.height(8.dp)
            )

            Text(
                text =
                "There are no courses to display."
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxSize(),

        contentAlignment =
        Alignment.Center
    ) {

        Column(

            modifier =
            Modifier.padding(24.dp),

            horizontalAlignment =
            Alignment.CenterHorizontally
        ) {

            Text(
                text = message,

                style =
                MaterialTheme.typography
                    .titleMedium,

                fontWeight =
                FontWeight.Bold
            )


            Spacer(
                modifier =
                Modifier.height(8.dp)
            )


            Text(
                text =
                "Something went wrong while loading your courses."
            )


            Spacer(
                modifier =
                Modifier.height(16.dp)
            )


            Button(
                onClick = onRetry
            ) {

                Text("Retry")
            }
        }
    }
}