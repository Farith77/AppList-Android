package com.dreamapps.applist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamapps.applist.data.repository.ItemRepository
import com.dreamapps.applist.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ItemViewModel(
    private val repository: ItemRepository,
    private val listCod: Int
) : ViewModel() {

    // 1. EL AMORTIGUADOR: Almacena los ítems para la UI y permite arrastrarlos instantáneamente
    private val _itemsUI = MutableStateFlow<List<ItemEntity>>(emptyList())
    val items: StateFlow<List<ItemEntity>> = _itemsUI.asStateFlow()

    val enModoEdicion = MutableStateFlow(false)
    val textoEdicion = MutableStateFlow("")

    init {
        // 2. Escuchamos a la base de datos (Room) y pasamos los datos al amortiguador
        viewModelScope.launch {
            repository.obtenerItemsLocales(listCod).collect { listaDesdeRoom ->
                _itemsUI.value = listaDesdeRoom
            }
        }

        // 3. Sincronizamos con el servidor Spring Boot en segundo plano
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
            val lineas = textoEdicion.value.lines().filter { it.isNotBlank() }
            repository.guardarItemsSincronizados(listCod, lineas)
        }
    }

    // 4. NUEVA LÓGICA DE ARRASTRE: Manipulamos el amortiguador con .update
    fun reordenarItemLocal(fromIndex: Int, toIndex: Int) {
        _itemsUI.update { listaActual ->
            val listaMutable = listaActual.toMutableList()
            val itemMovido = listaMutable.removeAt(fromIndex)
            listaMutable.add(toIndex, itemMovido)
            listaMutable // Retornamos la nueva lista para que la UI se repinte sola
        }
    }

    // Guarda el orden definitivo (Lo conectaremos a Room/Spring Boot en el siguiente paso)
    fun guardarNuevoOrden() {
        viewModelScope.launch {
            // 1. Tomamos la lista visual que el usuario acaba de ordenar en pantalla
            val listaOrdenada = _itemsUI.value

            // 2. Le reasignamos el número de posición (índice 0, 1, 2...) a cada ítem
            val listaActualizada = listaOrdenada.mapIndexed { index, item ->
                item.copy(itemOrder = index) // Usamos itemOrder, tu variable de Kotlin
            }

            // 3. Le pasamos el trabajo al Repositorio
            repository.actualizarOrdenItemsLocales(listaActualizada)
        }
    }
}