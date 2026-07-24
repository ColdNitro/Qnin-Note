package com.qnin.note.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotesScreen() {

    var search by remember { mutableStateOf("") }
    var newNoteTitle by remember { mutableStateOf("") }

    val notes = remember {
        mutableStateListOf("Welcome to Qnin Note")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Qnin Note",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newNoteTitle,
            onValueChange = { newNoteTitle = it },
            label = { Text("New note title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newNoteTitle.isNotBlank()) {
                    notes.add(newNoteTitle)
                    newNoteTitle = ""
                }
            }
        ) {
            Text("Create Note")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(notes) { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
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
