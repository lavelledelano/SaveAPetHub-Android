package com.example.saveapethub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.github.jan.supabase.auth.providers.Google

@Composable
fun AppRoot() {
    val status by Supabase.client.auth.sessionStatus.collectAsState()
    var showRegister by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    when (status) {
        is SessionStatus.Authenticated -> MainScaffold()
        is SessionStatus.NotAuthenticated ->
            if (showRegister) RegisterScreen(onGoToLogin = { showRegister = false })
            else LoginScreen(onGoToRegister = { showRegister = true })
        else -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
    }
}

private fun friendlyError(e: Exception): String {
    val msg = e.message ?: ""
    return when {
        msg.contains("Invalid login credentials", true) -> "Incorrect email or password."
        msg.contains("already registered", true) -> "An account with this email already exists."
        msg.contains("rate limit", true) -> "Too many attempts. Please wait a moment and try again."
        else -> "Something went wrong. Please check your internet connection and try again."
    }
}

private fun validEmail(email: String) =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

@Composable
fun LoginScreen(onGoToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Save-a-Pet Hub", style = MaterialTheme.typography.headlineMedium)
        Text("Second chances for every tail.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it.trim() },
            label = { Text("Email address") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                error = null
                when {
                    email.isBlank() || password.isBlank() -> error = "Please enter your email and password."
                    !validEmail(email) -> error = "Please enter a valid email address."
                    else -> {
                        busy = true
                        scope.launch {
                            try {
                                Supabase.client.auth.signInWith(Email) {
                                    this.email = email
                                    this.password = password
                                }
                            } catch (e: Exception) {
                                error = friendlyError(e)
                            }
                            busy = false
                        }
                    }
                }
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (busy) "Logging in..." else "Log in") }

        OutlinedButton(
            onClick = {
                scope.launch {
                    try {
                        Supabase.client.auth.signInWith(Google)
                    } catch (e: Exception) {
                        error = "Google sign-in failed. Please try again."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Continue with Google") }

        TextButton(onClick = onGoToRegister, modifier = Modifier.fillMaxWidth()) {
            Text("New here? Create an account")
        }
    }
}

@Composable
fun RegisterScreen(onGoToLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Full name") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it.trim() },
            label = { Text("Email address") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password (min 6 characters)") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = confirm, onValueChange = { confirm = it },
            label = { Text("Confirm password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                error = null
                when {
                    name.isBlank() || email.isBlank() || password.isBlank() ->
                        error = "Please fill in all fields."
                    !validEmail(email) -> error = "Please enter a valid email address."
                    password.length < 6 -> error = "Password must be at least 6 characters."
                    password != confirm -> error = "Passwords do not match."
                    else -> {
                        busy = true
                        scope.launch {
                            try {
                                Supabase.client.auth.signUpWith(Email) {
                                    this.email = email
                                    this.password = password
                                    data = buildJsonObject { put("full_name", name.trim()) }
                                }
                            } catch (e: Exception) {
                                error = friendlyError(e)
                            }
                            busy = false
                        }
                    }
                }
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (busy) "Creating account..." else "Register") }

        TextButton(onClick = onGoToLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? Log in")
        }
    }
}