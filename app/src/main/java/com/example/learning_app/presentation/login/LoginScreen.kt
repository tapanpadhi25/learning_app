package com.example.learning_app.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {

    val uiState by loginViewModel.uiState.collectAsState()

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    // Navigate after successful login
    LaunchedEffect(uiState.isLoginSuccess) {

        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            // Login title
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.email,

                onValueChange = {
                    loginViewModel.onEmailChange(it)
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Email")
                },

                singleLine = true,

                isError = uiState.emailError != null,

                supportingText = {
                    uiState.emailError?.let {
                        Text(it)
                    }
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Password
            OutlinedTextField(
                value = uiState.password,

                onValueChange = {
                    loginViewModel.onPasswordChange(it)
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Password")
                },

                singleLine = true,

                isError = uiState.passwordError != null,

                supportingText = {
                    uiState.passwordError?.let {
                        Text(it)
                    }
                },

                visualTransformation =
                if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

                trailingIcon = {

                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {

                        Icon(
                            imageVector =
                            if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },

                            contentDescription =
                            if (passwordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            }
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            uiState.errorMessage?.let { error ->

                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            // Login button
            Button(
                onClick = {
                    loginViewModel.login()
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                enabled = !uiState.isLoading
            ) {

                if (uiState.isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.height(22.dp),
                        strokeWidth = 2.dp
                    )

                } else {

                    Text(
                        text = "Login"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Demo credentials
            Text(
                text = "Demo: test@gmail.com / 123456",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
