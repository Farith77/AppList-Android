package com.dreamapps.applist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamapps.applist.data.local.dao.ListaDao
import com.dreamapps.applist.data.local.entity.ListaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.dreamapps.applist.data.repository.ListaRepository

// CAMBIO: Ahora recibimos el Repository en lugar del Dao
class ListaViewModel(private val repository: ListaRepository) : ViewModel() {

    // CAMBIO: Leemos la única fuente de verdad del repositorio
    val listas: StateFlow<List<ListaEntity>> = repository.listasLocales
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val menuExpandido = MutableStateFlow(false)
    val listaSeleccionada = MutableStateFlow<ListaEntity?>(null)
    val listasSeleccionadas = MutableStateFlow<Set<ListaEntity>>(emptySet())

    // CAMBIO: Se ejecuta automáticamente al abrir la pantalla
    init {
        sincronizarConBackend()
    }

    private fun sincronizarConBackend() {
        viewModelScope.launch {
            repository.sincronizarListasConServidor()
        }
    }

    fun toggleMenu(expandido: Boolean) { menuExpandido.value = expandido }
    fun seleccionarLista(lista: ListaEntity?) { listaSeleccionada.value = lista }

    // Activa el modo selección con el primer toque largo
    fun activarModoSeleccion(lista: ListaEntity) {
        listasSeleccionadas.value = setOf(lista)
    }

    // Marca o desmarca una lista cuando ya estamos en modo selección
    fun toggleSeleccion(lista: ListaEntity) {
        val actuales = listasSeleccionadas.value.toMutableSet()
        if (actuales.contains(lista)) actuales.remove(lista) else actuales.add(lista)
        listasSeleccionadas.value = actuales
    }

    // Selecciona todas las listas de la pantalla
    fun seleccionarTodas() {
        if (listasSeleccionadas.value.size == listas.value.size) {
            limpiarSeleccion() // Deselecciona todas (y oculta la barra superior)
        } else {
            listasSeleccionadas.value = listas.value.toSet() // Selecciona todas
        }
    }

    // Cancela la selección
    fun limpiarSeleccion() {
        listasSeleccionadas.value = emptySet()
    }

    // --- ELIMINADO EN MASA ---
    fun eliminarListasSeleccionadas() {
        viewModelScope.launch {
            listasSeleccionadas.value.forEach { lista ->
                repository.eliminarListaSincronizada(lista)
            }
            limpiarSeleccion() // Salimos del modo selección tras borrar
        }
    }

    fun eliminarListaSeleccionada() {
        listaSeleccionada.value?.let { lista ->
            viewModelScope.launch {
                repository.eliminarListaSincronizada(lista) // Usa el repositorio
                seleccionarLista(null)
            }
        }
    }

    fun crearListaDePrueba() {
        viewModelScope.launch {
            // Ahora usamos la función inteligente del repositorio
            repository.crearListaSincronizada(
                nombre = "Nueva Lista Sincronizada",
                descripcion = "Creada desde mi celular físico"
            )
        }
    }
}