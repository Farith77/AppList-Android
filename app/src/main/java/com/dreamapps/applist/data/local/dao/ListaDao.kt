package com.dreamapps.applist.data.local.dao

import androidx.room.*
import com.dreamapps.applist.data.local.entity.ListaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListaDao {

    // Modificamos la consulta para que SOLO traiga las listas que NO están eliminadas
    @Query("SELECT * FROM lista WHERE is_deleted = 0 ORDER BY list_cod DESC")
    fun obtenerListasActivas(): Flow<List<ListaEntity>>

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

    // Función para enviar a la papelera (Eliminado Lógico)
    @Query("UPDATE lista SET is_deleted = 1 WHERE list_cod = :listCod")
    suspend fun moverListaAPapelera(listCod: Int)

    // Obtener solo las eliminadas lógicamente
    @Query("SELECT * FROM lista WHERE is_deleted = 1 ORDER BY list_cod DESC")
    fun obtenerListasEnPapelera(): Flow<List<ListaEntity>>

    // Restaurar (Volver a ocultar la bandera)
    @Query("UPDATE lista SET is_deleted = 0 WHERE list_cod = :listCod")
    suspend fun restaurarLista(listCod: Int)

    // ELIMINADO FÍSICO de una sola lista (Hard Delete)
    @Delete
    suspend fun eliminarListaFisicamente(lista: ListaEntity)

    // ELIMINADO FÍSICO MASIVO (Vaciar papelera)
    @Query("DELETE FROM lista WHERE is_deleted = 1")
    suspend fun vaciarPapelera()
}