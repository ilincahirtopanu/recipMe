package com.example.recipeme
import androidx.compose.ui.res.stringResource
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen2(onLoginSuccess: () -> Unit, onSignUpClick: () -> Unit) {

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }


    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val savedUsername = prefs.getString("username", "")
        val savedPassword = prefs.getString("password", "")

        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            username = savedUsername
            password = savedPassword
            rememberMe = true
        } else {
            username = ""
            password = ""
            rememberMe = false
        }
    }

    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(stringResource(R.string.login_title), style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(40.dp))

        TextField(
            value = username,
            onValueChange = {
                username = it
                error = null
            },
            label = { Text(stringResource(R.string.email)) },
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
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.remember_me), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.weight(1f))
            Switch(
                checked = rememberMe,
                onCheckedChange = { isChecked ->
                    rememberMe = isChecked

                    if (!isChecked) {
                        username = ""
                        password = ""

                        prefs.edit {
                            remove("username")
                            remove("password")
                        }
                    }
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        // LOGIN BUTTON
        Button(
            onClick = {
                isLoading = true
                error = null

                scope.launch {
                    try {
                        AuthRepository.login(username, password)

                        if (rememberMe) {
                            prefs.edit {
                                putString("username", username)
                                putString("password", password)
                            }
                        } else {
                            prefs.edit {
                                clear() // wipes both
                            }
                        }
                        onLoginSuccess()
                    } catch (e: Exception) {
                        error = e.message ?: context.getString(R.string.login_failed)
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.login))
        }

        Spacer(Modifier.height(20.dp))

        // REGISTER BUTTON
        Button(
            onClick = {
                isLoading = true
                error = null

                onSignUpClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.create_account))
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
