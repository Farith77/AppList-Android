package com.dreamapps.applist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamapps.applist.ui.components.BottomSearchBar
import com.dreamapps.applist.ui.components.ItemRow
import com.dreamapps.applist.ui.viewmodels.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    viewModel: ItemViewModel,
    nombreLista: String,
    onBack: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val enModoEdicion by viewModel.enModoEdicion.collectAsState()
    val textoEdicion by viewModel.textoEdicion.collectAsState()

    // Estado para la barra de búsqueda inferior
    var textoBusqueda by remember { mutableStateOf("") }
    // NUEVA LÓGICA: Filtramos los ítems en tiempo real
    val itemsFiltrados = if (textoBusqueda.isBlank()) {
        items // Si no hay texto, mostramos todos
    } else {
        items.filter { it.itemName.contains(textoBusqueda, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Vacío, el título va abajo */ },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cambiarModoEdicion(!enModoEdicion) }) {
                        Icon(
                            imageVector = if (enModoEdicion) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = "Cambiar estado",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5364FF) // Fondo azul del encabezado
                )
            )
        },
        bottomBar = {
            // Detalle UX: Ocultamos la barra de búsqueda si estamos escribiendo ítems
            if (!enModoEdicion) {
                BottomSearchBar(
                    textoBusqueda = textoBusqueda,
                    onTextoCambio = { textoBusqueda = it } // Actualizamos el estado de la pantalla
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // TÍTULO FIJO (Ningún ítem puede subir más allá de aquí)
            Text(
                text = nombreLista,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            // CONTENIDO (Lista o App de Notas)
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                if (enModoEdicion) {
                    TextField(
                        value = textoEdicion,
                        onValueChange = { viewModel.actualizarTextoEdicion(it) },
                        modifier = Modifier.fillMaxSize(),
                        placeholder = { Text("Escribe tus ítems aquí, uno por cada línea...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                } else {
                    // LÓGICA DE BÚSQUEDA MEJORADA
                    if (itemsFiltrados.isEmpty()) {
                        if (textoBusqueda.isNotEmpty()) {
                            // Si está buscando y no hay resultados
                            Text(
                                text = "No se encontraron resultados para '$textoBusqueda'",
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                            )
                        } else {
                            // Si la lista está totalmente vacía desde el inicio
                            Text(
                                text = "No hay ítems. Presiona el lápiz para agregar algunos.",
                                modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
                            )
                        }
                    } else {
                        // LA LISTA DESPLAZABLE
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(itemsFiltrados) { item ->
                                ItemRow(itemText = item.itemName)
                            }
                        }
                    }
                }
            }
        }
    }
}