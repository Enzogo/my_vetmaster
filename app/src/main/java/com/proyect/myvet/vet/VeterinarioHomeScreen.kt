package com.proyect.myvet.vet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VeterinarioHomeScreen() {
    val vm: VetViewModel = viewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.loadAll() }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Personas", "Citas", "Mascotas")

    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Panel del Veterinario",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else state.error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }

        when (selectedTab) {
            0 -> PersonasTab(state)
            1 -> CitasTab(state)
            2 -> MascotasTab(state)
        }
    }
}

@Composable
private fun PersonasTab(state: VetUiState) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        items(state.owners) { o ->
            Text("${o.nombre ?: "-"} (${o.email ?: "-"})", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Divider()
        }
    }
}

@Composable
private fun CitasTab(state: VetUiState) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        items(state.citas) { c ->
            Text("Fecha: ${c.fecha ?: "-"} | Estado: ${c.estado ?: "-"}", style = MaterialTheme.typography.bodyLarge)
            Text("Dueño: ${c.duenioNombre ?: "-"} | Mascota: ${c.mascotaNombre ?: "-"}")
            Spacer(Modifier.height(8.dp))
            Divider()
        }
    }
}

@Composable
private fun MascotasTab(state: VetUiState) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        items(state.mascotas) { m ->
            Text("${m.nombre ?: "-"} (${m.especie ?: "-"})", style = MaterialTheme.typography.bodyLarge)
            Text("Dueño: ${m.duenioNombre ?: "-"}")
            Spacer(Modifier.height(8.dp))
            Divider()
        }
    }
}