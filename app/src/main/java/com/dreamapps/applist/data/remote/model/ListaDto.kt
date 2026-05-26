package com.dreamapps.applist.data.remote.model

import com.google.gson.annotations.SerializedName

data class ListaDto(
    // @SerializedName asegura que el nombre coincida con el JSON de Spring Boot,
    // incluso si decides cambiar el nombre de la variable en Kotlin
    @SerializedName("listCod") val listCod: Int = 0,
    @SerializedName("listName") val listName: String,
    @SerializedName("listDescription") val listDescription: String? = null,
    @SerializedName("listImage") val listImage: String? = null,
    @SerializedName("listOrder") val listOrder: Int = 0
)