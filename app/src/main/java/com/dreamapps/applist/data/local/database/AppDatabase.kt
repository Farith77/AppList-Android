package com.dreamapps.applist.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dreamapps.applist.data.local.dao.ItemDao
import com.dreamapps.applist.data.local.dao.ListaDao
import com.dreamapps.applist.data.local.entity.ItemEntity
import com.dreamapps.applist.data.local.entity.ListaEntity

@Database(
    entities = [ListaEntity::class, ItemEntity::class], // Las tablas del diccionario de datos
    version = 1, // Control de versiones local
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Exponemos los DAOs para que el resto de la app pueda usarlos
    abstract fun listaDao(): ListaDao
    abstract fun itemDao(): ItemDao

    // --- Patrón Singleton (Estándar de Ingeniería) ---
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la devuelve. Si no, la crea de forma segura.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "applist_db" // El nombre del archivo SQLite en el celular
                )
                    // .fallbackToDestructiveMigration() // Útil en desarrollo si cambias las tablas
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}