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
data class NewApplication(
    @SerialName("user_id") val userId: String,
    @SerialName("pet_id") val petId: Long,
    val reason: String
)

@Composable
fun ApplyScreen(pet: Pet, onBack: () -> Unit) {
    var reason by remember { mutableStateOf("") }
    var agreed by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var submitted by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back") }

        if (submitted) {
            Text("Application sent!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Thank you for applying to adopt ${pet.name}. Our team will review your application.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back to pets") }
        } else {
            Text("Adopt ${pet.name}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text("${pet.species} • ${pet.breed ?: "Unknown breed"}")
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = reason, onValueChange = { reason = it },
                label = { Text("Why would you like to adopt ${pet.name}?") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Text("${reason.trim().length} characters (minimum 20)", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = agreed, onCheckedChange = { agreed = it })
                Text("I can provide a safe, loving home and agree to a home inspection.")
            }

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
                        reason.trim().length < 20 -> error = "Please write at least 20 characters."
                        reason.trim().length > 1000 -> error = "Please keep your answer under 1000 characters."
                        !agreed -> error = "Please tick the box to confirm."
                        userId == null -> error = "Please log in again."
                        else -> {
                            busy = true
                            scope.launch {
                                try {
                                    Supabase.client.from("adoption_applications").insert(
                                        NewApplication(userId, pet.petId, reason.trim())
                                    )
                                    submitted = true
                                } catch (e: Exception) {
                                    error = if (e.message?.contains("duplicate", true) == true ||
                                        e.message?.contains("one_application_per_pet", true) == true
                                    ) "You have already applied for ${pet.name}."
                                    else "Could not send your application. Please check your internet connection."
                                }
                                busy = false
                            }
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (busy) "Sending..." else "Submit Application") }
        }
    }
}