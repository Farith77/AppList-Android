package com.dreamapps.applist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamapps.applist.ui.viewmodels.ListaViewModel
import com.dreamapps.applist.ui.components.ListaItemCard
import androidx.compose.ui.graphics.Color
import com.dreamapps.applist.ui.components.AppDrawerSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onNavigateToItems: (Int, String) -> Unit,
    onNavigateToPapelera: () -> Unit
) {
    // Observamos los estados del ViewModel
    val listas by viewModel.listas.collectAsState()
    val menuExpandido by viewModel.menuExpandido.collectAsState()
    val listasSeleccionadas by viewModel.listasSeleccionadas.collectAsState()

    // Variables de UI para la multiselección y alertas
    val modoSeleccionActivo = listasSeleccionadas.isNotEmpty()
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // ESTADOS PARA EL MENÚ LATERAL (DRAWER)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerSheet(
                rutaActual = "listas",
                onNavigateToListas = { /* Ya estamos aquí */ },
                onNavigateToPapelera = { onNavigateToPapelera() },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                // Diseño centrado y limpio
                CenterAlignedTopAppBar(
                    title = { Text("Mis Listas") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú Principal")
                        }
                    },
                    actions = {
                        // Integramos tu menú desplegable en la barra superior
                        Box {
                            IconButton(onClick = { viewModel.toggleMenu(true) }) {
                                Icon(Icons.Filled.Add, contentDescription = "Opciones de Lista")
                            }

                            DropdownMenu(
                                expanded = menuExpandido,
                                onDismissRequest = { viewModel.toggleMenu(false) }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Crear lista nueva") },
                                    onClick = {
                                        viewModel.toggleMenu(false)
                                        onNavigateToItems(
                                            -1,
                                            ""
                                        ) // Navegación con guardado demorado
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
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF5364FF), // <-- azul personalizado para el fondo
                        titleContentColor = Color.White,    // <-- El color del texto del título
                        actionIconContentColor = Color.White, // <-- El color de los iconos (Lupa, Menú, etc)
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {

                // BARRA DE ACCIÓN CONTEXTUAL (Estilo Minimalista)
                if (modoSeleccionActivo) {
                    // Evaluamos si todas las listas están seleccionadas
                    val todasSeleccionadas = listasSeleccionadas.size == listas.size && listas.isNotEmpty()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically // Alineamos el texto con los íconos
                    ) {
                        // 1. El contador de listas seleccionadas
                        Text(
                            text = "${listasSeleccionadas.size}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(end = 4.dp)
                        )

                        // 2. El botón dinámico (Check ✓ o Equis X)
                        IconButton(onClick = { viewModel.seleccionarTodas() }) {
                            Icon(
                                imageVector = if (todasSeleccionadas) Icons.Filled.Close else Icons.Filled.CheckCircle,
                                contentDescription = if (todasSeleccionadas) "Deseleccionar Todas" else "Seleccionar Todas"
                            )
                        }

                        // 3. El botón de eliminar
                        IconButton(onClick = { mostrarDialogoEliminar = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar Seleccionadas")
                        }
                    }
                }

                // LA LISTA PRINCIPAL
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listas, key = { it.listCod }) { lista ->
                        ListaItemCard(
                            lista = lista,
                            estaSeleccionada = listasSeleccionadas.contains(lista),
                            modoSeleccionActivo = modoSeleccionActivo,
                            onClick = {
                                if (modoSeleccionActivo) {
                                    viewModel.toggleSeleccion(lista) // Si estamos seleccionando, marca/desmarca
                                } else {
                                    onNavigateToItems(
                                        lista.listCod,
                                        lista.listName
                                    ) // Si no, entra directo a los ítems
                                }
                            },
                            onLongClick = {
                                if (!modoSeleccionActivo) {
                                    viewModel.activarModoSeleccion(lista) // Inicia la selección múltiple
                                }
                            }
                        )
                    }
                }
            }

            // DIÁLOGO DE CONFIRMACIÓN (Adaptado para una o múltiples listas)
            if (mostrarDialogoEliminar) {
                val cantidad = listasSeleccionadas.size
                AlertDialog(
                    onDismissRequest = { mostrarDialogoEliminar = false },
                    title = { Text(if (cantidad == 1) "¿Eliminar lista?" else "¿Eliminar $cantidad listas?") },
                    text = { Text("Se enviarán a la papelera.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.eliminarListasSeleccionadas()
                                mostrarDialogoEliminar = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoEliminar = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}