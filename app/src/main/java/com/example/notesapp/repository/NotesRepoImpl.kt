package com.example.notesapp.repository

import com.example.notesapp.database.NotesDao
import com.example.notesapp.database.entities.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepoImpl @Inject constructor(
    private val notesDao: NotesDao
) : NotesRepo {

    override fun getAllNotes(): Flow<List<Note>> {
        return notesDao.getAllNotes()
    }

    override suspend fun insertNote(note: Note): Long {
        return notesDao.insertNote(note)
    }

    override suspend fun getNoteById(id: Int): Note? {
        return notesDao.getNoteById(id)
    }

    override suspend fun updateNoteTitle(noteId: Int, title: String) {
        notesDao.updateNoteTitle(noteId, title)
    }

    override suspend fun updateNoteDescription(noteId: Int, description: String) {
        notesDao.updateNoteDescription(noteId, description)
    }

    override suspend fun deleteNote(noteId: Int) {
        notesDao.deleteNote(noteId)
    }
}