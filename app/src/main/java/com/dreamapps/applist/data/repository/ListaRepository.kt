package com.dreamapps.applist.data.repository

import android.util.Log
import com.dreamapps.applist.data.local.dao.ListaDao
import com.dreamapps.applist.data.local.entity.ListaEntity
import com.dreamapps.applist.data.remote.api.ListaApiService
import kotlinx.coroutines.flow.Flow

class ListaRepository(
    private val listaDao: ListaDao,
    private val apiService: ListaApiService
) {
    val listasLocales: Flow<List<ListaEntity>> = listaDao.obtenerListasActivas()
    val listasEnPapelera: Flow<List<ListaEntity>> = listaDao.obtenerListasEnPapelera()

    // Operaciones locales que el ViewModel puede usar
    suspend fun crearListaSincronizada(nombre: String, descripcion: String?) {
        try {
            Log.d("API_TRACKER", "Intentando subir nueva lista a Spring Boot...")

            // 1. Preparamos la "caja" (DTO) para enviarla sin ID (Spring Boot se lo pondrá)
            val nuevaListaDto = com.dreamapps.applist.data.remote.model.ListaDto(
                listName = nombre,
                listDescription = descripcion,
                listOrder = 0 // Orden por defecto
            )

            // 2. Mandamos al "repartidor" (Retrofit)
            val response = apiService.crearLista(nuevaListaDto)

            if (response.isSuccessful) {
                // 3. El servidor nos devuelve la lista ya con su ID oficial creado en PostgreSQL
                val listaCreadaEnServer = response.body()

                if (listaCreadaEnServer != null) {
                    // 4. La traducimos y la guardamos en Room
                    val entidadOficial = ListaEntity(
                        listCod = listaCreadaEnServer.listCod, // ID oficial del backend
                        listName = listaCreadaEnServer.listName,
                        listDescription = listaCreadaEnServer.listDescription,
                        listOrder = listaCreadaEnServer.listOrder
                    )
                    listaDao.insertarLista(entidadOficial)
                    Log.d("API_TRACKER", "ÉXITO: Lista subida y guardada localmente con ID: ${entidadOficial.listCod}")
                }
            } else {
                Log.e("API_TRACKER", "El servidor rechazó la creación: ${response.code()}")
                guardarSoloLocal(nombre, descripcion)
            }
        } catch (e: Exception) {
            Log.e("API_TRACKER", "Sin conexión. Guardando en modo Offline-First. Detalle: ${e.message}")
            guardarSoloLocal(nombre, descripcion)
        }
    }

    suspend fun eliminarListaSincronizada(lista: ListaEntity) {
        try {
            // Eliminado lógico en Room local (se va a la papelera)
            listaDao.moverListaAPapelera(lista.listCod)

            // TODO: Más adelante, cuando paguemos la deuda técnica,
            // le avisaremos a Spring Boot que esta lista fue eliminada lógicamente.
        } catch (e: Exception) {
            Log.e("API_TRACKER", "Error al enviar lista a papelera: ${e.message}")
        }
    }

    suspend fun sincronizarListasConServidor() {
        try {
            Log.d("API_TRACKER", "1. Iniciando llamada a Spring Boot...")
            val response = apiService.obtenerListas()

            if (response.isSuccessful) {
                val listasRemotas = response.body() ?: emptyList()
                Log.d("API_TRACKER", "2. Éxito! Spring Boot devolvió ${listasRemotas.size} listas.")

                val entidades = listasRemotas.map { dto ->
                    ListaEntity(
                        listCod = dto.listCod,
                        listName = dto.listName,
                        listDescription = dto.listDescription,
                        listOrder = dto.listOrder
                    )
                }

                entidades.forEach {
                    listaDao.insertarLista(it)
                    Log.d("API_TRACKER", "3. Lista guardada en Room: ${it.listName}")
                }
            } else {
                Log.e("API_TRACKER", "ERROR SERVER: El servidor respondió con código ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("API_TRACKER", "ERROR RED/CONVERSIÓN: La app falló antes de conectar o al leer el JSON. Detalle: ${e.message}")
        }
    }

    // Función auxiliar de respaldo por si no hay internet
    private suspend fun guardarSoloLocal(nombre: String, descripcion: String?) {
        val entidadTemporal = ListaEntity(
            listName = nombre,
            listDescription = descripcion
        )
        listaDao.insertarLista(entidadTemporal)
    }

    suspend fun restaurarLista(listCod: Int) {
        listaDao.restaurarLista(listCod)
    }

    suspend fun eliminarListaFisicamente(lista: ListaEntity) {
        listaDao.eliminarListaFisicamente(lista)
        // TODO (Deuda técnica): Aquí también le avisaremos a Spring Boot para que haga el Hard Delete en la nube
    }

    suspend fun vaciarPapelera() {
        listaDao.vaciarPapelera()
        // TODO: Avisar a Spring Boot de vaciar papelera
    }

    suspend fun crearListaRapidaLocal(nombre: String): Int {
        val nuevaLista = com.dreamapps.applist.data.local.entity.ListaEntity(
            listName = nombre,
            listDescription = ""
        )
        // Room inserta y nos devuelve el ID generado
        return listaDao.insertarLista(nuevaLista).toInt()
    }
}