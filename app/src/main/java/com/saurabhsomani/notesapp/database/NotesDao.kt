package com.saurabhsomani.notesapp.database

import androidx.room.*
import com.saurabhsomani.notesapp.database.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM Note ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotes(notes: List<Note>): List<Long>

    @Query("UPDATE Note SET title = :title WHERE id = :noteId")
    suspend fun updateNoteTitle(noteId: Long, title: String)

    @Query("UPDATE Note SET description = :description WHERE id = :noteId")
    suspend fun updateNoteDescription(noteId: Long, description: String)

    @Query("DELETE FROM Note WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)

    @Query("DELETE FROM Note")
    suspend fun deleteAllNotes()

    @Query("UPDATE Note SET title = :title, description = :description WHERE id = :noteId")
    suspend fun updateNote(noteId: Long, title: String, description: String)

    @Query("SELECT * FROM Note WHERE id = :noteId")
    fun getFlowNoteById(noteId: Long): Flow<Note?>
}