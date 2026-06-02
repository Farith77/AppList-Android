package com.dreamapps.applist.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun ItemRow(
    itemText: String,
    modifier: Modifier = Modifier
) {
    // Estado para saber si el usuario está tocando el ítem
    var estaPresionado by remember { mutableStateOf(false) }

    // Lógica del borde dinámico
    /**
     * val modificadorBorde = if (estaPresionado) {
     *         Modifier.border(2.dp, Color(0xFF5364FF), RoundedCornerShape(8.dp))
     *     } else {
     *         // Un borde invisible para que el ítem no "salte" o cambie de tamaño al presionarlo
     *         Modifier.border(2.dp, Color.Transparent, RoundedCornerShape(8.dp))
     *     }
     */

    // Borde estatico
    val modificadorBorde = Modifier.border(1.dp, Color(0xFF7D9BFF), RoundedCornerShape(8.dp))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(modificadorBorde) // Aplicamos el borde
            .padding(16.dp) // El padding interno (el aire dentro de la caja)
    ) {
        Text(text = itemText, style = MaterialTheme.typography.bodyLarge)
    }
}