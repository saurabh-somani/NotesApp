package com.saurabhsomani.notesapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saurabhsomani.notesapp.util.formatNoteDate
import com.saurabhsomani.notesapp.database.entities.Note
import com.saurabhsomani.notesapp.usecases.DeleteNoteUseCase
import com.saurabhsomani.notesapp.usecases.FetchNotesUseCase
import com.saurabhsomani.notesapp.usecases.InsertNoteUseCase
import com.saurabhsomani.notesapp.usecases.GetUsernameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val fetchNotesUseCase: FetchNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val insertNoteUseCase: InsertNoteUseCase,
    getUsernameUseCase: GetUsernameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState(getUsernameUseCase()))
    val uiState = _uiState.asStateFlow()

    init {
        collectNotesUiItems()
    }

    private fun collectNotesUiItems() {
        viewModelScope.launch {
            fetchNotesUseCase.getAllNotes()
                .map { it.getNoteItems() }
                .collect { noteItems ->
                    _uiState.update { notesUiState ->
                        Log.d(TAG, "collectNotesUiItems: ")
                        notesUiState.copy(notesUiItems = noteItems)
                    }
                }
        }
    }

    private fun List<Note>.getNoteItems() = map { note ->
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

    private fun showSnackBar(note: Note) {
        Log.d(TAG, "showSnackBar: $note")
        _uiState.update { notesUiState ->
            val snackBarEvents = notesUiState.snackBarEvents + NotesEvent.SnackBarEvent(
                id = note.id,
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

    private fun deleteItem(noteId: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
        }
    }

    fun onFabClicked() {
        Log.d(TAG, "onFabClicked: ")
        viewModelScope.launch {
            val noteId = insertNote()
            navigateToDetailScreen(noteId)
        }
    }

    private suspend fun insertNote(note: Note = Note()): Long {
        Log.d(TAG, "insertNote: ")
        return insertNoteUseCase(note)
    }

    private fun navigateToDetailScreen(noteId: Long) {
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

    // UiState data classes

    data class NotesUiState(
        val username: String,
        val notesUiItems: List<NoteItemUiState> = emptyList(),
        val navEvents: List<NotesEvent.NavEvent> = emptyList(),
        val snackBarEvents: List<NotesEvent.SnackBarEvent> = emptyList()
    )

    sealed class NotesEvent {
        data class NavEvent(val noteId: Long): NotesEvent()
        data class SnackBarEvent(
            val id: Long,
            val messageResStr: String,
            val actionTextResStr: String,
            val onActionClick: () -> Unit
        ): NotesEvent()
    }

    data class NoteItemUiState(
        val id: Long,
        val title: String,
        val description: String,
        val timestamp: String,
        val onClick: () -> Unit,
        val onSwipe: () -> Unit
    )
}