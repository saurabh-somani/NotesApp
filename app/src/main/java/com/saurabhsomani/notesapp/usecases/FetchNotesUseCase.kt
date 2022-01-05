package com.saurabhsomani.notesapp.usecases

import com.saurabhsomani.notesapp.database.entities.Note
import com.saurabhsomani.notesapp.di.IoDispatcher
import com.saurabhsomani.notesapp.network.NetworkUseCase
import com.saurabhsomani.notesapp.repository.NotesRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchNotesUseCase @Inject constructor(
    private val notesRepo: NotesRepo,
    private val networkUseCase: NetworkUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    fun getAllNotes(): Flow<List<Note>> = flow {
        downloadNotesToDb()
        emitAll(notesRepo.getAllNotes())
    }

    suspend fun downloadNotesToDb() = withContext(ioDispatcher) {
        val notes = networkUseCase.downloadNotes()
        if (notes.isNotEmpty()) {
            notesRepo.insertAllNotes(notes)
        }
    }

    suspend fun getNoteById(noteId: Long): Note? {
        return notesRepo.getNoteById(noteId)
    }

    fun getFlowNoteById(noteId: Long): Flow<Note?> {
        return notesRepo.getFlowNoteById(noteId)
    }
}