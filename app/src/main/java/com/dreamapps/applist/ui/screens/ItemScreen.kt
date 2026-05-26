package com.dreamapps.applist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamapps.applist.ui.components.ItemRow
import com.dreamapps.applist.ui.viewmodels.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    viewModel: ItemViewModel,
    nombreLista: String, // Recibimos el nombre para mostrarlo en el título
    onBack: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val enModoEdicion by viewModel.enModoEdicion.collectAsState()
    val textoEdicion by viewModel.textoEdicion.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nombreLista) }, // "Se mostrara el nombre de esta" (Edu-01)
                actions = {
                    // Botón para alternar estados
                    IconButton(onClick = { viewModel.cambiarModoEdicion(!enModoEdicion) }) {
                        Icon(
                            imageVector = if (enModoEdicion) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = "Cambiar estado"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize()) {
            if (enModoEdicion) {
                // ESTADO EDICIÓN: App de notas
                TextField(
                    value = textoEdicion,
                    onValueChange = { viewModel.actualizarTextoEdicion(it) },
                    modifier = Modifier.fillMaxSize(),
                    placeholder = { Text("Escribe tus ítems aquí, uno por cada línea...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            } else {
                // ESTADO VISUALIZACIÓN: Lista normal
                if (items.isEmpty()) {
                    Text("No hay ítems. Presiona el lápiz para agregar algunos.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items) { item ->
                            ItemRow(itemText = item.itemName)
                        }
                    }
                }
            }
        }
    }
}