package com.saurabhsomani.notesapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String = "",
    var description: String = "",
    var timestamp: Long = System.currentTimeMillis()
)
