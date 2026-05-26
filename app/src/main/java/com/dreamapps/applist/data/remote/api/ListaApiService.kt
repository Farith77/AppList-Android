package com.dreamapps.applist.data.remote.api

import com.dreamapps.applist.data.remote.model.ListaDto
import retrofit2.Response
import retrofit2.http.*

interface ListaApiService {

    // Hace una petición GET a http://10.0.2.2:8080/api/listas
    @GET("api/listas")
    suspend fun obtenerListas(): Response<List<ListaDto>>

    // Hace una petición POST enviando el JSON de la nueva lista
    @POST("api/listas")
    suspend fun crearLista(@Body lista: ListaDto): Response<ListaDto>

    // Hace una petición DELETE pasándole el ID en la URL
    @DELETE("api/listas/{id}")
    suspend fun eliminarLista(@Path("id") id: Int): Response<Unit>
}