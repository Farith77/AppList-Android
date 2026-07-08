package com.dreamapps.applist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawerSheet(
    rutaActual: String = "listas", // Para resaltar qué pantalla está activa
    onNavigateToListas: () -> Unit,
    onNavigateToPapelera: () -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp), // Ancho estándar del menú
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // ==========================================
        // 1. CABECERA DEL MENÚ (Perfil / Marca)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            Column {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "DreamApps List",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Gestión inteligente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 2. OPCIONES DE NAVEGACIÓN
        // ==========================================

        // OPCIÓN: MIS LISTAS (Pantalla principal)
        NavigationDrawerItem(
            label = { Text("Mis Listas") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            selected = rutaActual == "listas",
            onClick = {
                onNavigateToListas()
                onClose()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // OPCIÓN: PAPELERA (¡Para ver el eliminado lógico!)
        NavigationDrawerItem(
            label = { Text("Papelera") },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            selected = rutaActual == "papelera",
            onClick = {
                onNavigateToPapelera()
                onClose()
            },
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.error,
                unselectedTextColor = MaterialTheme.colorScheme.error
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // OPCIÓN: CONFIGURACIONES (Para el futuro)
        NavigationDrawerItem(
            label = { Text("Configuración") },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            selected = false,
            onClick = {
                /* TODO: Implementar pantalla de configuración en el futuro */
                onClose()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}