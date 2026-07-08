package com.dreamapps.applist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lista")
data class ListaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_cod")
    val listCod: Int = 0,

    @ColumnInfo(name = "list_name")
    val listName: String,

    @ColumnInfo(name = "list_description")
    val listDescription: String? = null,

    @ColumnInfo(name = "list_image")
    val listImage: String? = null,

    @ColumnInfo(name = "list_order")
    val listOrder: Int = 0,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
)