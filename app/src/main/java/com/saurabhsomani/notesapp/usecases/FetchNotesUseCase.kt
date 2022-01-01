package com.saurabhsomani.notesapp.usecases

import com.saurabhsomani.notesapp.database.entities.Note
import com.saurabhsomani.notesapp.repository.NotesRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchNotesUseCase @Inject constructor(
    private val notesRepo: NotesRepo
) {

    fun getAllNotes(): Flow<List<Note>> {
        return notesRepo.getAllNotes()
    }

    suspend fun getNoteById(noteId: Long): Note? {
        return notesRepo.getNoteById(noteId)
    }
}