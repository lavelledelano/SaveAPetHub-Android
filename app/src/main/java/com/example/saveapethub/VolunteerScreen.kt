package com.example.saveapethub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Volunteer(
    @SerialName("volunteer_id") val volunteerId: Long,
    val availability: String? = null,
    val skills: String? = null,
    val status: String = "Pending"
)

@Serializable
data class NewVolunteer(
    @SerialName("user_id") val userId: String,
    val availability: String,
    val skills: String
)

@Composable
fun VolunteerScreen() {
    val userId = Supabase.client.auth.currentUserOrNull()?.id
    val availabilityOptions = listOf("Weekdays", "Weekends", "Both weekdays and weekends")

    var existing by remember { mutableStateOf<Volunteer?>(null) }
    var loading by remember { mutableStateOf(true) }
    var availability by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }
    var skills by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            if (userId != null) {
                existing = Supabase.client.from("volunteers").select {
                    filter { eq("user_id", userId) }
                }.decodeList<Volunteer>().firstOrNull()
            }
        } catch (e: Exception) {
            error = "Could not load your volunteer details. Please check your internet connection."
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Text("Volunteer", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))

        val current = existing
        when {
            loading -> Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) {
                CircularProgressIndicator()
            }
            current != null -> {
                Text("Thank you for volunteering!", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Text("Status: ${current.status}")
                Text("Availability: ${current.availability ?: "-"}")
                Text("Skills: ${current.skills ?: "-"}")
                Spacer(Modifier.height(12.dp))
                Text("Our team will be in touch once your application has been reviewed.")
            }
            else -> {
                Text("Give your time to help animals in need. Tell us when you're free and what you can do.")
                Spacer(Modifier.height(16.dp))

                Box {
                    OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (availability.isEmpty()) "Select your availability" else availability)
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        availabilityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { availability = option; menuOpen = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = skills, onValueChange = { skills = it },
                    label = { Text("Your skills (e.g. dog walking, admin, driving)") },
                    minLines = 3,
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
                            availability.isEmpty() -> error = "Please select your availability."
                            skills.trim().length < 3 -> error = "Please tell us about your skills."
                            skills.trim().length > 500 -> error = "Please keep your skills under 500 characters."
                            userId == null -> error = "Please log in again."
                            else -> {
                                busy = true
                                scope.launch {
                                    try {
                                        Supabase.client.from("volunteers").insert(
                                            NewVolunteer(userId, availability, skills.trim())
                                        )
                                        existing = Volunteer(0, availability, skills.trim(), "Pending")
                                    } catch (e: Exception) {
                                        error = if (e.message?.contains("duplicate", true) == true)
                                            "You have already signed up as a volunteer."
                                        else "Could not send your sign-up. Please check your internet connection."
                                    }
                                    busy = false
                                }
                            }
                        }
                    },
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (busy) "Sending..." else "Sign up as a volunteer") }
            }
        }
    }
}