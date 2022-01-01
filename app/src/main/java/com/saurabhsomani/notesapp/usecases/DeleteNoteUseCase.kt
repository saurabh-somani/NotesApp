package com.saurabhsomani.notesapp.usecases

import com.saurabhsomani.notesapp.repository.NotesRepo
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val notesRepo: NotesRepo
) {

    suspend operator fun invoke(noteId: Long) {
        notesRepo.deleteNote(noteId)
    }

    suspend fun deleteAllNotes() {
        notesRepo.deleteAllNotes()
    }
}