package com.proyect.myvet

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.proyect.myvet.auth.LocalAuthViewModel
import com.proyect.myvet.citas.CitasScreen
import com.proyect.myvet.historial.DetalleHistorialScreen
import com.proyect.myvet.historial.HistorialCita
import com.proyect.myvet.historial.HistorialScreen
import com.proyect.myvet.mascotas.RegistrarMascotaScreen
import com.proyect.myvet.network.FeedbackApi
import com.proyect.myvet.network.FeedbackCreateRequest
import com.proyect.myvet.network.RetrofitClient
import com.proyect.myvet.prediagnostico.PrediagnosticoScreen
import com.proyect.myvet.vet.VetCitasScreen
import com.proyect.myvet.vet.VetPerfilScreen
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen() {
    val authVM = LocalAuthViewModel.current
    val authState by authVM.state.collectAsState()
    val isVet = authState.role == "veterinario"

    val navController = rememberNavController()
    val tabs = if (isVet) {
        listOf(NavigationItem.VetCitas, NavigationItem.VetPerfil)
    } else {
        listOf(NavigationItem.Home, NavigationItem.Citas, NavigationItem.Prediagnostico, NavigationItem.Historial, NavigationItem.Perfil)
    }
    val startRoute = if (isVet) NavigationItem.VetCitas.route else NavigationItem.Home.route

    Scaffold(
        containerColor = Color(0xFFF5F1EB),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                tabs.forEach { item ->
                    val isSelected = currentRoute?.startsWith(item.route) == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = startRoute, modifier = Modifier.padding(innerPadding)) {
            if (isVet) {
                composable(NavigationItem.VetCitas.route) { VetCitasScreen() }
                composable(NavigationItem.VetPerfil.route) { VetPerfilScreen(navController = navController) }
                composable("editar_perfil") { com.proyect.myvet.perfil.EditarPerfilScreen(navController = navController) }
            } else {
                composable(NavigationItem.Home.route) { InicioDuenoContent(navController = navController) }
                composable(
                    route = "${NavigationItem.Citas.route}?motivo={motivo}",
                    arguments = listOf(navArgument("motivo") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) { CitasScreen(navController = navController, motivoInicial = it.arguments?.getString("motivo")) }
                composable(NavigationItem.Prediagnostico.route) { PrediagnosticoScreen(navController = navController) }
                composable(NavigationItem.Historial.route) { HistorialScreen(navController = navController) }
                composable(
                    route = "detalle_historial/{citaJson}",
                    arguments = listOf(navArgument("citaJson") { type = NavType.StringType })
                ) {
                    val citaJson = it.arguments?.getString("citaJson")?.let { s ->
                        URLDecoder.decode(s, StandardCharsets.UTF_8.toString())
                    }
                    val cita = Gson().fromJson(citaJson, HistorialCita::class.java)
                    DetalleHistorialScreen(navController = navController, cita = cita)
                }
                composable(NavigationItem.Perfil.route) { com.proyect.myvet.perfil.PerfilScreen(navController = navController) }
                composable("gestion_mascotas") { com.proyect.myvet.perfil.GestionMascotasScreen(navController = navController) }
                composable(
                    route = "registrar_mascota?mascotaId={mascotaId}",
                    arguments = listOf(navArgument("mascotaId") { type = NavType.StringType; nullable = true })
                ) { RegistrarMascotaScreen(navController = navController, mascotaId = null) }
                composable("editar_perfil") { com.proyect.myvet.perfil.EditarPerfilScreen(navController = navController) }
            }
        }
    }
}

@Composable
private fun InicioDuenoContent(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var rating by remember { mutableStateOf(0) }
    var sugerencia by remember { mutableStateOf(TextFieldValue("")) }
    var sending by remember { mutableStateOf(false) }
    var avg by remember { mutableStateOf(0.0) }
    var count by remember { mutableStateOf(0) }
    var loadingSummary by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val api = RetrofitClient.authed(context).create(FeedbackApi::class.java)
            val s = api.summary()
            avg = s.avg; count = s.count
        } catch (_: Exception) {
        } finally {
            loadingSummary = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1) Tarjeta de bienvenida
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7DA581)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("¡Bienvenido a MyVet!", style = MaterialTheme.typography.headlineSmall, color = Color.Black)
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // 2) Burbujas 2x2 (Citas, Perfil, IA, Historial)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F1EB)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BubbleButton(
                            title = "Citas",
                            color = Color(0xFF69C27C),
                            onClick = { navController.safeNavigate(NavigationItem.Citas.route) },
                            icon = { Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = "Citas", tint = Color.Black) },
                            modifier = Modifier.weight(1f)
                        )
                        BubbleButton(
                            title = "Perfil",
                            color = Color(0xFFFA8C59),
                            onClick = { navController.safeNavigate(NavigationItem.Perfil.route) },
                            icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = "Perfil", tint = Color.Black) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BubbleButton(
                            title = "IA",
                            color = Color(0xFF7D7AEF),
                            onClick = { navController.safeNavigate(NavigationItem.Prediagnostico.route) },
                            icon = { Icon(imageVector = Icons.Outlined.Psychology, contentDescription = "IA", tint = Color.Black) },
                            modifier = Modifier.weight(1f)
                        )
                        BubbleButton(
                            title = "Historial",
                            color = Color(0xFF5CB4E7),
                            onClick = { navController.safeNavigate(NavigationItem.Historial.route) },
                            icon = { Icon(imageVector = Icons.Outlined.History, contentDescription = "Historial", tint = Color.Black) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // 3) Registrar mascota (BLANCO)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("¿Eres nuevo? Registra tu mascota", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { navController.navigate("registrar_mascota") }) {
                        Text("Registrar mascota")
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // 4) Clasificación de la app (BLANCO)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Clasificación de la app", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    if (loadingSummary) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val full = avg.toInt().coerceIn(0, 5)
                            val rest = 5 - full
                            repeat(full) { Icon(Icons.Filled.Star, contentDescription = null) }
                            repeat(rest) { Icon(Icons.Outlined.StarBorder, contentDescription = null) }
                            Spacer(Modifier.width(8.dp))
                            Text(String.format("%.1f", avg) + " (${count})")
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }

        // 5) Enviar feedback (BLANCO)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tu opinión nos ayuda", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        (1..5).forEach { i ->
                            IconButton(onClick = { rating = i }) {
                                if (i <= rating) Icon(Icons.Filled.Star, contentDescription = "star_$i")
                                else Icon(Icons.Outlined.StarBorder, contentDescription = "star_$i")
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sugerencia,
                        onValueChange = { sugerencia = it },
                        label = { Text("Sugerencia (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (rating <= 0) {
                                Toast.makeText(context, "Selecciona una puntuación (1..5)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            scope.launch {
                                try {
                                    sending = true
                                    val api = RetrofitClient.authed(context).create(FeedbackApi::class.java)
                                    val resp = api.create(FeedbackCreateRequest(rating, sugerencia.text.trim()))
                                    if (resp.isSuccessful) {
                                        Toast.makeText(context, "¡Gracias por tu feedback!", Toast.LENGTH_SHORT).show()
                                        rating = 0
                                        sugerencia = TextFieldValue("")
                                        try {
                                            val s = api.summary()
                                            avg = s.avg; count = s.count
                                        } catch (_: Exception) { }
                                    } else {
                                        val code = resp.code()
                                        val msg = when (code) {
                                            401 -> "Sesión expirada. Inicia sesión."
                                            404 -> "Falta /api/feedback en backend."
                                            415 -> "Formato no soportado."
                                            else -> "Error $code al enviar feedback"
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    val code = (e as? HttpException)?.code()
                                    val msg = when (code) {
                                        401 -> "Sesión expirada."
                                        404 -> "Falta /api/feedback."
                                        else -> "No se pudo enviar el feedback"
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                } finally {
                                    sending = false
                                }
                            }
                        },
                        enabled = !sending
                    ) {
                        Text(if (sending) "Enviando..." else "Enviar feedback")
                    }
                }
            }
        }
    }
}

@Composable
private fun BubbleButton(
    title: String,
    color: Color,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(color)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

private fun NavController.safeNavigate(route: String) {
    val current = this.currentDestination?.route
    if (current != route) this.navigate(route)
}