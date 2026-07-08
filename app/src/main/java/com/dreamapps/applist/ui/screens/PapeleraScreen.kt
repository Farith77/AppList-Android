package com.dreamapps.applist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dreamapps.applist.data.local.entity.ListaEntity
import com.dreamapps.applist.ui.viewmodels.PapeleraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PapeleraScreen(
    viewModel: PapeleraViewModel,
    onBack: () -> Unit
) {
    val listasBorradas by viewModel.listasBorradas.collectAsState()
    var mostrarAlertaVaciar by remember { mutableStateOf(false) }
    var listaParaEliminar by remember { mutableStateOf<ListaEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Papelera") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (listasBorradas.isNotEmpty()) {
                        TextButton(onClick = { mostrarAlertaVaciar = true }) {
                            Text("Vaciar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        if (listasBorradas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("La papelera está vacía", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listasBorradas, key = { it.listCod }) { lista ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(lista.listName, style = MaterialTheme.typography.titleMedium)
                                Text("Se eliminará definitivamente pronto", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            // Botón de Restaurar
                            IconButton(onClick = { viewModel.restaurarLista(lista) }) {
                                Icon(Icons.Default.Restore, contentDescription = "Restaurar", tint = MaterialTheme.colorScheme.primary)
                            }
                            // Botón de Eliminado Físico
                            IconButton(onClick = { listaParaEliminar = lista }) {
                                Icon(Icons.Default.DeleteForever,
                                    contentDescription = "Eliminar Definitivamente",
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // Alerta de Vaciar Papelera
        if (mostrarAlertaVaciar) {
            AlertDialog(
                onDismissRequest = { mostrarAlertaVaciar = false },
                title = { Text("¿Vaciar papelera?") },
                text = { Text("Todos los elementos se eliminarán de forma permanente y no se podrán recuperar.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.vaciarPapelera()
                            mostrarAlertaVaciar = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Vaciar permanentemente") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarAlertaVaciar = false }) { Text("Cancelar") }
                }
            )
        }
        if (listaParaEliminar != null) {
            AlertDialog(
                onDismissRequest = { listaParaEliminar = null },
                title = { Text("¿Eliminar definitivamente?") },
                text = { Text("La lista '${listaParaEliminar?.listName}' se eliminará de forma permanente. Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.eliminarFisicamente(listaParaEliminar!!)
                            listaParaEliminar = null // Cerramos el diálogo
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { listaParaEliminar = null }) { Text("Cancelar") }
                }
            )
        }
    }
}