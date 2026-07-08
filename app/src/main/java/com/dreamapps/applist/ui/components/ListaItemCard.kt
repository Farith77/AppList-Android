package com.dreamapps.applist.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dreamapps.applist.data.local.entity.ListaEntity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListaItemCard(
    lista: ListaEntity,
    estaSeleccionada: Boolean,
    modoSeleccionActivo: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit // NUEVO: Para el toque prolongado
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // NUEVO: Reemplazamos .clickable por .combinedClickable
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        // Conservamos tu lógica de elevación y colores
        elevation = CardDefaults.cardElevation(defaultElevation = if (estaSeleccionada) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaSeleccionada) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        // Usamos un Row para poner el Checkbox a la izquierda de los textos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // EL CHECKBOX (Solo aparece si mantuviste presionado alguna lista)
            if (modoSeleccionActivo) {
                Checkbox(
                    checked = estaSeleccionada,
                    // Si tocan el checkbox, actúa igual que si tocaran la tarjeta
                    onCheckedChange = { onClick() },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            // COLUMNA CENTRAL (Conservamos tu título y descripción original)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lista.listName,
                    style = MaterialTheme.typography.titleLarge
                )

                if (!lista.listDescription.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lista.listDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}