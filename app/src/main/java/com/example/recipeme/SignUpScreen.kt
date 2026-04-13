package com.example.recipeme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.recipeme.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit) {

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }

    var username by remember {
        mutableStateOf(prefs.getString("username", "") ?: "")
    }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Sign up for RecipMe", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(40.dp))

        TextField(
            value = username,
            onValueChange = {
                username = it
                error = null
                prefs.edit { putString("username", it) }
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        TextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(30.dp))

        TextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                error = null
            },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(Modifier.height(30.dp))

        // REGISTER BUTTON
        Button(
            onClick = {
                isLoading = true
                error = null

                scope.launch {
                    try {
                        AuthRepository.register(username, password)
                        Toast.makeText(context, "Registration Successful!", Toast.LENGTH_LONG).show()
                        AuthRepository.login(username, password)
                        onSignUpSuccess()
                    } catch (e: Exception) {
                        error = e.message ?: "Registration failed"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = username.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && (password == confirmPassword),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        if (isLoading) {
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }

        error?.let {
            Spacer(Modifier.height(20.dp))
            Text(it, color = Color.Red)
        }
    }
}