package com.dreamapps.applist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamapps.applist.data.repository.ItemRepository
import com.dreamapps.applist.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// CAMBIO: Ahora recibe el ItemRepository
class ItemViewModel(
    private val repository: ItemRepository,
    private val listCod: Int
) : ViewModel() {

    // 1. Leemos la única fuente de verdad (Room a través del Repositorio)
    val items: StateFlow<List<ItemEntity>> = repository.obtenerItemsLocales(listCod)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enModoEdicion = MutableStateFlow(false)
    val textoEdicion = MutableStateFlow("")

    // 2. Al abrir la pantalla, descargamos los ítems del servidor
    init {
        viewModelScope.launch {
            repository.sincronizarItemsConServidor(listCod)
        }
    }

    fun cambiarModoEdicion(editar: Boolean) {
        enModoEdicion.value = editar
        if (editar) {
            val textoActual = items.value.joinToString("\n") { it.itemName }
            textoEdicion.value = textoActual
        } else {
            // Pasamos a visualización: subimos los cambios
            guardarItemsDesdeTexto()
        }
    }

    fun actualizarTextoEdicion(nuevoTexto: String) {
        textoEdicion.value = nuevoTexto
    }

    private fun guardarItemsDesdeTexto() {
        viewModelScope.launch {
            // Filtramos las líneas vacías
            val lineas = textoEdicion.value.lines().filter { it.isNotBlank() }

            // Le pasamos el trabajo pesado al Repositorio (Borrar locales, Subir a Spring Boot, Guardar locales)
            repository.guardarItemsSincronizados(listCod, lineas)
        }
    }
}