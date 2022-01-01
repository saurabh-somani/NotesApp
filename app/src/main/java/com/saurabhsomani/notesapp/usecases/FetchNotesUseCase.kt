package com.saurabhsomani.notesapp.usecases

import com.saurabhsomani.notesapp.database.entities.Note
import com.saurabhsomani.notesapp.network.NetworkUseCase
import com.saurabhsomani.notesapp.repository.NotesRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchNotesUseCase @Inject constructor(
    private val notesRepo: NotesRepo,
    private val networkUseCase: NetworkUseCase
) {

    fun getAllNotes(): Flow<List<Note>> = flow {
        val notes = networkUseCase.downloadNotes()
        if (notes.isNotEmpty()) {
            notesRepo.insertAllNotes(notes)
        }
        emitAll(notesRepo.getAllNotes())
    }

    suspend fun getNoteById(noteId: Long): Note? {
        return notesRepo.getNoteById(noteId)
    }
}