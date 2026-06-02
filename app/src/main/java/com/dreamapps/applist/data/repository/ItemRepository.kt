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
    // 3. SUBIR AL SERVIDOR Y GUARDAR LOCAL (Offline-First Real y Seguro)
    suspend fun guardarItemsSincronizados(listCod: Int, nombresNuevos: List<String>) {
        val itemsAntiguos = itemDao.obtenerItemsListaSync(listCod)
        val itemsABorrar = itemsAntiguos.filter { !nombresNuevos.contains(it.itemName) }
        val nombresAntiguos = itemsAntiguos.map { it.itemName }
        val nombresACrear = nombresNuevos.filter { !nombresAntiguos.contains(it) }

        // --- FASE 1: LOCAL (Siempre funciona, con o sin internet. UI instantánea) ---
        itemsABorrar.forEach { itemDao.eliminarItem(it) }

        nombresACrear.forEach { nombre ->
            val indiceReal = nombresNuevos.indexOf(nombre)
            itemDao.insertarItem(
                ItemEntity(
                    itemName = nombre.trim(),
                    itemOrder = indiceReal,
                    listCod = listCod
                )
            )
        }

        // --- FASE 2: RED (Sincronización segura) ---
        try {
            // 1. Borramos en Spring Boot
            itemsABorrar.forEach { itemFantasma ->
                // Solo mandamos a borrar si el ítem tenía un ID real de Spring Boot (distinto de 0)
                if (itemFantasma.itemCod != 0) {
                    apiService.eliminarItem(listCod, itemFantasma.itemCod)
                }
            }

            // 2. Creamos en Spring Boot
            var huboCambiosExitosos = false
            nombresACrear.forEach { nombre ->
                val indiceReal = nombresNuevos.indexOf(nombre)
                val dto = com.dreamapps.applist.data.remote.model.ItemDto(
                    itemName = nombre.trim(),
                    itemOrder = indiceReal,
                    listCod = listCod
                )

                val responsePost = apiService.crearItem(listCod, dto)
                if (responsePost.isSuccessful) {
                    huboCambiosExitosos = true
                }
            }

            // 3. ¡EL TRUCO MAESTRO!
            // Si logramos subir cosas nuevas al servidor, descargamos la lista fresca
            // en segundo plano. Esto asegura que Room cambie los IDs temporales por
            // los IDs oficiales (1001, 1002...) generados por PostgreSQL.
            if (huboCambiosExitosos) {
                sincronizarItemsConServidor(listCod)
            }

        } catch (e: Exception) {
            // Si no hay internet, no pasa nada. La app sigue funcionando con los IDs temporales.
            Log.e("API_TRACKER", "Modo Offline: Servidor inalcanzable. Se sincronizará luego.")
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

    // 4. ACTUALIZAR EL ORDEN (Drag & Drop)
    suspend fun actualizarOrdenItemsLocales(items: List<ItemEntity>) {
        try {
            // 1. Guardamos el nuevo orden en Room (Local e instantáneo)
            itemDao.actualizarMultiplesItems(items)

            // 2. Filtramos los ítems fantasmas (creados offline sin ID oficial)
            // y convertimos las entidades de Room a DTOs para enviarlos por red.
            val itemsParaSubir = items
                .filter { it.itemCod != 0 } // Solo ítems reales del servidor
                .map { entity ->
                    com.dreamapps.applist.data.remote.model.ItemDto(
                        itemCod = entity.itemCod,
                        itemName = entity.itemName,
                        itemOrder = entity.itemOrder,
                        listCod = entity.listCod
                    )
                }

            // 3. Si hay ítems válidos para subir, le avisamos a Spring Boot
            if (itemsParaSubir.isNotEmpty()) {
                val listCodActual = itemsParaSubir.first().listCod
                val response = apiService.actualizarOrdenItems(listCodActual, itemsParaSubir)

                if (response.isSuccessful) {
                    Log.d("API_TRACKER", "Nuevo orden guardado en Spring Boot con éxito")
                } else {
                    Log.e("API_TRACKER", "Spring Boot rechazó el nuevo orden")
                }
            }
        } catch (e: Exception) {
            Log.e("API_TRACKER", "Modo Offline: No se pudo avisar a Spring Boot sobre el nuevo orden.")
        }
    }
}