package com.qnin.note.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotesScreen() {
    var search by remember { mutableStateOf("") }

    val notes = listOf(
        "Welcome to Qnin Note",
        "Shopping List",
        "Ideas",
        "Meeting Notes"
    )

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
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { }) {
            Text("New Note")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = note,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
