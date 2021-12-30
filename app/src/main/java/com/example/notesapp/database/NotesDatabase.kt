package com.example.notesapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notesapp.database.entities.Note

@Database(entities = [Note::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao
}