package com.proyect.myvet.perfil

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.auth.LocalAuthViewModel

@Composable
fun PerfilScreen(navController: NavController) {
    val authVM = LocalAuthViewModel.current
    val state by authVM.state.collectAsState()
    val isVet = state.role == "veterinario"
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1EB))
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1) Tarjeta de perfil con avatar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7DA581)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar circular con inicial del nombre
                    val inicial = (state.nombre?.firstOrNull()?.uppercaseChar() ?: state.email?.firstOrNull()?.uppercaseChar() ?: "U").toString()
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5A9E6F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = inicial,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre (si existe)
                    if (!state.nombre.isNullOrBlank()) {
                        Text(
                            text = state.nombre ?: "Usuario",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Email
                    Text(
                        text = state.email ?: "usuario@email.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Badge del rol
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isVet) "🩺 Veterinario" else "🐾 Dueño de Mascota",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // 2) Sección de opciones
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F1EB)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    if (isVet) {
                        // Opciones para veterinario
                        ProfileOptionBubble(
                            title = "Editar Perfil",
                            subtitle = "Actualiza tu información",
                            color = Color(0xFF69C27C),
                            icon = Icons.Outlined.Edit,
                            onClick = { navController.navigate("editar_perfil") }
                        )

                        Spacer(Modifier.height(12.dp))

                        ProfileOptionBubble(
                            title = "Cerrar Sesión",
                            subtitle = "Salir de tu cuenta",
                            color = Color(0xFFFA8C59),
                            icon = Icons.Outlined.PowerSettingsNew,
                            onClick = {
                                authVM.logout()
                                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        // Opciones para dueño
                        ProfileOptionBubble(
                            title = "Editar Perfil",
                            subtitle = "Actualiza tu información",
                            color = Color(0xFF69C27C),
                            icon = Icons.Outlined.Edit,
                            onClick = { navController.navigate("editar_perfil") }
                        )

                        Spacer(Modifier.height(12.dp))

                        ProfileOptionBubble(
                            title = "Mis Mascotas",
                            subtitle = "Gestionar y registrar",
                            color = Color(0xFF7D7AEF),
                            icon = Icons.Outlined.Pets,
                            onClick = { navController.navigate("gestion_mascotas") }
                        )

                        Spacer(Modifier.height(12.dp))

                        ProfileOptionBubble(
                            title = "Cerrar Sesión",
                            subtitle = "Salir de tu cuenta",
                            color = Color(0xFFFA8C59),
                            icon = Icons.Outlined.PowerSettingsNew,
                            onClick = {
                                authVM.logout()
                                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        // 3) Información adicional
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
                    Text(
                        "Acerca de",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "MyVet - Tu clínica veterinaria en línea",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Versión 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionBubble(
    title: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono circular con color
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Flecha
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}
