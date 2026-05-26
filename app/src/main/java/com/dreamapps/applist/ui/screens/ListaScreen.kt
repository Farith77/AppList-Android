package com.dreamapps.applist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamapps.applist.data.local.entity.ListaEntity
import com.dreamapps.applist.ui.viewmodels.ListaViewModel
import com.dreamapps.applist.ui.components.ListaItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onNavigateToItems: (Int, String) -> Unit // Función para navegar a los ítems (se usará luego)
) {
    // Observamos los estados del ViewModel
    val listas by viewModel.listas.collectAsState()
    val menuExpandido by viewModel.menuExpandido.collectAsState()
    val listaSeleccionada by viewModel.listaSeleccionada.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Listas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // Lógica del botón "Nueva Lista" (Punto 1 de tus cambios)
            Box {
                FloatingActionButton(onClick = { viewModel.toggleMenu(true) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Nueva Lista")
                }

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { viewModel.toggleMenu(false) }
                ) {
                    DropdownMenuItem(
                        text = { Text("Crear lista nueva") },
                        onClick = {
                            viewModel.toggleMenu(false)
                            viewModel.crearListaDePrueba() // Reemplazaremos esto por un diálogo o pantalla de creación
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Importar lista existente") },
                        onClick = {
                            viewModel.toggleMenu(false)
                            // Aquí irá la lógica de leer .txt o .csv
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Renderizamos las listas
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listas) { lista ->
                ListaItemCard(
                    lista = lista,
                    estaSeleccionada = lista == listaSeleccionada,
                    onClick = {
                        // (Punto 4 de tus cambios) Al presionar, se selecciona
                        if (listaSeleccionada == lista) {
                            viewModel.seleccionarLista(null) // Deselecciona si se vuelve a tocar
                        } else {
                            viewModel.seleccionarLista(lista)
                        }
                    },
                    onDelete = { viewModel.eliminarListaSeleccionada() },
                    onEdit = { /* Lógica para abrir editor de lista */ },
                    onOpen = { onNavigateToItems(lista.listCod, lista.listName) }
                )
            }
        }
    }
}
