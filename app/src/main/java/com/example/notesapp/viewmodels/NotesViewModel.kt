package com.example.notesapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.util.formatNoteDate
import com.example.notesapp.database.entities.Note
import com.example.notesapp.repository.NotesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepo: NotesRepo
) : ViewModel() {

    val notesUiItems = notesRepo.getAllNotes().map { notes ->
        notes.map { note ->
            NoteItemUiState(
                id = note.id,
                title = note.title,
                description = note.description,
                timestamp = formatNoteDate(note.timestamp),
                onClick = {
                    navigateToDetailScreen(note.id)
                },
                onSwipe = {
                    deleteItem(note.id)
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    private fun deleteItem(noteId: Int) {
        viewModelScope.launch {
            notesRepo.deleteNote(noteId)
        }
    }

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState = _uiState.asStateFlow()

    fun onFabClicked() {
        Log.d(TAG, "onFabClicked: ")
        viewModelScope.launch {
            val noteId = insertNote()
            navigateToDetailScreen(noteId)
        }
    }

    fun onItemSwiped(noteId: Int) {
        viewModelScope.launch {
            notesRepo.deleteNote(noteId)
        }
    }

    private suspend fun insertNote(): Int {
        Log.d(TAG, "insertNote: ")
        return notesRepo.insertNote(Note()).toInt()
    }

    private fun navigateToDetailScreen(noteId: Int) {
        Log.d(TAG, "navigateToDetailScreen: ")
        _uiState.update { notesUiState ->
            Log.d(TAG, "navigateToDetailScreen: update: $notesUiState")
            val navEvents = notesUiState.navEvents + NavEvent(noteId)
            notesUiState.copy(navEvents = navEvents)
        }
    }

    fun onNavComplete() {
        Log.d(TAG, "onNavComplete: ")
        _uiState.update { notesUiState ->
            notesUiState.copy(navEvents = emptyList())
        }
    }

    companion object {
        private const val TAG = "NotesViewModel"
    }
}

data class NotesUiState(
    val navEvents: List<NavEvent> = emptyList()
)

data class NavEvent(
    val noteId: Int
)

data class NoteItemUiState(
    val id: Int,
    val title: String,
    val description: String,
    val timestamp: String,
    val onClick: () -> Unit,
    val onSwipe: () -> Unit
)