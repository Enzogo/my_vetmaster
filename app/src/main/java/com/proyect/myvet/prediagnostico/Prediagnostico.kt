package com.proyect.myvet.prediagnostico

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.proyect.myvet.NavigationItem
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrediagnosticoScreen(navController: NavController) {
    var sintomas by remember { mutableStateOf("") }
    var prediagnostico by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Pre-diagnóstico con IA",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = sintomas,
            onValueChange = { sintomas = it },
            label = { Text("Síntomas y observaciones") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                isLoading = true
                prediagnostico = ""
                GlobalScope.launch {
                    delay(2000)
                    prediagnostico = "Basado en los síntomas reportados de '${sintomas}', el pre-diagnóstico preliminar podría ser una infección respiratoria leve. Se recomienda realizar un examen físico completo para confirmar."
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading && sintomas.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Generar Pre-diagnóstico", fontSize = 18.sp)
            }
        }

        Spacer(Modifier.height(32.dp))

        if (prediagnostico.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Resultado del Pre-diagnóstico:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(prediagnostico)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // **LA SOLUCIÓN PARA LA NAVEGACIÓN:**
                            // Navegamos a la ruta de Citas, pero sin borrar la pantalla actual del historial.
                            val encodedMotivo = URLEncoder.encode(prediagnostico, StandardCharsets.UTF_8.toString())
                            navController.navigate("${NavigationItem.Citas.route}?motivo=$encodedMotivo")
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Agendar Cita")
                    }
                }
            }
        }
    }
}