package com.dreamapps.applist.ui.navigation

import kotlinx.serialization.Serializable

// Pantalla principal (No necesita recibir datos)
@Serializable
data object DestinoLista

// Pantalla de ítems (Necesita saber qué lista abrir y su nombre)
@Serializable
data class DestinoItems(
    val listCod: Int,
    val nombreLista: String
)