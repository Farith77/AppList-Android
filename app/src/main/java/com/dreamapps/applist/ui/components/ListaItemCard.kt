package com.dreamapps.applist.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamapps.applist.data.local.entity.ListaEntity

@Composable
fun ListaItemCard(
    lista: ListaEntity,
    estaSeleccionada: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (estaSeleccionada) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionada) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = lista.listName, style = MaterialTheme.typography.titleLarge)

            if (!lista.listDescription.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = lista.listDescription, style = MaterialTheme.typography.bodyMedium)
            }

            // Opciones de Edición/Eliminación que aparecen solo si está seleccionada
            AnimatedVisibility(visible = estaSeleccionada) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onOpen) { Text("Ver Ítems") }
                    IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, "Editar") }
                    IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}