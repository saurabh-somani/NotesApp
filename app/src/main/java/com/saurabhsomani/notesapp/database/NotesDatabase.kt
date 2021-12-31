package com.saurabhsomani.notesapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saurabhsomani.notesapp.database.entities.Note

@Database(entities = [Note::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao
}