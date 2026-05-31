package com.dreamapps.applist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomSearchBar(
    textoBusqueda: String,
    onTextoCambio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onTextoCambio,
            placeholder = { Text("busca entre tus items", color = Color.Gray) },
            trailingIcon = {
                if (textoBusqueda.isNotEmpty()) {
                    IconButton(onClick = { onTextoCambio("") }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Limpiar búsqueda", tint = Color.Black)
                    }
                } else {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = Color.Gray)
                }
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFE0E0E0),
                // SOLUCIÓN: Forzamos el texto y cursor a negro sin importar el tema del celular
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}