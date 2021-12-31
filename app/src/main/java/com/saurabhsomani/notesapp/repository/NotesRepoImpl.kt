package com.saurabhsomani.notesapp.repository

import com.saurabhsomani.notesapp.database.NotesDao
import com.saurabhsomani.notesapp.database.entities.Note
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

    override suspend fun getNoteById(id: Long): Note? {
        return notesDao.getNoteById(id)
    }

    override suspend fun updateNoteTitle(noteId: Long, title: String) {
        notesDao.updateNoteTitle(noteId, title)
    }

    override suspend fun updateNoteDescription(noteId: Long, description: String) {
        notesDao.updateNoteDescription(noteId, description)
    }

    override suspend fun deleteNote(noteId: Long) {
        notesDao.deleteNote(noteId)
    }
}