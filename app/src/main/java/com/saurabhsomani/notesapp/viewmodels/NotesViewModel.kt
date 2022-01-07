package com.saurabhsomani.notesapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saurabhsomani.notesapp.database.entities.Note
import com.saurabhsomani.notesapp.network.NetworkUseCase
import com.saurabhsomani.notesapp.repository.NotesRepo
import com.saurabhsomani.notesapp.usecases.GetUsernameUseCase
import com.saurabhsomani.notesapp.util.formatNoteDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepo: NotesRepo,
    private val networkUseCase: NetworkUseCase,
    getUsernameUseCase: GetUsernameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState(username = getUsernameUseCase()))
    val uiState = _uiState.asStateFlow()


    init {
        loadNotesUiItems()
    }

    private fun loadNotesUiItems() {
        viewModelScope.launch {
            notesRepo.getAllNotes()
                .onEach { networkUseCase.uploadNotes(it) }
                .map { it.getNoteItems() }
                .collect { notesUiItems ->
                    _uiState.update { notesUiState ->
                        Log.d(TAG, "collectNotesUiItems: ")
                        notesUiState.copy(notesUiItems = notesUiItems)
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
            onClick = { onEvent(NotesEvent.OnNoteItemClick(note)) },
            onSwipe = { onEvent(NotesEvent.OnNoteItemSwipe(note)) }
        )
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.OnNoteItemClick -> {
                navigateToDetailScreen(event.note.id)
            }
            is NotesEvent.OnNoteItemSwipe -> {
                event.note.let { note ->
                    viewModelScope.launch {
                        notesRepo.deleteNote(note.id)
                        showSnackBar(note)
                    }
                }
            }
            is NotesEvent.OnFabClicked -> {
                Log.d(TAG, "onFabClicked: ")
                viewModelScope.launch {
                    val noteId = insertNote()
                    navigateToDetailScreen(noteId)
                }
            }
            is NotesEvent.OnSnackbarShown -> {
                Log.d(TAG, "onSnackBarDismiss: ")
                _uiState.update { notesUiState ->
                    val snackBarEvents = notesUiState.snackbarEvents
                        .filterNot { it.id == event.snackBarId }
                    notesUiState.copy(snackbarEvents = snackBarEvents)
                }
            }
            is NotesEvent.OnNavComplete -> {
                Log.d(TAG, "onNavComplete: ")
                _uiState.update { notesUiState ->
                    notesUiState.copy(navEvents = emptyList())
                }
            }
            is NotesEvent.OnNotesRefresh -> {
                viewModelScope.launch {
                    _uiState.update { notesUiState ->
                        notesUiState.copy(notesRefreshing = true)
                    }
                    notesRepo.downloadNotesToDb()
                    _uiState.update { notesUiState ->
                        notesUiState.copy(notesRefreshing = false)
                    }
                }
            }
        }
    }

    private fun showSnackBar(note: Note) {
        Log.d(TAG, "showSnackBar: $note")
        _uiState.update { notesUiState ->
            val snackBarEvents = notesUiState.snackbarEvents + NotesUiEvent.SnackbarEvent(note.id) {
                viewModelScope.launch {
                    insertNote(note)
                }
            }

            notesUiState.copy(snackbarEvents = snackBarEvents)
        }
    }

    private suspend fun insertNote(note: Note = Note()): Long {
        Log.d(TAG, "insertNote: ")
        return notesRepo.insertNote(note)
    }

    private fun navigateToDetailScreen(noteId: Long) {
        Log.d(TAG, "navigateToDetailScreen: ")
        _uiState.update { notesUiState ->
            Log.d(TAG, "navigateToDetailScreen: update: $notesUiState")
            val navEvents = notesUiState.navEvents + NotesUiEvent.NavEvent(noteId)
            notesUiState.copy(navEvents = navEvents)
        }
    }

    companion object {
        private const val TAG = "NotesViewModel"
    }

    // UiState data classes

    data class NotesUiState(
        val username: String,
        val notesUiItems: List<NoteItemUiState> = emptyList(),
        val navEvents: List<NotesUiEvent.NavEvent> = emptyList(),
        val snackbarEvents: List<NotesUiEvent.SnackbarEvent> = emptyList(),
        val notesRefreshing: Boolean = false
    )

    sealed class NotesEvent {
        data class OnNoteItemClick(val note: Note): NotesEvent()
        data class OnNoteItemSwipe(val note: Note): NotesEvent()
        data class OnSnackbarShown(val snackBarId: Long): NotesEvent()
        object OnFabClicked: NotesEvent()
        object OnNavComplete: NotesEvent()
        object OnNotesRefresh: NotesEvent()
    }

    sealed class NotesUiEvent {
        data class NavEvent(val noteId: Long): NotesUiEvent()
        data class SnackbarEvent(
            val id: Long,
            val messageResStr: String = "snackbar_on_item_delete_text",
            val actionTextResStr: String = "snackbar_on_item_delete_action_text",
            val onActionClick: () -> Unit
        ): NotesUiEvent()
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