package com.example.notesapp.database

import androidx.room.*
import com.example.notesapp.database.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM Note ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Query("UPDATE Note SET title = :title WHERE id = :noteId")
    suspend fun updateNoteTitle(noteId: Int, title: String)

    @Query("UPDATE Note SET description = :description WHERE id = :noteId")
    suspend fun updateNoteDescription(noteId: Int, description: String)

    @Query("DELETE FROM Note WHERE id = :noteId")
    suspend fun deleteNote(noteId: Int)
}