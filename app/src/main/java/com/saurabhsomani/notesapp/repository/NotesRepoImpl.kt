package com.saurabhsomani.notesapp.repository

import com.saurabhsomani.notesapp.database.NotesDao
import com.saurabhsomani.notesapp.database.entities.Note
import com.saurabhsomani.notesapp.di.IoDispatcher
import com.saurabhsomani.notesapp.network.NetworkUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotesRepoImpl @Inject constructor(
    private val notesDao: NotesDao,
    private val networkUseCase: NetworkUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NotesRepo {

    override fun getAllNotes(): Flow<List<Note>> = flow {
        downloadNotesToDb()
        emitAll(notesDao.getAllNotes())
    }

    override suspend fun downloadNotesToDb() = withContext(ioDispatcher) {
        val notes = networkUseCase.downloadNotes()
        if (notes.isNotEmpty()) {
            insertAllNotes(notes)
        }
    }

    override suspend fun insertNote(note: Note): Long {
        return notesDao.insertNote(note)
    }

    override suspend fun insertAllNotes(notes: List<Note>): List<Long> {
        return notesDao.insertAllNotes(notes)
    }

    override suspend fun deleteAllNotes() {
        notesDao.deleteAllNotes()
    }

    override suspend fun updateNote(noteId: Long, title: String, description: String) {
        notesDao.updateNote(noteId, title, description)
    }

    override fun getFlowNoteById(noteId: Long): Flow<Note?> {
        return notesDao.getFlowNoteById(noteId)
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