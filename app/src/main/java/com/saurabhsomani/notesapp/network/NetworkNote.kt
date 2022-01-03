package com.saurabhsomani.notesapp.network

import com.google.firebase.firestore.Exclude
import com.saurabhsomani.notesapp.database.entities.Note

class NetworkNote(
    @get:Exclude var id: Long = 0,
    val title: String = "",
    val description: String = "",
    val timestamp: Long = 0
)

fun NetworkNote.toNote(): Note = Note(
    id = id,
    title = title,
    description = description,
    timestamp = timestamp
)

fun Note.toNetworkNote() = NetworkNote(
    id = id,
    title = title,
    description = description,
    timestamp = timestamp
)
