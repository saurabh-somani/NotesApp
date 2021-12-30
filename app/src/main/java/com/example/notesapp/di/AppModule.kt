package com.example.notesapp.di

import android.content.Context
import androidx.room.Room
import com.example.notesapp.database.NotesDao
import com.example.notesapp.database.NotesDatabase
import com.example.notesapp.repository.NotesRepo
import com.example.notesapp.repository.NotesRepoImpl
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
}