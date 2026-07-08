package com.dreamapps.applist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dreamapps.applist.data.repository.ItemRepository
import com.dreamapps.applist.data.repository.ListaRepository

class ItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val listaRepository: ListaRepository,
    private val listCod: Int,
    private val nombreOriginal: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemRepository, listaRepository, listCod, nombreOriginal) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}