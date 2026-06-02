package com.dreamapps.applist.data.remote.api

import com.dreamapps.applist.data.remote.model.ItemDto
import retrofit2.Response
import retrofit2.http.*

interface ItemApiService {

    // Coincide con: @GetMapping en "/api/listas/{listCod}/items"
    @GET("api/listas/{listCod}/items")
    suspend fun obtenerItemsPorLista(@Path("listCod") listCod: Int): Response<List<ItemDto>>

    // Coincide con: @PostMapping en "/api/listas/{listCod}/items"
    @POST("api/listas/{listCod}/items")
    suspend fun crearItem(
        @Path("listCod") listCod: Int,
        @Body item: ItemDto
    ): Response<ItemDto>

    // Coincide con: @DeleteMapping("/{itemCod}") en "/api/listas/{listCod}/items"
    @DELETE("api/listas/{listCod}/items/{itemCod}")
    suspend fun eliminarItem(
        @Path("listCod") listCod: Int,
        @Path("itemCod") itemCod: Int
    ): Response<Unit>

    // Petición PUT para enviar el nuevo orden de los ítems
    @PUT("api/listas/{listCod}/items/orden")
    suspend fun actualizarOrdenItems(
        @Path("listCod") listCod: Int,
        @Body items: List<com.dreamapps.applist.data.remote.model.ItemDto>
    ): retrofit2.Response<Void>
}