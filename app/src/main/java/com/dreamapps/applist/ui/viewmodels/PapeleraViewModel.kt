package com.dreamapps.applist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamapps.applist.data.local.entity.ListaEntity
import com.dreamapps.applist.data.repository.ListaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PapeleraViewModel(private val repository: ListaRepository) : ViewModel() {

    val listasBorradas: StateFlow<List<ListaEntity>> = repository.listasEnPapelera
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun restaurarLista(lista: ListaEntity) {
        viewModelScope.launch {
            repository.restaurarLista(lista.listCod)
        }
    }

    fun eliminarFisicamente(lista: ListaEntity) {
        viewModelScope.launch {
            repository.eliminarListaFisicamente(lista)
        }
    }

    fun vaciarPapelera() {
        viewModelScope.launch {
            repository.vaciarPapelera()
        }
    }
}