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