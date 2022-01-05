package com.saurabhsomani.notesapp.usecases

import com.saurabhsomani.notesapp.repository.NotesRepo
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val notesRepo: NotesRepo
) {

    suspend fun updateNoteTitle(noteId: Long, title: String) {
        notesRepo.updateNoteTitle(noteId, title)
    }

    suspend fun updateNoteDescription(noteId: Long, description: String) {
        notesRepo.updateNoteDescription(noteId, description)
    }

    suspend fun updateNote(noteId: Long, title: String, description: String) {
        notesRepo.updateNote(noteId, title, description)
    }
}