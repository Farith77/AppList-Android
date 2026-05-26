package com.dreamapps.applist.data.remote.model

import com.google.gson.annotations.SerializedName

data class ItemDto(
    @SerializedName("itemCod") val itemCod: Int = 0,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("itemOrder") val itemOrder: Int = 0,
    @SerializedName("listCod") val listCod: Int // El ID de la lista a la que pertenece
)