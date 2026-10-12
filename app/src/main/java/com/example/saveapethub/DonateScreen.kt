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
data class NewDonation(
    @SerialName("user_id") val userId: String,
    @SerialName("donor_name") val donorName: String,
    val amount: Double,
    @SerialName("payment_method") val paymentMethod: String
)

@Composable
fun DonateScreen() {
    val methods = listOf("Credit/Debit Card", "EFT", "SnapScan", "Cash at shelter")
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Text("Make a Donation", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("Your financial support helps us provide food, medical care, and second chances for every animal in need.")
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Your name") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = amount, onValueChange = { amount = it },
            label = { Text("Donation amount (R)") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Box {
            OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (method.isEmpty()) "Select payment method" else method)
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                methods.forEach { m ->
                    DropdownMenuItem(text = { Text(m) }, onClick = { method = m; menuOpen = false })
                }
            }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        success?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                error = null
                success = null
                val value = amount.replace(',', '.').toDoubleOrNull()
                val userId = Supabase.client.auth.currentUserOrNull()?.id
                when {
                    name.isBlank() -> error = "Please enter your name."
                    value == null -> error = "Please enter a valid amount."
                    value <= 0 -> error = "The amount must be more than R0."
                    value > 1_000_000 -> error = "The maximum online donation is R1,000,000."
                    method.isEmpty() -> error = "Please select a payment method."
                    userId == null -> error = "Please log in again."
                    else -> {
                        busy = true
                        scope.launch {
                            try {
                                Supabase.client.from("donations").insert(
                                    NewDonation(
                                        userId = userId,
                                        donorName = name.trim(),
                                        amount = Math.round(value * 100) / 100.0,
                                        paymentMethod = method
                                    )
                                )
                                success = "Thank you for your donation of R%.2f!".format(value)
                                amount = ""
                                method = ""
                            } catch (e: Exception) {
                                error = "Could not save your donation. Please check your internet connection and try again."
                            }
                            busy = false
                        }
                    }
                }
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (busy) "Submitting..." else "Submit Donation") }
    }
}