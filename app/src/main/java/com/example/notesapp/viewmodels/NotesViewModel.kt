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
import java.util.*
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
                    showSnackBar(note)
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    private fun showSnackBar(note: Note) {
        Log.d(TAG, "showSnackBar: $note")
        _uiState.update { notesUiState ->
            val snackBarEvents = notesUiState.snackBarEvents + NotesEvent.SnackBarEvent(
                id = note.id.toLong(),
                messageResStr = "snackbar_on_item_delete_text",
                actionTextResStr = "snackbar_on_item_delete_action_text"
            ) {
                viewModelScope.launch {
                    insertNote(note)
                }
            }

            notesUiState.copy(snackBarEvents = snackBarEvents)
        }
    }

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

    private suspend fun insertNote(note: Note = Note()): Int {
        Log.d(TAG, "insertNote: ")
        return notesRepo.insertNote(note).toInt()
    }

    private fun navigateToDetailScreen(noteId: Int) {
        Log.d(TAG, "navigateToDetailScreen: ")
        _uiState.update { notesUiState ->
            Log.d(TAG, "navigateToDetailScreen: update: $notesUiState")
            val navEvents = notesUiState.navEvents + NotesEvent.NavEvent(noteId)
            notesUiState.copy(navEvents = navEvents)
        }
    }

    fun onNavComplete() {
        Log.d(TAG, "onNavComplete: ")
        _uiState.update { notesUiState ->
            notesUiState.copy(navEvents = emptyList())
        }
    }

    fun onSnackBarShown(snackBarId: Long) {
        Log.d(TAG, "onSnackBarDismiss: ")
        _uiState.update { notesUiState ->
            val snackBarEvents = notesUiState.snackBarEvents.filterNot { it.id == snackBarId }
            notesUiState.copy(snackBarEvents = snackBarEvents)
        }
    }

    companion object {
        private const val TAG = "NotesViewModel"
    }
}

data class NotesUiState(
    val navEvents: List<NotesEvent.NavEvent> = emptyList(),
    val snackBarEvents: List<NotesEvent.SnackBarEvent> = emptyList()
)

sealed class NotesEvent {
    data class NavEvent(val noteId: Int): NotesEvent()
    data class SnackBarEvent(
        val id: Long,
        val messageResStr: String,
        val actionTextResStr: String,
        val onActionClick: () -> Unit
    ): NotesEvent()
}

data class NoteItemUiState(
    val id: Int,
    val title: String,
    val description: String,
    val timestamp: String,
    val onClick: () -> Unit,
    val onSwipe: () -> Unit
)