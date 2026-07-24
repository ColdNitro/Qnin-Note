package com.qnin.note.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotesScreen() {
    var search by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Qnin Note",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search notes") },
            modifier = Modifier.fillMaxSize(fraction = 1f)
        )

        Button(onClick = { }) {
            Text("New Note")
        }

        Text(
            text = "No notes yet.\nCreate your first note."
        )
    }
}
