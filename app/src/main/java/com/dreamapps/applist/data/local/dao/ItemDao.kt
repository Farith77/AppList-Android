package com.dreamapps.applist.data.local.dao

import androidx.room.*
import com.dreamapps.applist.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    // Obtener ítems de una lista específica, ordenados por la posición personalizada
    @Query("SELECT * FROM item WHERE list_cod = :listCod ORDER BY item_order ASC")
    fun obtenerItemsPorLista(listCod: Int): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarItem(item: ItemEntity)

    // Útil para cuando el usuario guarde su "app de notas" y tengamos que insertar varios de golpe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMultiplesItems(items: List<ItemEntity>)

    @Update
    suspend fun actualizarItem(item: ItemEntity)

    @Delete
    suspend fun eliminarItem(item: ItemEntity)

    // NUEVO: Permite borrar todos los ítems de una lista específica
    @Query("DELETE FROM item WHERE list_cod = :listCod")
    suspend fun eliminarItemsPorLista(listCod: Int)

    // NUEVO: Toma una "fotografía" instantánea de los ítems actuales sin usar Flow
    @Query("SELECT * FROM item WHERE list_cod = :listCod")
    suspend fun obtenerItemsListaSync(listCod: Int): List<ItemEntity>
}