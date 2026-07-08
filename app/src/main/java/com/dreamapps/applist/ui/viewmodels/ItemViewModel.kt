package com.dreamapps.applist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamapps.applist.data.repository.ItemRepository
import com.dreamapps.applist.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.dreamapps.applist.data.repository.ListaRepository


class ItemViewModel(
    private val itemRepository: ItemRepository,
    private val listaRepository: ListaRepository,
    private val listCodOriginal: Int,
    nombreOriginal: String
) : ViewModel() {
    // 1. EL AMORTIGUADOR: Almacena los ítems para la UI y permite arrastrarlos instantáneamente
    private val _itemsUI = MutableStateFlow<List<ItemEntity>>(emptyList())
    val items: StateFlow<List<ItemEntity>> = _itemsUI.asStateFlow()
    private var currentListCod = listCodOriginal
    val enModoEdicion = MutableStateFlow(false)
    val textoEdicion = MutableStateFlow("")
    val tituloLista = MutableStateFlow(
        if (nombreOriginal.isNotBlank()) nombreOriginal
        else "Lista sin nombre"
    )
    val tituloEdicion = MutableStateFlow("")

    init {
        // Lógica Reactiva
        if (currentListCod == -1) {
            // ES UNA LISTA NUEVA: Iniciamos en modo edición
            enModoEdicion.value = true
        } else {
            // ES UNA LISTA EXISTENTE: Descargamos datos
            viewModelScope.launch {
                itemRepository.obtenerItemsLocales(currentListCod).collect { listaDesdeRoom ->
                    _itemsUI.value = listaDesdeRoom
                }
            }
            viewModelScope.launch {
                itemRepository.sincronizarItemsConServidor(currentListCod)
            }
        }
    }

    fun cambiarModoEdicion(editar: Boolean) {
        enModoEdicion.value = editar
        if (editar) {
            val textoActual = items.value.joinToString("\n") { it.itemName }
            textoEdicion.value = textoActual
            tituloEdicion.value = if (tituloLista.value == "Lista sin nombre") "" else tituloLista.value
        } else {
            // --- INICIO DEL GUARDADO DEMORADO ---
            val tituloFinal = if (tituloEdicion.value.isBlank()) "Lista sin nombre" else tituloEdicion.value.trim()
            tituloLista.value = tituloFinal

            viewModelScope.launch {
                if (currentListCod == -1) {
                    // 1. Es una lista fantasma. La creamos en Room y obtenemos el ID real.
                    currentListCod = listaRepository.crearListaRapidaLocal(tituloFinal)

                    // 2. Ahora que ya tiene un ID real, empezamos a escuchar los cambios en BD
                    launch {
                        itemRepository.obtenerItemsLocales(currentListCod).collect { listaDesdeRoom ->
                            _itemsUI.value = listaDesdeRoom
                        }
                    }
                }

                // 3. Guardamos los ítems usando el ID real
                guardarItemsDesdeTexto()
            }
            // --- FIN DEL GUARDADO DEMORADO ---
        }
    }

    fun actualizarTextoEdicion(nuevoTexto: String) {
        textoEdicion.value = nuevoTexto
    }

    // función para actualizar el título mientras escribe
    fun actualizarTituloEdicion(nuevoTitulo: String) {
        tituloEdicion.value = nuevoTitulo
    }

    private fun guardarItemsDesdeTexto() {
        viewModelScope.launch {
            val lineas = textoEdicion.value.lines().filter { it.isNotBlank() }
            itemRepository.guardarItemsSincronizados(currentListCod, lineas)
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
            itemRepository.actualizarOrdenItemsLocales(listaActualizada)
        }
    }
}