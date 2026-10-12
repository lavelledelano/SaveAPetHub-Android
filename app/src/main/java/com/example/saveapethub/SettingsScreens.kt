package com.example.saveapethub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    @SerialName("full_name") val fullName: String = "",
    val phone: String? = null,
    val address: String? = null
)

@Serializable
data class ProfileUpdate(
    @SerialName("full_name") val fullName: String,
    val phone: String,
    val address: String
)

@Serializable
data class MyApplication(
    @SerialName("application_id") val applicationId: Long,
    @SerialName("pet_id") val petId: Long,
    val reason: String? = null,
    val status: String = "Pending"
)

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val userId = Supabase.client.auth.currentUserOrNull()?.id
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var profileMsg by remember { mutableStateOf<String?>(null) }
    var profileError by remember { mutableStateOf<String?>(null) }

    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var pwMsg by remember { mutableStateOf<String?>(null) }
    var pwError by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            if (userId != null) {
                val p = Supabase.client.from("profiles").select {
                    filter { eq("id", userId) }
                }.decodeList<ProfileData>().firstOrNull()
                if (p != null) {
                    name = p.fullName
                    phone = p.phone ?: ""
                    address = p.address ?: ""
                }
            }
        } catch (e: Exception) {
            profileError = "Could not load your details. Please check your internet connection."
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back to profile") }
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Text("Profile details", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (loading) {
            CircularProgressIndicator()
        } else {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Full name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Phone number") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text("Address") }, minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            profileError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            profileMsg?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    profileError = null
                    profileMsg = null
                    val digits = phone.filter { it.isDigit() }
                    when {
                        name.isBlank() -> profileError = "Please enter your name."
                        phone.isNotBlank() && (digits.length < 9 || digits.length > 15) ->
                            profileError = "Please enter a valid phone number."
                        userId == null -> profileError = "Please log in again."
                        else -> {
                            busy = true
                            scope.launch {
                                try {
                                    Supabase.client.from("profiles").update(
                                        ProfileUpdate(name.trim(), phone.trim(), address.trim())
                                    ) { filter { eq("id", userId) } }
                                    profileMsg = "Details saved."
                                } catch (e: Exception) {
                                    profileError = "Could not save. Please check your internet connection."
                                }
                                busy = false
                            }
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save details") }
        }

        Spacer(Modifier.height(32.dp))
        Text("Change password", style = MaterialTheme.typography.titleMedium)
        Text(
            "If you signed in with Google, you don't have a password to change.",
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = newPassword, onValueChange = { newPassword = it },
            label = { Text("New password (min 6 characters)") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = confirm, onValueChange = { confirm = it },
            label = { Text("Confirm new password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        pwError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        pwMsg?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                pwError = null
                pwMsg = null
                when {
                    newPassword.length < 6 -> pwError = "Password must be at least 6 characters."
                    newPassword != confirm -> pwError = "Passwords do not match."
                    else -> {
                        busy = true
                        scope.launch {
                            try {
                                Supabase.client.auth.updateUser { password = newPassword }
                                pwMsg = "Password changed."
                                newPassword = ""
                                confirm = ""
                            } catch (e: Exception) {
                                pwError = "Could not change password. Try a different one, or log in again."
                            }
                            busy = false
                        }
                    }
                }
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Change password") }
    }
}

@Composable
fun MyApplicationsScreen(onBack: () -> Unit) {
    val userId = Supabase.client.auth.currentUserOrNull()?.id
    var apps by remember { mutableStateOf<List<MyApplication>>(emptyList()) }
    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            if (userId != null) {
                apps = Supabase.client.from("adoption_applications").select {
                    filter { eq("user_id", userId) }
                }.decodeList<MyApplication>()
                pets = Supabase.client.from("pets").select().decodeList<Pet>()
            }
        } catch (e: Exception) {
            error = "Could not load your applications. Please check your internet connection."
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back to profile") }
        Text("My Applications", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        when {
            loading -> CircularProgressIndicator()
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            apps.isEmpty() -> Text("You haven't applied to adopt any pets yet.")
            else -> apps.forEach { app ->
                val petName = pets.firstOrNull { it.petId == app.petId }?.name ?: "Pet #${app.petId}"
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(petName, style = MaterialTheme.typography.titleMedium)
                        Text("Status: ${app.status}")
                    }
                }
            }
        }
    }
}