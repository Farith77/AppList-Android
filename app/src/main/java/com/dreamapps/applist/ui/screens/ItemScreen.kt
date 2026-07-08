package com.dreamapps.applist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamapps.applist.data.local.entity.ItemEntity
import com.dreamapps.applist.ui.components.BottomSearchBar
import com.dreamapps.applist.ui.components.ItemRow
import com.dreamapps.applist.ui.viewmodels.ItemViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@Composable
fun ItemScreen(
    viewModel: ItemViewModel,
    onBack: () -> Unit // Ya no necesitamos pedir el nombre de la lista aquí
) {
    // 1. RECOLECCIÓN DE ESTADOS
    val items by viewModel.items.collectAsState()
    val enModoEdicion by viewModel.enModoEdicion.collectAsState()
    val textoEdicion by viewModel.textoEdicion.collectAsState()
    val tituloLista by viewModel.tituloLista.collectAsState()
    val tituloEdicion by viewModel.tituloEdicion.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    val itemsFiltrados = if (textoBusqueda.isBlank()) items else items.filter { it.itemName.contains(textoBusqueda, ignoreCase = true) }

    // 2. ESTRUCTURA DECLARATIVA
    Scaffold(
        topBar = {
            ItemTopBar(
                enModoEdicion = enModoEdicion,
                onBack = onBack,
                onCambiarModo = { viewModel.cambiarModoEdicion(!enModoEdicion) }
            )
        },
        bottomBar = {
            if (!enModoEdicion) {
                BottomSearchBar(
                    textoBusqueda = textoBusqueda,
                    onTextoCambio = { textoBusqueda = it }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TituloDinamico(
                enModoEdicion = enModoEdicion,
                tituloLista = tituloLista,
                tituloEdicion = tituloEdicion,
                onTituloChange = { viewModel.actualizarTituloEdicion(it) }
            )

            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                if (enModoEdicion) {
                    LienzoDeEdicion(
                        textoEdicion = textoEdicion,
                        onTextoChange = { viewModel.actualizarTextoEdicion(it) }
                    )
                } else {
                    ListaArrastrable(
                        itemsFiltrados = itemsFiltrados,
                        textoBusqueda = textoBusqueda,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

// ===================================================================
// SUB-COMPONENTES EXTRÍDOS (Mantenibilidad pura)
// ===================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemTopBar(enModoEdicion: Boolean, onBack: () -> Unit, onCambiarModo: () -> Unit) {
    TopAppBar(
        title = { /* Vacío */ },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = onCambiarModo) {
                Icon(
                    imageVector = if (enModoEdicion) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = "Cambiar estado",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF5364FF))
    )
}

@Composable
private fun TituloDinamico(enModoEdicion: Boolean, tituloLista: String, tituloEdicion: String, onTituloChange: (String) -> Unit) {
    if (enModoEdicion) {
        TextField(
            value = tituloEdicion,
            onValueChange = onTituloChange,
            placeholder = { Text("Lista sin nombre", fontSize = 28.sp, color = Color.LightGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            textStyle = LocalTextStyle.current.copy(fontSize = 28.sp, textAlign = TextAlign.Center),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
    } else {
        Text(
            text = tituloLista,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun LienzoDeEdicion(textoEdicion: String, onTextoChange: (String) -> Unit) {
    TextField(
        value = textoEdicion,
        onValueChange = onTextoChange,
        modifier = Modifier.fillMaxSize(),
        placeholder = { Text("Escribe un ítem por línea...") },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun ListaArrastrable(itemsFiltrados: List<ItemEntity>, textoBusqueda: String, viewModel: ItemViewModel) {
    if (itemsFiltrados.isEmpty()) {
        Text(
            text = if (textoBusqueda.isNotEmpty()) "No se encontraron resultados para '$textoBusqueda'" else "No hay ítems. Presiona el lápiz para agregar algunos.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
    } else {
        val lazyListState = rememberLazyListState()
        val reorderableState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
            viewModel.reordenarItemLocal(from.index, to.index)
        }
        val arrastreHabilitado = textoBusqueda.isEmpty()

        LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
            items(itemsFiltrados, key = { it.itemName }) { item ->
                ReorderableItem(reorderableState, key = item.itemName) { isDragging ->
                    val modificadorArrastre = if (arrastreHabilitado) {
                        Modifier.longPressDraggableHandle(onDragStopped = { viewModel.guardarNuevoOrden() })
                    } else Modifier

                    ItemRow(
                        itemText = item.itemName,
                        modifier = modificadorArrastre.shadow(if (isDragging) 8.dp else 0.dp)
                    )
                }
            }
        }
    }
}