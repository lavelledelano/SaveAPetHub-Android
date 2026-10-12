package com.example.saveapethub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val helpTopics = listOf(
    "How do I create an account?" to
            "On the login screen tap \"New here? Create an account\". Enter your full name, email and a password of at least 6 characters, then tap Register. You can also tap \"Continue with Google\" to sign in with your Google account.",
    "How do I adopt a pet?" to
            "Open the Adopt tab, tap a pet to see its details, then tap \"Apply to adopt\". Tell us why you would like to adopt, tick the confirmation box and submit. You can apply for each pet once.",
    "Where can I see my applications?" to
            "Go to Profile, then My Applications. Each application shows the pet's name and its status: Pending, Approved or Rejected.",
    "How do I make a donation?" to
            "Open the Donate tab, enter your name and an amount in Rands, choose a payment method and tap Submit Donation.",
    "How do I volunteer?" to
            "Open the Volunteer tab, choose when you are available, describe your skills and tap Sign up as a volunteer. After that the tab shows the status of your sign-up.",
    "How do I surrender a pet or report abuse?" to
            "Go to Profile, then tap Surrender a Pet or Report Animal Abuse. Fill in the form and submit it. If an animal is in immediate danger, please also contact the SPCA directly.",
    "How do I change my details or password?" to
            "Go to Profile, then Settings. You can update your name, phone number and address, and change your password. If you signed in with Google you do not have a password to change.",
    "Why do I see an error message?" to
            "Check that you filled in every field correctly and that your phone is connected to the internet, then try again. Error messages tell you what to fix.",
    "How do I log out?" to
            "Go to Profile and tap Log out."
)

@Composable
fun HelpScreen(onBack: () -> Unit) {
    var openIndex by remember { mutableIntStateOf(-1) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back to profile") }
        Text("Help", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("Tap a question to see the answer.")
        Spacer(Modifier.height(12.dp))

        helpTopics.forEachIndexed { index, (question, answer) ->
            Card(
                Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clickable { openIndex = if (openIndex == index) -1 else index }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(question, style = MaterialTheme.typography.titleMedium)
                    if (openIndex == index) {
                        Spacer(Modifier.height(8.dp))
                        Text(answer)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Still stuck? Please contact the Johannesburg SPCA directly.", style = MaterialTheme.typography.labelLarge)
    }
}