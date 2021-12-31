package com.example.notesapp.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.repository.NotesRepo
import com.example.notesapp.util.formatNoteDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val notesRepo: NotesRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Int = savedStateHandle.get("noteId")!!

    private val _uiState = MutableStateFlow(NoteDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            val note = notesRepo.getNoteById(noteId)
            note?.let {
                _uiState.update { noteDetailUiState ->
                    noteDetailUiState.copy(
                        isLoading = false,
                        title = it.title,
                        description = it.description,
                        timestamp = formatNoteDate(it.timestamp)
                    )
                }
            }
        }
    }

    fun saveNoteTitle(text: String) {
        viewModelScope.launch {
            notesRepo.updateNoteTitle(noteId, text)
        }
    }

    fun saveNoteDescription(text: String) {
        viewModelScope.launch {
            notesRepo.updateNoteDescription(noteId, text)
        }
    }
}

data class NoteDetailUiState(
    val isLoading: Boolean = false,
    val title: String = "",
    val description: String = "",
    val timestamp: String = ""
)