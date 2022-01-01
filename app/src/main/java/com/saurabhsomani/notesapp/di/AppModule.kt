package com.saurabhsomani.notesapp.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.saurabhsomani.notesapp.database.NotesDao
import com.saurabhsomani.notesapp.database.NotesDatabase
import com.saurabhsomani.notesapp.repository.NotesRepo
import com.saurabhsomani.notesapp.repository.NotesRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNotesDatabase(@ApplicationContext appContext: Context): NotesDatabase =
        Room.databaseBuilder(
            appContext,
            NotesDatabase::class.java,
            "notesDb"
        ).build()

    @Singleton
    @Provides
    fun provideNotesDao(notesDatabase: NotesDatabase): NotesDao = notesDatabase.notesDao()

    @Provides
    fun provideNotesRepo(notesRepoImpl: NotesRepoImpl): NotesRepo = notesRepoImpl

    @Provides
    fun provideFirebaseUser() = Firebase.auth.currentUser
}