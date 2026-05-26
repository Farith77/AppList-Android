package com.dreamapps.applist.data.local.dao

import androidx.room.*
import com.dreamapps.applist.data.local.entity.ListaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListaDao {

    // Obtener todas las listas. 'Flow' hace que la UI se actualice sola si la base de datos cambia.
    @Query("SELECT * FROM lista ORDER BY list_order ASC")
    fun obtenerTodasLasListas(): Flow<List<ListaEntity>>

    // Insertar o actualizar. 'suspend' significa que debe correr en un hilo secundario (Corrutina).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(lista: ListaEntity): Long

    @Update
    suspend fun actualizarLista(lista: ListaEntity)

    @Delete
    suspend fun eliminarLista(lista: ListaEntity)

    // Requisito Edu-01: Eliminar varias listas seleccionadas
    @Query("DELETE FROM lista WHERE list_cod IN (:ids)")
    suspend fun eliminarListasPorIds(ids: List<Int>)
}