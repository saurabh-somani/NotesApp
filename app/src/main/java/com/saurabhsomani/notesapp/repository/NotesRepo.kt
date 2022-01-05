package com.saurabhsomani.notesapp.repository

import com.saurabhsomani.notesapp.database.entities.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepo {

    fun getAllNotes(): Flow<List<Note>>

    suspend fun insertNote(note: Note): Long

    suspend fun insertAllNotes(notes: List<Note>): List<Long>

    suspend fun getNoteById(id: Long): Note?

    suspend fun updateNoteTitle(noteId: Long, title: String)

    suspend fun updateNoteDescription(noteId: Long, description: String)

    suspend fun deleteNote(noteId: Long)

    suspend fun deleteAllNotes()

    suspend fun updateNote(noteId: Long, title: String, description: String)

    fun getFlowNoteById(noteId: Long): Flow<Note?>
}