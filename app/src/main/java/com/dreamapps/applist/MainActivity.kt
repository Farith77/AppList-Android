package com.dreamapps.applist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dreamapps.applist.data.local.database.AppDatabase
import com.dreamapps.applist.ui.navigation.DestinoItems
import com.dreamapps.applist.ui.navigation.DestinoLista
import com.dreamapps.applist.ui.screens.ItemScreen
import com.dreamapps.applist.ui.screens.ListaScreen
import com.dreamapps.applist.ui.theme.AppListTheme
import com.dreamapps.applist.ui.viewmodels.AppViewModelFactory
import com.dreamapps.applist.ui.viewmodels.ItemViewModel
import com.dreamapps.applist.ui.viewmodels.ListaViewModel
import com.dreamapps.applist.data.remote.api.RetrofitClient
import com.dreamapps.applist.data.repository.ItemRepository
import com.dreamapps.applist.ui.viewmodels.ItemViewModelFactory
import com.dreamapps.applist.data.repository.ListaRepository
import com.dreamapps.applist.ui.navigation.DestinoPapelera
import com.dreamapps.applist.ui.viewmodels.PapeleraViewModel
import com.dreamapps.applist.ui.screens.PapeleraScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos la base de datos local (Room)
        val database = AppDatabase.getDatabase(this)

        // 2. Inicializamos el cliente de Retrofit
        val apiService = RetrofitClient.apiService
        val itemApiService = RetrofitClient.itemApiService

        // 3. Creamos el Repositorio dándole ambas herramientas
        val listaRepository = ListaRepository(database.listaDao(), apiService)
        val itemRepository = ItemRepository(database.itemDao(), itemApiService)

        // 4. Se lo pasamos al Factory
        val factory = AppViewModelFactory(listaRepository)

        setContent {
            AppListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. El Router centralizado
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = DestinoLista
                    ) {
                        // --- RUTA 1: Pantalla de Listas ---
                        composable<DestinoLista> {
                            val listaViewModel: ListaViewModel = viewModel(factory = factory)

                            ListaScreen(
                                viewModel = listaViewModel,
                                onNavigateToItems = { id, nombre ->
                                    navController.navigate(DestinoItems(listCod = id, nombreLista = nombre))
                                },
                                onNavigateToPapelera = {
                                    navController.navigate(DestinoPapelera)
                                }
                            )
                        }
                        // --- RUTA: Papelera ---
                        composable<DestinoPapelera> {
                            // Reutilizamos el Factory para inyectar el repositorio
                            val papeleraViewModel: PapeleraViewModel = viewModel(
                                factory = AppViewModelFactory(listaRepository)
                                // OJO: Tendrás que añadir el caso para PapeleraViewModel en tu AppViewModelFactory
                            )

                            PapeleraScreen(
                                viewModel = papeleraViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- RUTA 2: Pantalla de Ítems ---
                        composable<DestinoItems> { backStackEntry ->
                            // Recuperamos los argumentos seguros que enviamos
                            val args = backStackEntry.toRoute<DestinoItems>()

                            // Inyectamos AMBOS repositorios al Factory
                            val itemViewModel: ItemViewModel = viewModel (
                                factory = ItemViewModelFactory(
                                    itemRepository = itemRepository,
                                    listaRepository = listaRepository,
                                    listCod = args.listCod,
                                    nombreOriginal = args.nombreLista
                                )
                            )

                            ItemScreen(
                                viewModel = itemViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}