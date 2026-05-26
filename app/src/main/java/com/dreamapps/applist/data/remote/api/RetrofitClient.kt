package com.dreamapps.applist.data.remote.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.dreamapps.applist.BuildConfig

object RetrofitClient {
    // ip del momento
    private const val BASE_URL = BuildConfig.BASE_URL

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // servicio para las listas
    val apiService: ListaApiService by lazy {
        retrofit.create(ListaApiService::class.java)
    }

    // servicio para los ítems
    val itemApiService: ItemApiService by lazy {
        retrofit.create(ItemApiService::class.java)
    }
}