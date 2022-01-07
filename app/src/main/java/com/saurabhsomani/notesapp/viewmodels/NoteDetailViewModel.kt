package com.saurabhsomani.notesapp.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saurabhsomani.notesapp.database.entities.Note
import com.saurabhsomani.notesapp.repository.NotesRepo
import com.saurabhsomani.notesapp.util.formatNoteDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val notesRepo: NotesRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long = savedStateHandle.get("noteId")!!
    private val noteFlow: Flow<Note> = notesRepo.getFlowNoteById(noteId).filterNotNull()

    private val _uiState = MutableStateFlow(NoteDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            noteFlow.collect { note ->
                Log.d(TAG, "loadNote: $note")
                _uiState.update { noteDetailUiState ->
                    noteDetailUiState.copy(
                        isLoading = false,
                        title = note.title,
                        description = note.description,
                        timestamp = formatNoteDate(note.timestamp)
                    )
                }
            }
        }
    }

//    private fun loadNote() {
//        viewModelScope.launch {
//            val note = fetchNotesUseCase.getNoteById(noteId)
//            note?.let {
//                _uiState.update { noteDetailUiState ->
//                    noteDetailUiState.copy(
//                        isLoading = false,
//                        title = it.title,
//                        description = it.description,
//                        timestamp = formatNoteDate(it.timestamp)
//                    )
//                }
//            }
//        }
//    }

//    fun saveNoteTitle(text: String) {
//        viewModelScope.launch {
//            updateNoteUseCase.updateNoteTitle(noteId, text)
//        }
//    }
//
//    fun saveNoteDescription(text: String) {
//        viewModelScope.launch {
//            updateNoteUseCase.updateNoteDescription(noteId, text)
//        }
//    }

    fun saveNote() {
        viewModelScope.launch {
            _uiState.value.let {
                Log.d(TAG, "saveNote: $it")
                notesRepo.updateNote(noteId, it.title, it.description)
            }
            _uiState.update {
                it.copy(saveBtnVisible = false)
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update {
            it.copy(title = title, saveBtnVisible = true)
        }
    }

    fun updateDescription(description: String) {
        _uiState.update {
            it.copy(description = description, saveBtnVisible = true)
        }
    }

    companion object {
        private const val TAG = "NoteDetailViewModel"
    }

    data class NoteDetailUiState(
        val isLoading: Boolean = false,
        val title: String = "",
        val description: String = "",
        val timestamp: String = "",
        val saveBtnVisible: Boolean = false
    )
}