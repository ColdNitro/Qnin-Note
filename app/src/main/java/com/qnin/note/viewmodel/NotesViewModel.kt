package com.qnin.note.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class NotesViewModel : ViewModel() {

    val notes = mutableStateListOf(
        "Welcome to Qnin Note"
    )

    fun addNote(title: String) {
        if (title.isNotBlank()) {
            notes.add(title)
        }
    }
}
