package com.saurabhsomani.notesapp.usecases

import com.saurabhsomani.notesapp.database.entities.Note
import com.saurabhsomani.notesapp.repository.NotesRepo
import javax.inject.Inject

class InsertNoteUseCase @Inject constructor(
    private val notesRepo: NotesRepo
) {

    suspend operator fun invoke(note: Note): Long {
        return notesRepo.insertNote(note)
    }

    suspend operator fun invoke(notes: List<Note>): List<Long> {
        return notesRepo.insertAllNotes(notes)
    }
}