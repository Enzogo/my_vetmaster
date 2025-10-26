package com.proyect.myvet.prediagnostico

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.proyect.myvet.NavigationItem
import com.proyect.myvet.network.PrediagnosticoApi
import com.proyect.myvet.network.PrediRequest
import com.proyect.myvet.network.RetrofitClient
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.jvm.java

@Composable
fun PrediagnosticoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var sintomas by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(sintomas, { sintomas = it }, label = { Text("Síntomas/Observaciones") }, modifier = Modifier.fillMaxWidth().height(140.dp))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(especie, { especie = it }, label = { Text("Especie") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(edad, { edad = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(sexo, { sexo = it }, label = { Text("Sexo") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val api = RetrofitClient.authed(context).create(PrediagnosticoApi::class.java)
                        val r = api.predi(PrediRequest(sintomas, especie.ifBlank { null }, edad.ifBlank { null }, sexo.ifBlank { null }))
                        val motivo = URLEncoder.encode(r.recomendaciones, StandardCharsets.UTF_8.toString())
                        navController.navigate("${NavigationItem.Citas.route}?motivo=$motivo")
                    } catch (_: Exception) {
                        Toast.makeText(context, "Error en prediagnóstico", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Generar y Agendar") }
    }
}