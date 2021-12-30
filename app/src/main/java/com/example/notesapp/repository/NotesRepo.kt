package com.example.notesapp.repository

import com.example.notesapp.database.entities.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepo {

    fun getAllNotes(): Flow<List<Note>>

    suspend fun insertNote(note: Note): Long

    suspend fun getNoteById(id: Int): Note?

    suspend fun updateNoteTitle(noteId: Int, title: String)

    suspend fun updateNoteDescription(noteId: Int, description: String)

    suspend fun deleteNote(noteId: Int)
}