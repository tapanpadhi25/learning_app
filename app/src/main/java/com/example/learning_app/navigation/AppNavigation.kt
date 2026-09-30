package com.example.learning_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learning_app.presentation.dashboard.DashboardScreen
import com.example.learning_app.presentation.details.CourseDetailsScreen
import com.example.learning_app.presentation.login.LoginScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object CourseDetails : Screen("course/{courseId}") {
        const val ARG_COURSE_ID = "courseId"
        fun createRoute(courseId: Int) = "course/$courseId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetails.createRoute(courseId))
                }
            )
        }

        composable(
            route = Screen.CourseDetails.route,
            arguments = listOf(
                navArgument(Screen.CourseDetails.ARG_COURSE_ID) { type = NavType.IntType }
            )
        ) {
            CourseDetailsScreen(onBack = { navController.popBackStack() })
        }
    }
}
