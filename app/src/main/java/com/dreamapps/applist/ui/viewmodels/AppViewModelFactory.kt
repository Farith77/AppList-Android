package com.dreamapps.applist.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dreamapps.applist.data.repository.ListaRepository

class AppViewModelFactory(private val repository: ListaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListaViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}