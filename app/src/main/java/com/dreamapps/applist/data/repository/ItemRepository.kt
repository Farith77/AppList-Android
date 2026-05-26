package com.dreamapps.applist.data.repository

import android.util.Log
import com.dreamapps.applist.data.local.dao.ItemDao
import com.dreamapps.applist.data.local.entity.ItemEntity
import com.dreamapps.applist.data.remote.api.ItemApiService
import kotlinx.coroutines.flow.Flow

class ItemRepository(
    private val itemDao: ItemDao,
    private val apiService: ItemApiService
) {
    // 1. LA ÚNICA FUENTE DE VERDAD: La UI lee los ítems desde Room
    fun obtenerItemsLocales(listCod: Int): Flow<List<ItemEntity>> {
        return itemDao.obtenerItemsPorLista(listCod)
    }

    // 2. DESCARGAR DEL SERVIDOR (GET)
    suspend fun sincronizarItemsConServidor(listCod: Int) {
        try {
            val response = apiService.obtenerItemsPorLista(listCod)
            if (response.isSuccessful) {
                val itemsRemotos = response.body() ?: emptyList()

                // Limpiamos los ítems viejos de esta lista específica antes de insertar los nuevos
                // para evitar acumulaciones si se borraron en el servidor.
                itemDao.eliminarItemsPorLista(listCod)

                val entidades = itemsRemotos.map { dto ->
                    ItemEntity(
                        itemCod = dto.itemCod,
                        itemName = dto.itemName,
                        itemOrder = dto.itemOrder,
                        listCod = listCod // Aseguramos la relación
                    )
                }
                itemDao.insertarMultiplesItems(entidades)
            }
        } catch (e: Exception) {
            Log.e("API_TRACKER", "Error sincronizando ítems: ${e.message}")
        }
    }

    // 3. SUBIR AL SERVIDOR Y GUARDAR LOCAL (Smart Diff)
    suspend fun guardarItemsSincronizados(listCod: Int, nombresNuevos: List<String>) {
        try {
            // 1. Tomamos la fotografía de lo que hay actualmente en Room
            val itemsAntiguos = itemDao.obtenerItemsListaSync(listCod)

            // 2. DETECTAR ELIMINADOS: Si un ítem antiguo ya no está en el texto nuevo, hay que borrarlo
            val itemsABorrar = itemsAntiguos.filter { antiguo ->
                !nombresNuevos.contains(antiguo.itemName)
            }

            // 3. DETECTAR NUEVOS: Si una línea del texto no existía en Room, hay que crearla
            val nombresAntiguos = itemsAntiguos.map { it.itemName }
            val nombresACrear = nombresNuevos.filter { nuevo ->
                !nombresAntiguos.contains(nuevo)
            }

            // --- FASE DE RED: Hablar con Spring Boot ---

            // A) Enviar las órdenes de ELIMINAR (DELETE)
            itemsABorrar.forEach { itemFantasma ->
                val responseDelete = apiService.eliminarItem(listCod, itemFantasma.itemCod)
                if (responseDelete.isSuccessful) {
                    itemDao.eliminarItem(itemFantasma) // Lo borramos de Room también
                    Log.d("API_TRACKER", "Ítem eliminado con éxito: ${itemFantasma.itemName}")
                } else {
                    Log.e("API_TRACKER", "Spring Boot no pudo eliminar: ${itemFantasma.itemName}")
                }
            }

            // B) Enviar las órdenes de CREAR (POST)
            // (Usamos los nombresNuevos completos para calcular el orden correcto en la lista)
            nombresACrear.forEach { nombre ->
                val indiceReal = nombresNuevos.indexOf(nombre)
                val dto = com.dreamapps.applist.data.remote.model.ItemDto(
                    itemName = nombre.trim(),
                    itemOrder = indiceReal,
                    listCod = listCod
                )

                val responsePost = apiService.crearItem(listCod, dto)
                if (responsePost.isSuccessful) {
                    val itemCreado = responsePost.body()
                    if (itemCreado != null) {
                        itemDao.insertarItem(
                            ItemEntity(
                                itemCod = itemCreado.itemCod,
                                itemName = itemCreado.itemName,
                                itemOrder = itemCreado.itemOrder,
                                listCod = listCod
                            )
                        )
                        Log.d("API_TRACKER", "Ítem creado con éxito: ${itemCreado.itemName}")
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("API_TRACKER", "Error de red al hacer CRUD de ítems: ${e.message}")
            // Si quieres que funcione offline perfecto, aquí deberías meter los datos a Room
            // y marcarlos como pendientes, pero para el MVP lo dejaremos en Log.
        }
    }

    private suspend fun guardarSoloLocal(nombre: String, orden: Int, listCod: Int) {
        itemDao.insertarItem(
            ItemEntity(
                itemName = nombre.trim(),
                itemOrder = orden,
                listCod = listCod
            )
        )
    }
}