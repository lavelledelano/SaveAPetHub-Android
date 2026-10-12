package com.example.saveapethub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun MainScaffold() {
    var tab by remember { mutableIntStateOf(0) }
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var applyingPet by remember { mutableStateOf<Pet?>(null) }
    var profilePage by remember { mutableStateOf<String?>(null) }

    val tabs = listOf(
        "Home" to Icons.Filled.Home,
        "Adopt" to Icons.Filled.Favorite,
        "Donate" to Icons.Filled.Star,
        "Volunteer" to Icons.Filled.Face,
        "Profile" to Icons.Filled.Person
    )

    BackHandler(enabled = applyingPet != null || selectedPet != null || profilePage != null) {
        when {
            applyingPet != null -> applyingPet = null
            selectedPet != null -> selectedPet = null
            else -> profilePage = null
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index; selectedPet = null; applyingPet = null; profilePage = null },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).statusBarsPadding()) {
            val pet = selectedPet
            val applying = applyingPet
            if (applying != null) {
                ApplyScreen(applying, onBack = { applyingPet = null })
            } else if (pet != null) {
                PetDetailScreen(pet, onBack = { selectedPet = null }, onApply = { applyingPet = pet })
            } else {
                when (tab) {
                    0 -> HomeScreen(onBrowse = { tab = 1 })
                    1 -> AdoptScreen(onPetClick = { selectedPet = it })
                    2 -> DonateScreen()
                    3 -> VolunteerScreen()
                    else -> ProfileScreen(page = profilePage, onPageChange = { profilePage = it })
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onBrowse: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("ADOPT PAWS & CLAWS", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Discover abandoned pets and give them a permanent, loving home.")
        Text("Second chances for every tail.", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBrowse, modifier = Modifier.fillMaxWidth()) {
            Text("Browse Available Pets")
        }
    }
}

@Composable
fun AdoptScreen(onPetClick: (Pet) -> Unit) {
    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            pets = Supabase.client.from("pets").select().decodeList<Pet>()
        } catch (e: Exception) {
            error = "Could not load pets. Please check your internet connection."
        }
        loading = false
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Adopt Available Pets", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        when {
            loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pets) { pet ->
                    Card(Modifier.fillMaxWidth().clickable { onPetClick(pet) }) {
                        Column(Modifier.padding(16.dp)) {
                            Text(pet.name, style = MaterialTheme.typography.titleMedium)
                            Text("${pet.species} • Age: ${pet.ageYears ?: "?"} years • Colour: ${pet.colour ?: "-"}")
                            Text("STATUS: ${pet.status.uppercase()}", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(4.dp))
                            Text("Tap for details", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PetDetailScreen(pet: Pet, onBack: () -> Unit, onApply: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        TextButton(onClick = onBack) { Text("← Back to pets") }
        Text(pet.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Species: ${pet.species}")
        Text("Breed: ${pet.breed ?: "Unknown"}")
        Text("Age: ${pet.ageYears ?: "?"} years")
        Text("Colour: ${pet.colour ?: "-"}")
        Text("Status: ${pet.status}")
        Spacer(Modifier.height(12.dp))
        Text(pet.description ?: "No description yet.")
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onApply,
            enabled = pet.status == "Available",
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (pet.status == "Available") "Apply to adopt" else "Not available")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("$title - coming soon", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun ProfileScreen(page: String?, onPageChange: (String?) -> Unit) {
    val user = Supabase.client.auth.currentUserOrNull()
    val scope = rememberCoroutineScope()

    when (page) {
        "abuse" -> ReportAbuseScreen(onBack = { onPageChange(null) })
        "surrender" -> SurrenderScreen(onBack = { onPageChange(null) })
        "settings" -> SettingsScreen(onBack = { onPageChange(null) })
        "applications" -> MyApplicationsScreen(onBack = { onPageChange(null) })
        else -> Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
        ) {
            Text("My Profile", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            Text("Email: ${user?.email ?: "-"}")
            Spacer(Modifier.height(24.dp))

            OutlinedButton(onClick = { onPageChange("applications") }, modifier = Modifier.fillMaxWidth()) {
                Text("My Applications")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onPageChange("settings") }, modifier = Modifier.fillMaxWidth()) {
                Text("Settings")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onPageChange("surrender") }, modifier = Modifier.fillMaxWidth()) {
                Text("Surrender a Pet")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onPageChange("abuse") }, modifier = Modifier.fillMaxWidth()) {
                Text("Report Animal Abuse")
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        try { Supabase.client.auth.signOut() } catch (_: Exception) {}
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Log out") }
        }
    }
}