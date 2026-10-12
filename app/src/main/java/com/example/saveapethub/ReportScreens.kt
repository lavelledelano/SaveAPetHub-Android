package com.example.saveapethub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewReport(
    @SerialName("reporter_id") val reporterId: String,
    @SerialName("report_type") val reportType: String,
    val description: String,
    val location: String
)

@Composable
fun ReportAbuseScreen(onBack: () -> Unit) {
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var done by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back to profile") }
        if (done) {
            Text("Report sent", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Thank you for speaking up. Our team will look into this as soon as possible.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Done") }
        } else {
            Text("Report Animal Abuse", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("Tell us what you saw and where. If an animal is in immediate danger, please also call the SPCA directly.")
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = location, onValueChange = { location = it },
                label = { Text("Location (street, suburb)") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("What did you see?") }, minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Text("${description.trim().length} characters (minimum 20)", style = MaterialTheme.typography.labelSmall)

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    error = null
                    val userId = Supabase.client.auth.currentUserOrNull()?.id
                    when {
                        location.trim().length < 3 -> error = "Please enter the location."
                        description.trim().length < 20 -> error = "Please describe what you saw (at least 20 characters)."
                        description.trim().length > 1500 -> error = "Please keep the description under 1500 characters."
                        userId == null -> error = "Please log in again."
                        else -> {
                            busy = true
                            scope.launch {
                                try {
                                    Supabase.client.from("reports").insert(
                                        NewReport(userId, "Abuse", description.trim(), location.trim())
                                    )
                                    done = true
                                } catch (e: Exception) {
                                    error = "Could not send your report. Please check your internet connection."
                                }
                                busy = false
                            }
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (busy) "Sending..." else "Submit Report") }
        }
    }
}

@Composable
fun SurrenderScreen(onBack: () -> Unit) {
    val speciesOptions = listOf("Dog", "Cat", "Bird", "Rabbit", "Reptile", "Fish", "Other")
    var petName by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var done by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back to profile") }
        if (done) {
            Text("Request sent", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Thank you. Our team will contact you to arrange the next steps for $petName.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Done") }
        } else {
            Text("Pet Surrender", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("If you can no longer care for your pet, tell us about them and we will help find a new home.")
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = petName, onValueChange = { petName = it },
                label = { Text("Pet's name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Box {
                OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (species.isEmpty()) "Select species" else species)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    speciesOptions.forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = { species = s; menuOpen = false })
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = age, onValueChange = { age = it },
                label = { Text("Age in years") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = area, onValueChange = { area = it },
                label = { Text("Your area (suburb)") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = reason, onValueChange = { reason = it },
                label = { Text("Reason for surrender") }, minLines = 3,
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
                    val userId = Supabase.client.auth.currentUserOrNull()?.id
                    val ageValue = age.trim().toIntOrNull()
                    when {
                        petName.isBlank() -> error = "Please enter your pet's name."
                        species.isEmpty() -> error = "Please select the species."
                        ageValue == null || ageValue < 0 || ageValue > 60 -> error = "Please enter a valid age (0 to 60)."
                        area.trim().length < 3 -> error = "Please enter your area."
                        reason.trim().length < 10 -> error = "Please tell us why (at least 10 characters)."
                        reason.trim().length > 1000 -> error = "Please keep the reason under 1000 characters."
                        userId == null -> error = "Please log in again."
                        else -> {
                            busy = true
                            scope.launch {
                                try {
                                    val text = "Pet: ${petName.trim()} | Species: $species | " +
                                            "Age: $ageValue | Reason: ${reason.trim()}"
                                    Supabase.client.from("reports").insert(
                                        NewReport(userId, "Surrender", text, area.trim())
                                    )
                                    done = true
                                } catch (e: Exception) {
                                    error = "Could not send your request. Please check your internet connection."
                                }
                                busy = false
                            }
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (busy) "Sending..." else "Submit Surrender Request") }
        }
    }
}