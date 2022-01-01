package com.saurabhsomani.notesapp.network

import com.saurabhsomani.notesapp.database.entities.Note

class NetworkNotes() {
    var networkNotesList: List<NetworkNote> = emptyList()

    constructor(networkNotesList: List<NetworkNote>) : this() {
        this.networkNotesList = networkNotesList
    }
}

class NetworkNote() {
    var id: Long = 0
    var title: String = ""
    var description: String = ""
    var timestamp: Long = 0

    constructor(id: Long, title: String, description: String, timestamp: Long) : this() {
        this.id = id
        this.title = title
        this.description = description
        this.timestamp = timestamp
    }
}

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
