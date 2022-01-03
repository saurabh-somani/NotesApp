package com.saurabhsomani.notesapp.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saurabhsomani.notesapp.usecases.FetchNotesUseCase
import com.saurabhsomani.notesapp.usecases.UpdateNoteUseCase
import com.saurabhsomani.notesapp.util.formatNoteDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val fetchNotesUseCase: FetchNotesUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteId: Long = savedStateHandle.get("noteId")!!

    private val _uiState = MutableStateFlow(NoteDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            val note = fetchNotesUseCase.getNoteById(noteId)
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
            updateNoteUseCase.updateNoteTitle(noteId, text)
        }
    }

    fun saveNoteDescription(text: String) {
        viewModelScope.launch {
            updateNoteUseCase.updateNoteDescription(noteId, text)
        }
    }

    data class NoteDetailUiState(
        val isLoading: Boolean = false,
        val title: String = "",
        val description: String = "",
        val timestamp: String = ""
    )
}