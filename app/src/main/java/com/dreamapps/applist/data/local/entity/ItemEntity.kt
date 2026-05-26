package com.dreamapps.applist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dreamapps.applist.data.local.entity.ListaEntity

@Entity(
    tableName = "item",
    foreignKeys = [
        ForeignKey(
            entity = ListaEntity::class,
            parentColumns = arrayOf("list_cod"),
            childColumns = arrayOf("list_cod"),
            onDelete = ForeignKey.Companion.CASCADE // ¡Estándar SQA!
        )
    ],
    // Indexar la llave foránea mejora drásticamente el rendimiento de las consultas
    indices = [Index(value = ["list_cod"])]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_cod")
    val itemCod: Int = 0,

    @ColumnInfo(name = "item_name")
    val itemName: String,

    @ColumnInfo(name = "item_order")
    val itemOrder: Int = 0,

    // En Room, solo guardamos el ID de la lista a la que pertenece
    @ColumnInfo(name = "list_cod")
    val listCod: Int
)